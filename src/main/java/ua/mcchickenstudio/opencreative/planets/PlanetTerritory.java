/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.planets;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeScript;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetLoadEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetUnloadEvent;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import ua.mcchickenstudio.opencreative.utils.world.generators.EnvironmentCapable;
import ua.mcchickenstudio.opencreative.utils.world.generators.StructuresCapable;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerator;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerators;

import java.lang.reflect.Method;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlanetConfig;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.clearOnceMessages;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>PlanetTerritory</h1>
 * This class represents a territory, where planet players can build or play.
 * It stores one-time information that will be removed on world unloading.
 */
public class PlanetTerritory {

    private final Planet planet;
    private final PlanetFlags flags;
    private final PlanetScoreboards scoreboards;
    private final PlanetRecipes recipes;

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Set<BukkitRunnable> runningBukkitRunnables = Collections.newSetFromMap(new IdentityHashMap<>());

    private final CodeScript script;
    private Location spawnLocation = null;
    private String generator = "";
    private int worldSize = 25;
    private World.Environment environment;
    private String biome;
    private boolean autoSave = true;
    private boolean busy = false;
    private boolean ignoreUnloading = false;

    public PlanetTerritory(@NotNull Planet planet) {
        this.planet = planet;
        flags = new PlanetFlags(planet);
        scoreboards = new PlanetScoreboards(planet);
        script = new CodeScript(planet);
        recipes = new PlanetRecipes(planet);
        loadInformation();
    }

    /**
     * Resets custom world size to owner's group world size.
     */
    public void resetWorldSize() {
        int worldSize = planet.getGroup().getWorldSize();
        FileUtils.removePlanetConfigParameter(planet, "size");
        if (this.worldSize == worldSize) return;
        this.worldSize = worldSize;
        if (getWorld() != null) {
            getWorld().getWorldBorder().setSize(worldSize);
            for (Player player : planet.getPlayers()) {
                showBorders(player);
            }
        }
    }

    /**
     * Sets custom size of world borders.
     *
     * @param size new size of world.
     * @param save whether ignore size from owner's group on next world load and use specified.
     */
    public void setWorldSize(int size, boolean save) {
        if (this.worldSize == size) return;
        if (size < 0) return;
        this.worldSize = size;
        if (getWorld() != null) {
            getWorld().getWorldBorder().setSize(size);
            for (Player player : planet.getPlayers()) {
                showBorders(player);
            }
        }
        if (save) FileUtils.setPlanetConfigParameter(planet, "size", size);
    }

    private void loadInformation() {
        FileConfiguration config = getPlanetConfig(planet);
        World.Environment environment = World.Environment.NORMAL;
        if (config.getString("environment") != null) {
            try {
                environment = World.Environment.valueOf(config.getString("environment"));
            } catch (Exception ignored) {}
        }
        worldSize = config.getInt("size", planet.getGroup().getWorldSize());
        autoSave = config.getBoolean("autosave", true);
        biome = config.getString("biome", "");
        this.generator = config.getString("generator", "");
        this.environment = environment;
    }

