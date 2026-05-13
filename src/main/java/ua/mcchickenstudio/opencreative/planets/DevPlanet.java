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

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.world.DevPlanetChunkGenerator;
import ua.mcchickenstudio.opencreative.utils.world.cache.ChunkCache;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformers;
import ua.mcchickenstudio.opencreative.utils.world.platforms.HasVisibleBorder;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>DevPlanet</h1>
 * This class represents developer's world, where players
 * can edit and change code with blocks on platform.
 * <p>
 * Platform consists of white, blue and gray stained glass
 * and it can't be destroyed. Players can place chests,
 * shulkers, signs and anvils on white stained glass.
 * On blue stained glass players should place executor blocks,
 * on gray stained glass - actions and conditions.
 * </p>
 */
public class DevPlanet {

    private final static Material DEFAULT_EVENT_MATERIAL = Material.BLUE_STAINED_GLASS;
    private final static Material DEFAULT_ACTION_MATERIAL = Material.GRAY_STAINED_GLASS;
    private final static Material DEFAULT_FLOOR_MATERIAL = Material.WHITE_STAINED_GLASS;
    private final Planet planet;
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final Map<Location, Layout> openedBlocksMenus = new HashMap<>();
    private final Map<UUID, Set<Location>> selectedExecutors = new HashMap<>();
    private final Set<Location> changedColumns = new HashSet<>();

    private String platformerID = "";
    private Material signMaterial = Material.BIRCH_WALL_SIGN;
    private Material containerMaterial = Material.CHEST;
    private boolean dropItems = true;
    private boolean saveLocation = true;
    private boolean nightVision = true;
    private boolean currentlySavingCode = false;

    /**
     * Constructor of developer planet, that
     * loads settings from config.
     *
     * @param planet planet, that owns this developer planet.
     */
    public DevPlanet(@NotNull Planet planet) {
        this.planet = planet;
        loadInformation();
    }

    /**
     * Returns default action block material,
     * used for creating new coding platforms.
     * <p>
     * This material must be solid, so players
     * will be able to place action blocks on it.
     *
     * @return default action block material.
     */
    public static Material getDefaultActionMaterial() {
        return DEFAULT_ACTION_MATERIAL;
    }

    /**
     * Returns default event block material,
     * used for creating new coding platforms.
     * <p>
     * This material must be solid, so players
     * will be able to place event blocks on it.
     *
     * @return default event block material.
     */
    public static Material getDefaultEventMaterial() {
        return DEFAULT_EVENT_MATERIAL;
    }

    /**
     * Returns default floor block material,
     * used for creating new coding platforms.
     * <p>
     * This material must be solid, so players
     * will be able to place allowed blocks on it.
     *
     * @return default floor block material.
     */
    public static Material getDefaultFloorMaterial() {
        return DEFAULT_FLOOR_MATERIAL;
    }

    /**
     * Loads settings of developer planet.
     */
    private void loadInformation() {
        FileConfiguration config = getPlanetConfig(planet);
        try {
            containerMaterial = Material.getMaterial(config.getString("dev.container", "CHEST"));
            if (containerMaterial == null || !containerMaterial.isBlock()) {
                containerMaterial = Material.CHEST;
            }
        } catch (Exception ignored) {
        }
        try {
            signMaterial = Material.getMaterial(config.getString("dev.sign", "BIRCH_WALL_SIGN"));
            if (signMaterial == null || !signMaterial.isBlock()) {
                signMaterial = Material.BIRCH_WALL_SIGN;
            }
        } catch (Exception ignored) {
        }
        dropItems = config.getBoolean("dev.drops", true);
        saveLocation = config.getBoolean("dev.save-location", true);
        nightVision = config.getBoolean("dev.night-vision", true);
        platformerID = config.getString("dev.platformer", "");
    }

    /**
     * Loads developer's world and setups it.
     */
    public void loadDevPlanetWorld() {
        long startTime = System.currentTimeMillis();
        boolean existed = this.exists();
        World world = Bukkit.createWorld(new WorldCreator(this.getWorldName())
                .type(WorldType.FLAT)
                .generator(new DevPlanetChunkGenerator()));
        if (world == null) {
            sendCriticalErrorMessage("Failed to load Dev planet world " + planet.getId());
            return;
        }
        if (existed) {
            if (world.getBlockAt(4, 0, 4).isEmpty()) {
                createPlatform(1, 1);
            }
        } else {
            createPlatform(1, 1);
            world.setTime(12500);
        }
        setupWorld();
        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Dev planet world " + planet.getId() + " loaded in " + (endTime - startTime) + " ms");
    }