    /**
     * Loads planet's files into worlds directory, loads and setups build world, loads script and variables.
     */
    public synchronized void load() {
        long startTime = System.currentTimeMillis();

        loadInformation();
        flags.loadFlags();
        planet.getWorldPlayers().loadPlayers();

        ConfigurationSection spawnSection = getPlanetConfig(planet).getConfigurationSection("spawn");
        if (spawnSection != null) {
            double x = spawnSection.getDouble("x", 0);
            double y = spawnSection.getDouble("y", 0);
            double z = spawnSection.getDouble("z", 0);
            float yaw = (float) spawnSection.getDouble("yaw", 0);
            float pitch = (float) spawnSection.getDouble("pitch", 0);
            spawnLocation = new Location(null, x, y, z, yaw, pitch);
        }

        WorldGenerator worldGenerator = WorldGenerators.getInstance().getById(generator);
        WorldCreator creator = new WorldCreator(planet.getWorldName())
                .environment(planet.getTerritory().getEnvironment())
                .keepSpawnLoaded(TriState.FALSE);
        if (worldGenerator != null) {
            worldGenerator.modifyWorldCreator(creator, biome);
        }
        World world = creator.createWorld();
        if (world == null) return;
        world.setAutoSave(autoSave);
        setGameRuleIfExists("spawn_chunk_radius", 1);
        setGameRuleIfExists("command_blocks_work", false);
        world.setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        if (world.getEnvironment() == World.Environment.THE_END) {
            if (world.getEnderDragonBattle() != null) {
                world.getEnderDragonBattle().setPreviouslyKilled(true);
                world.getEnderDragonBattle().getBossBar().setVisible(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof EnderDragon dragon) {
                            dragon.setHealth(0);
                        }
                    }
                }
            }.runTaskLater(OpenCreative.getPlugin(), 10L);
        }
        planet.setLastActivityTime(System.currentTimeMillis());
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.getWorldBorder().setSize(worldSize);
        new PlanetLoadEvent(planet).callEvent();

        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " loaded in " + (endTime - startTime) + " ms");
    }

    /**
     * Saves planet's data and unloads planet's build and dev world.
     */
    public synchronized void unload() {
        if (ignoreUnloading) return;
        if (OpenCreative.getPlugin().isEnabled()) {
            Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                handleUnloadProcess(true);
            }, 5);
        } else {
            handleUnloadProcess(false);
        }
    }

    /**
     * Saves planet's data and unloads planet's build and dev world.
     */
    private void handleUnloadProcess(boolean asyncSaveData) {
        long startTime = System.currentTimeMillis();
        if (!planet.isLoaded()) {
            if (planet.getDevPlanet().isLoaded()) {
                planet.getDevPlanet().unload(asyncSaveData);
                long endTime = System.currentTimeMillis();
                OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " unloaded only dev in " + (endTime - startTime) + " ms");
            }
            return;
        }

        World world = getWorld();
        if (world != null) {
            for (Entity entity : world.getEntitiesByClass(Item.class)) {
                if (entity instanceof Item item) {
                    item.setItemStack(ItemUtils.fixItem(item.getItemStack()));
                }
            }
        }
        for (Player player : planet.getPlayers()) {
            new QuitEvent(player).callEvent();
        }
        if (asyncSaveData) {
            Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), this::saveData);
        } else {
            this.saveData();
        }
        if (world != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(world)) {
                    teleportToLobby(player);
                }
            }
            if (asyncSaveData) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    world.unloadChunk(chunk.getX(), chunk.getZ(), autoSave);
                }
                try {
                    // 1.21+ Content:
                    Method saveMethod = world.getClass().getMethod("save", boolean.class);
                    saveMethod.invoke(world, false);
                } catch (Exception ignored) {
                    world.save();
                }
                busy = true;
                Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                    Bukkit.unloadWorld(planet.getWorldName(), false);
                    busy = false;
                }, 60);
            } else {
                Bukkit.unloadWorld(planet.getWorldName(), autoSave);
            }
        }

        if (planet.getDevPlanet().isLoaded()) {
            planet.getDevPlanet().unload(asyncSaveData);
        }
        new PlanetUnloadEvent(planet).callEvent();

        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " unloaded in " + (endTime - startTime) + " ms");
    }

    private void saveData() {
        planet.setLastActivityTime(System.currentTimeMillis());
        FileUtils.setPlanetConfigParameter(planet, "environment", planet.getTerritory().getEnvironment().name());
        planet.getVariables().save();
        clearData();
    }

    /**
     * Clears temporary information stored in memory.
     * Used on world unload.
     */
    public void clearData() {
        stopBukkitRunnables();
        bossBars.clear();
        scoreboards.clear();
        flags.clear();
        script.getExecutors().clear();
        planet.getVariables().clearVariables();
        planet.getWorldPlayers().clear();
        planet.getLimits().clear();
        script.unload();
        spawnLocation = null;
        clearOnceMessages(planet);
        recipes.clear();
    }

    public void addBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.add(runnable);
    }

    public void scheduleRunnable(@NotNull PlanetRunnable runnable, long delay) {
        runningBukkitRunnables.add(runnable);
        runnable.runTaskLater(OpenCreative.getPlugin(), delay);
    }

    public void scheduleAsyncRunnable(@NotNull PlanetRunnable runnable, long delay) {
        runningBukkitRunnables.add(runnable);
        runnable.runTaskLaterAsynchronously(OpenCreative.getPlugin(), delay);
    }

    public void removeBukkitRunnable(BukkitRunnable runnable) {
        runningBukkitRunnables.remove(runnable);
    }

    /**
     * Stops all running bukkit runnables and tasks in world.
     */
    public void stopBukkitRunnables() {
        for (BukkitRunnable runnable : new HashSet<>(runningBukkitRunnables)) {
            try {
                if (runnable != null && !runnable.isCancelled()) {
                    runnable.cancel();
                }
            } catch (IllegalStateException ignored) {
            }
        }
        runningBukkitRunnables.clear();
    }

    /**
     * Returns flags of planet, that store additional settings.
     *
     * @return planet's flags.
     */
    public PlanetFlags getFlags() {
        return flags;
    }

    /**
     * Returns map of IDs and boss bars.
     *
     * @return map of IDs and boss bars.
     */
    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    /**
     * Returns world of planet for buildings.
     * If world is unloaded, returns null.
     *
     * @return planet's world, or null - if world is unloaded.
     */
    public @Nullable World getWorld() {
        return Bukkit.getWorld(planet.getWorldName());
    }

    /**
     * Returns size of world, that will be used
     * to set world borders.
     *
     * @return size of world.
     */
    public int getWorldSize() {
        return worldSize;
    }

    /**
     * Returns code script of world, that stores
     * executors and actions.
     *
     * @return code script.
     */
    public @NotNull CodeScript getScript() {
        return script;
    }

    public @Nullable World generateWorld(WorldGenerator generator, World.Environment environment,
                                         long seed, boolean generateStructures, String biome) {

        WorldCreator worldCreator = new WorldCreator(planet.getWorldName());
        if (generator instanceof StructuresCapable) {
            worldCreator.generateStructures(generateStructures);
        }
        worldCreator.type(WorldType.FLAT);
        if (generator instanceof EnvironmentCapable) {
            worldCreator.environment(environment);
        }
        worldCreator.seed(seed);

        generator.modifyWorldCreator(worldCreator, biome);

        worldCreator.keepSpawnLoaded(TriState.FALSE);
        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            world.setAutoSave(true);
            setGameRuleIfExists("spawn_chunk_radius", 1);
            world.getWorldBorder().setSize(getWorldSize());

            world.setGameRule(GameRule.DO_MOB_LOOT, true);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
            world.setGameRule(GameRule.MOB_GRIEFING, true);
            world.setGameRule(GameRule.NATURAL_REGENERATION, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false);

            world.setTime(0);
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) entity.remove();
            }

            generator.afterCreation(world);

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof EnderDragon dragon) {
                            dragon.setHealth(0);
                        }
                    }
                }
            };
            runnable.runTaskLater(OpenCreative.getPlugin(), 10L);

            return world;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void setGameRuleIfExists(@NotNull String gameRule, boolean value) {
        try {
            GameRule<?> rule = GameRule.getByName(gameRule.toLowerCase());
            if (rule != null && getWorld() != null) {
                getWorld().setGameRule((GameRule<? super Boolean>) rule, value);
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public void setGameRuleIfExists(@NotNull String gameRule, int value) {
        try {
            GameRule<?> rule = GameRule.getByName(gameRule.toLowerCase());
            if (rule != null && getWorld() != null) {
                getWorld().setGameRule((GameRule<? super Integer>) rule, value);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Shows custom world borders for player.
     *
     * @param player player to show.
     */
    public void showBorders(@NotNull Player player) {
        if (isEntityInDevPlanet(player)) return;
        WorldBorder border = Bukkit.createWorldBorder();
        border.setSize(player.getWorld().getWorldBorder().getSize());
        if (planet.getMode() == Planet.Mode.PLAYING) {
            PlanetPlayer planetPlayer = planet.getWorldPlayers().getPlanetPlayer(player);
            if (planetPlayer != null && planetPlayer.getWorldSize() != null) {
                border.setSize(planetPlayer.getWorldSize());
            }
        }
        switch (planet.getFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS)) {
            case 1 -> border.setSize(border.getSize()); // Default
            case 2 -> border.setSize(border.getSize() + 0.001, 3600); // Green
            case 3 -> {
                border.setSize(border.getSize() + 0.1);
                player.setWorldBorder(border);
                border.setSize(border.getSize() - 0.1, 3600); // Red
            }
            case 4 -> border.setSize(border.getMaxSize()); // Not visible
        }
        player.setWorldBorder(border);
    }

    /**
     * Returns scoreboards of planet.
     *
     * @return planet's scoreboards.
     */
    public @NotNull PlanetScoreboards getScoreboards() {
        return scoreboards;
    }

    /**
     * Returns recipes of planet.
     *
     * @return planet's recipes.
     */
    public @NotNull PlanetRecipes getRecipes() {
        return recipes;
    }

    /**
     * Returns spawn location, where players should appear
     * after connecting to planet.
     *
     * @return spawn location of planet
     */
    public @NotNull Location getSpawnLocation() {
        World world = getWorld();
        if (world == null) return spawnLocation;
        if (spawnLocation != null) {
            spawnLocation.setWorld(world);
            return spawnLocation;
        }
        return world.getSpawnLocation();
    }

    /**
     * Sets new spawn location for planet, where player
     * will appear after joining to planet.
     *
     * @param spawnLocation new spawn location.
     */
    public void setSpawnLocation(@NotNull Location spawnLocation) {
        this.spawnLocation = WorldUtils.roundLocation(spawnLocation);
        World world = getWorld();
        this.spawnLocation.setWorld(world);
        if (world != null) world.setSpawnLocation(spawnLocation);

        Map<String, Double> configLocation = WorldUtils.fromLocationToMap(spawnLocation);
        FileUtils.setPlanetConfigParameter(planet, "spawn", configLocation);
    }

    /**
     * Checks whether world should save territory changes.
     *
     * @return true - will be saved, false - not.
     */
    public boolean isAutoSave() {
        return autoSave;
    }

    /**
     * Checks whether world is busy for deleting or unloading,
     * so players shouldn't be able to join it.
     *
     * @return true - is busy, false - not.
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Sets whether world should ignore unloading.
     *
     * @param ignoreUnloading ignore unloading.
     */
    public void setIgnoreUnloading(boolean ignoreUnloading) {
        this.ignoreUnloading = ignoreUnloading;
    }

    /**
     * Checks whether world will be not unloaded
     * by unload request.
     *
     * @return true - world cannot be unloaded, false - can be.
     */
    public boolean isIgnoringUnload() {
        return ignoreUnloading;
    }

    /**
     * Sets auto-save option to specified value.
     *
     * @param autoSave true - build world changes will be saved.
     *                 <p>false - build world changes will be not saved.</p>
     */
    public void setAutoSave(boolean autoSave) {
        if (this.autoSave == autoSave) return;
        this.autoSave = autoSave;
        for (Player planetPlayer : planet.getPlayers()) {
            planetPlayer.sendMessage(getLocaleMessage("settings.autosave." + (autoSave ? "enabled" : "disabled")));
        }
        if (getWorld() != null) {
            getWorld().setAutoSave(autoSave);
        }
        FileUtils.setPlanetConfigParameter(planet, "autosave", !autoSave ? false : null);
    }
}