    /**
     * Unloads developer's world and teleports
     * all players in it to lobby.
     */
    public void unload() {
        unload(OpenCreative.getPlugin().isEnabled());
    }

    /**
     * Unloads developer's world and teleports
     * all players in it to lobby.
     *
     * @param asyncSave true - will save world later, false - immediately.
     */
    public void unload(boolean asyncSave) {
        if (!isLoaded()) return;

        changedColumns.clear();

        long startTime = System.currentTimeMillis();

        World world = getWorld();
        if (world != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(world)) {
                    teleportToLobby(player);
                }
            }
            if (asyncSave) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    world.unloadChunk(chunk.getX(), chunk.getZ(), true);
                }
                try {
                    // 1.21+ Content:
                    Method saveMethod = world.getClass().getMethod("save", boolean.class);
                    saveMethod.invoke(world, false);
                } catch (Exception ignored) {
                    world.save();
                }
                Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                    Bukkit.unloadWorld(getWorldName(), false);
                }, 40);
            } else {
                Bukkit.unloadWorld(getWorldName(), true);
            }
        }

        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Dev planet world " + planet.getId() + " unloaded in " + (endTime - startTime) + " ms");
    }

    /**
     * Setups developer's world, changes spawn location,
     * sets game rules and world border.
     */
    public void setupWorld() {
        this.getWorld().setSpawnLocation(2, 1, 2);
        this.getWorld().setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        this.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        this.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        this.getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        this.getWorld().setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        this.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
        this.getWorld().setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false);
        getDevPlatformer().setWorldBorder(this);
        changedColumns.clear();
    }

    /**
     * Checks whether developer planet was generated before.
     *
     * @return true - exists, false - not created yet.
     */
    public boolean exists() {
        File folder = getDevPlanetFolder(this);
        return folder.exists() && folder.isDirectory();
    }

    public Set<Material> getAllCodingBlocksForPlacing() {
        Set<Material> allBlocks = new HashSet<>();
        allBlocks.addAll(getEventsBlocks());
        allBlocks.addAll(getActionsBlocks());
        return allBlocks;
    }

    public Set<Material> getEventsBlocks() {
        return new HashSet<>(Arrays.stream(ExecutorCategory.values()).map(ExecutorCategory::getBlock).toList());
    }

    public Set<Material> getActionsBlocks() {
        return new HashSet<>(Arrays.stream(ActionCategory.values()).map(ActionCategory::getBlock).toList());
    }

    /**
     * Creates a coding platform with specified X and Z of platform.
     * <p>
     * By default, it generates floor with white stained-glass,
     * and fills executor sections with blue stained-glass,
     * action sections with gray stained-glass.</p>
     *
     * @param platformX X number of platform.
     * @param platformZ Z number of platform.
     * @return true - if successfully created, false - if failed.
     */
    public boolean createPlatform(int platformX, int platformZ) {
        if (platformX >= 30 || platformZ >= 30 || platformX <= 0 || platformZ <= 0) {
            return false;
        }
        getDevPlatformer().buildPlatform(new DevPlatform(getWorld(), getDevPlatformer(), platformX, platformZ),
                DEFAULT_FLOOR_MATERIAL, DEFAULT_EVENT_MATERIAL, DEFAULT_ACTION_MATERIAL);
        return true;
    }

    /**
     * Claims new platform and teleports player to it.
     *
     * @param platform platform to claim.
     * @param player   player, who will be teleported.
     * @return true - claimed coding platform, false - already built and exists.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean claimPlatform(DevPlatform platform, Player player) {
        if (getDevPlatformer().claimPlatform(this, platform)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(platform.getSpawnLocation());
            player.sendMessage(getLocaleMessage("environment.platform.claimed"));
            Sounds.DEV_PLATFORM_CLAIM.play(player);
            Location location = getDevPlatformer().getPlatformBeginLocation(platform);
            Chunk chunk = location.getChunk();
            ChunkCache.load(getWorld(), chunk.getX(), chunk.getZ());
            return true;
        } else {
            return false;
        }
    }

    public Set<Material> getIndestructibleBlocks() {
        Set<Material> indestructibleBlocks = new HashSet<>();
        indestructibleBlocks.add(DEFAULT_ACTION_MATERIAL);
        indestructibleBlocks.add(DEFAULT_EVENT_MATERIAL);
        indestructibleBlocks.add(DEFAULT_FLOOR_MATERIAL);
        indestructibleBlocks.add(Material.DIAMOND_ORE);
        indestructibleBlocks.add(Material.GOLD_ORE);
        indestructibleBlocks.add(Material.REDSTONE_ORE);
        indestructibleBlocks.addAll(Arrays.stream(ExecutorCategory.values()).map(ExecutorCategory::getAdditionalBlock).toList());
        indestructibleBlocks.addAll(Arrays.stream(ActionCategory.values()).map(ActionCategory::getAdditionalBlock).toList());
        indestructibleBlocks.remove(Material.PISTON);
        return indestructibleBlocks;
    }

    public Set<Material> getAllowedBlocks() {
        Set<Material> allowedBlocks = new HashSet<>();
        allowedBlocks.add(Material.LANTERN);
        allowedBlocks.add(Material.JACK_O_LANTERN);
        allowedBlocks.add(Material.SOUL_LANTERN);
        allowedBlocks.add(Material.TORCH);
        allowedBlocks.add(Material.SOUL_TORCH);
        allowedBlocks.add(Material.BARREL);
        allowedBlocks.add(Material.OAK_SIGN);
        allowedBlocks.add(Material.SPRUCE_SIGN);
        allowedBlocks.add(Material.ACACIA_SIGN);
        allowedBlocks.add(Material.BAMBOO_SIGN);
        allowedBlocks.add(Material.JUNGLE_SIGN);
        allowedBlocks.add(Material.CHERRY_SIGN);
        allowedBlocks.add(Material.WARPED_SIGN);
        allowedBlocks.add(Material.CRIMSON_SIGN);
        allowedBlocks.add(Material.MANGROVE_SIGN);
        allowedBlocks.add(Material.DARK_OAK_SIGN);
        allowedBlocks.add(Material.BIRCH_SIGN);
        allowedBlocks.add(Material.CRAFTING_TABLE);
        allowedBlocks.add(Material.JUKEBOX);
        allowedBlocks.add(Material.STONECUTTER);
        allowedBlocks.add(Material.CARTOGRAPHY_TABLE);
        allowedBlocks.add(Material.SMITHING_TABLE);
        allowedBlocks.add(Material.LOOM);
        allowedBlocks.add(Material.GRINDSTONE);
        allowedBlocks.add(Material.CHEST);
        allowedBlocks.add(Material.ANVIL);
        allowedBlocks.add(Material.CHIPPED_ANVIL);
        allowedBlocks.add(Material.DAMAGED_ANVIL);
        allowedBlocks.add(Material.ENDER_CHEST);
        allowedBlocks.add(Material.SHULKER_BOX);
        allowedBlocks.add(Material.WHITE_SHULKER_BOX);
        allowedBlocks.add(Material.BLACK_SHULKER_BOX);
        allowedBlocks.add(Material.BLUE_SHULKER_BOX);
        allowedBlocks.add(Material.BROWN_SHULKER_BOX);
        allowedBlocks.add(Material.CYAN_SHULKER_BOX);
        allowedBlocks.add(Material.MAGENTA_SHULKER_BOX);
        allowedBlocks.add(Material.GRAY_SHULKER_BOX);
        allowedBlocks.add(Material.GREEN_SHULKER_BOX);
        allowedBlocks.add(Material.LIME_SHULKER_BOX);
        allowedBlocks.add(Material.RED_SHULKER_BOX);
        allowedBlocks.add(Material.ORANGE_SHULKER_BOX);
        allowedBlocks.add(Material.PURPLE_SHULKER_BOX);
        allowedBlocks.add(Material.YELLOW_SHULKER_BOX);
        allowedBlocks.add(Material.LIGHT_BLUE_SHULKER_BOX);
        allowedBlocks.add(Material.LIGHT_GRAY_SHULKER_BOX);
        allowedBlocks.add(Material.PINK_SHULKER_BOX);
        // 1.21+ Content:
        Optional.ofNullable(Material.matchMaterial("PALE_OAK_SIGN"))
                .ifPresent(allowedBlocks::add);
        return allowedBlocks;
    }

    public List<Location> getPlacedExecutors(ExecutorCategory category) {
        List<Location> locations = new ArrayList<>();
        for (DevPlatform platform : getPlatforms()) {
            locations.addAll(platform.getPlacedExecutors(category));
        }
        return locations;
    }

    public List<Location> getPlacedFunctions() {
        List<Location> locations = new ArrayList<>();
        for (Location location : getPlacedExecutors(ExecutorCategory.FUNCTION)) {
            Block block = location.getBlock();
            String line = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
            if (line != null && !line.isEmpty()) {
                locations.add(block.getLocation());
            }
        }
        return locations;
    }

    public List<Location> getPlacedMethods() {
        List<Location> locations = new ArrayList<>();
        for (Location location : getPlacedExecutors(ExecutorCategory.METHOD)) {
            Block block = location.getBlock();
            String line = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
            if (line != null && !line.isEmpty()) {
                locations.add(block.getLocation());
            }
        }
        return locations;
    }

    public void updateContainers() {
        if (!isLoaded()) return;
        for (DevPlatform platform : getPlatforms()) {
            platform.setContainerMaterial(containerMaterial);
        }
    }

    public void updateSigns() {
        if (!isLoaded()) return;
        for (DevPlatform platform : getPlatforms()) {
            platform.setSignMaterial(signMaterial);
        }
    }

    public boolean isLoaded() {
        return Bukkit.getWorld(getWorldName()) != null;
    }

    public Layout getOpenedMenu(Location location) {
        return openedBlocksMenus.get(location);
    }

    public void registerOpenedMenu(Location location, Layout menu) {
        openedBlocksMenus.put(location, menu);
    }

    public void unregisterOpenedMenu(Location location) {
        openedBlocksMenus.remove(location);
    }

    public Material getContainerMaterial() {
        return containerMaterial;
    }

    public Material getSignMaterial() {
        return signMaterial;
    }

    public boolean isNightVision() {
        return nightVision;
    }

    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
        setPlanetConfigParameter(planet, "dev.night-vision", nightVision);
    }

    public boolean isSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(boolean saveLocation) {
        this.saveLocation = saveLocation;
        setPlanetConfigParameter(planet, "dev.save-location", saveLocation);
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
        setPlanetConfigParameter(planet, "dev.drops", dropItems);
    }

    public void setPlatformerID(String platformer) {
        this.platformerID = platformer;
        setPlanetConfigParameter(planet, "dev.platformer", platformerID);
    }

    public boolean setContainerMaterial(Material containerMaterial) {
        if (containerMaterial == Material.BARREL || containerMaterial == Material.CHEST || containerMaterial.name().endsWith("SHULKER_BOX")) {
            this.containerMaterial = containerMaterial;
            setPlanetConfigParameter(planet, "dev.container", containerMaterial.name());
            return true;
        }
        return false;
    }

    public boolean setSignMaterial(Material signMaterial) {
        // 1.21+ Content:
        Material paleSign = Material.matchMaterial("PALE_OAK_SIGN");
        if (signMaterial == Material.OAK_WALL_SIGN || signMaterial == Material.ACACIA_WALL_SIGN ||
                signMaterial == Material.BAMBOO_WALL_SIGN || signMaterial == Material.CHERRY_WALL_SIGN ||
                signMaterial == Material.BIRCH_WALL_SIGN || signMaterial == Material.JUNGLE_WALL_SIGN ||
                (paleSign != null && signMaterial == paleSign)) {
            this.signMaterial = signMaterial;
            setPlanetConfigParameter(planet, "dev.sign", signMaterial.name());
            return true;
        }
        return false;
    }

    public @NotNull String getWorldName() {
        return planet.getWorldName() + "dev";
    }

    /**
     * Returns list of existing coding platforms, that
     * can be used to place coding blocks.
     *
     * @return list of developer platforms.
     */
    public @NotNull List<DevPlatform> getPlatforms() {
        return getDevPlatformer().getPlatforms(this);
    }

    /**
     * Returns coding platform by location.
     *
     * @param location location to get platform.
     * @return coding platform - if location contains coding platform, otherwise - null.
     */
    public @Nullable DevPlatform getPlatformInLocation(@NotNull Location location) {
        return getDevPlatformer().getPlatformInLocation(this, location);
    }

    /**
     * Changes world border for all players
     * inside developer world, used when some
     * player joins the developer world.
     */
    public void displayWorldBorders() {
        if (!isLoaded()) return;
        if (getDevPlatformer() instanceof HasVisibleBorder) return;
        for (Player player : getWorld().getPlayers()) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(getWorld().getWorldBorder().getCenter());
            border.setSize(getWorld().getWorldBorder().getSize() * 5);
            player.setWorldBorder(border);
        }
    }

    public @NotNull DevPlatformer getDevPlatformer() {
        if (platformerID == null || platformerID.isEmpty()) return OpenCreative.getDevPlatformer();
        DevPlatformer platformer = DevPlatformers.getInstance().getById(platformerID);
        if (platformer == null) return OpenCreative.getDevPlatformer();
        return platformer;
    }

    public Map<UUID, Location> getLastLocations() {
        return lastLocations;
    }

    public @NotNull Set<Location> getMarkedExecutors(@NotNull Player player) {
        return selectedExecutors.getOrDefault(player.getUniqueId(), new LinkedHashSet<>());
    }

    /**
     * Adds executor to marked list for player,
     * when they click it with manipulator item.
     *
     * @param player   player, who just marked executor.
     * @param location location of executor block.
     */
    public void markExecutorAsSelected(@NotNull Player player, @NotNull Location location) {
        Set<Location> locations = selectedExecutors.getOrDefault(player.getUniqueId(), new LinkedHashSet<>());
        locations.add(location);
        selectedExecutors.put(player.getUniqueId(), locations);
    }

    /**
     * Removes marked executor for player,
     * who selected it with manipulator item.
     *
     * @param player   player, who marked executor before.
     * @param location location of executor block.
     */
    public void unselectMarkedExecutor(@NotNull Player player, @NotNull Location location) {
        Set<Location> locations = selectedExecutors.getOrDefault(player.getUniqueId(), new LinkedHashSet<>());
        locations.remove(location);
        if (locations.isEmpty()) {
            selectedExecutors.remove(player.getUniqueId());
        } else {
            selectedExecutors.put(player.getUniqueId(), locations);
        }
    }

    /**
     * Removes marked executor for all players,
     * who selected it with manipulator item.
     *
     * @param location location of executor block.
     */
    public void clearMarkedExecutors(@NotNull Location location) {
        for (UUID uuid : new HashSet<>(selectedExecutors.keySet())) {
            Set<Location> locations = selectedExecutors.get(uuid);
            if (locations == null || locations.isEmpty()) continue;
            locations.remove(location);
            selectedExecutors.put(uuid, locations);
        }
    }

    /**
     * Returns whether code was changed after
     * last parsing and saving.
     *
     * @return true - code was changed, false - not changed.
     */
    public boolean isCodeChanged() {
        return !changedColumns.isEmpty();
    }

    /**
     * Checks whether code is currently
     * being saved to file or not.
     *
     * @return true - is busy, false - not.
     */
    public boolean isCurrentlySavingCode() {
        return currentlySavingCode;
    }

    /**
     * Sets the state of saving code.
     * If true, it will disallow to save a code.
     *
     * @param currentlySavingCode true - set busy state, false - allow to save code.
     */
    public void setCurrentlySavingCode(boolean currentlySavingCode) {
        this.currentlySavingCode = currentlySavingCode;
    }

    /**
     * Returns executor location by location.
     *
     * @param location location to get platform.
     * @return executor location - if location is related to actions or its executor location itself.
     */
    public @Nullable Location getCodingLineBeginLocation(@NotNull Location location) {
        return getDevPlatformer().getColumnBeginLocation(this, location);
    }

    /**
     * Adds coding line, that will be parsed on
     * partially code parsing (/play) by getting
     * begin location.
     *
     * @param location location on coding line.
     */
    public void addInsideCodeColumnChange(@NotNull Location location) {
        Location executorLocation = getCodingLineBeginLocation(location);
        if (executorLocation == null) return;
        changedColumns.add(executorLocation);
    }

    /**
     * Adds coding line, that will be parsed on
     * partially code parsing (/play).
     *
     * @param executorLocation location of executor on coding line.
     */
    public void addChangedColumn(@NotNull Location executorLocation) {
        changedColumns.add(executorLocation);
    }

    /**
     * Returns immutable set of executor locations for changed coding lines.
     *
     * @return immutable set of locations with executors.
     */
    public @NotNull Set<Location> getChangedColumns() {
        return new HashSet<>(changedColumns);
    }

    /**
     * Clears set of changed coding lines, so they will be not parsed.
     */
    public void clearColumnsChanges() {
        changedColumns.clear();
    }

    public void clearMarkedExecutors(@NotNull Player player) {
        selectedExecutors.remove(player.getUniqueId());
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public Planet getPlanet() {
        return planet;
    }
}
