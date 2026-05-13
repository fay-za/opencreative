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

package ua.mcchickenstudio.opencreative.managers.space;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.planet.PlanetDeletionEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetRegisterEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetCreationEvent;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;
import ua.mcchickenstudio.opencreative.wanders.Wander;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.ErrorUtils;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerator;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldTemplate;

import java.io.File;
import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.createWorldSettings;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isPlanet;

public final class Space implements PlanetsManager {

    private final Map<Integer, Planet> planets = new HashMap<>();
    private final Set<Planet> corruptedPlanets = new HashSet<>();

    @Override
    public @NotNull Set<Planet> getPlanets() {
        return new HashSet<>(planets.values());
    }

    @Override
    public @NotNull Set<Planet> getCorruptedPlanets() {
        return corruptedPlanets;
    }

    @Override
    public void registerPlanet(@NotNull Planet planet) {
        if (planet.isCorrupted()) {
            corruptedPlanets.add(planet);
        } else {
            planets.put(planet.getId(), planet);
        }
        new PlanetRegisterEvent(planet).callEvent();
    }

    @Override
    public void unregisterPlanet(@NotNull Planet planet) {
        planets.remove(planet.getId());
        corruptedPlanets.remove(planet);
        clearOnceMessages(planet);
    }

    @Override
    public void createPlanet(@NotNull Player owner, int id, @NotNull WorldGenerator generator) {
        createPlanet(owner, id, generator, World.Environment.NORMAL, new Random().nextInt(), false, "");
    }

    @Override
    public void createPlanet(@NotNull Player owner, int id, @NotNull WorldTemplate template) {
        Wander wander = OpenCreative.getWander(owner);
        wander.setConnectingToPlanet(true);
        owner.showTitle(Title.title(
                toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
        ));
        long startTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Copying template folder (" + template.getID() + ") for new planet " + id + " by " + owner.getName() + "...");
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            File worldTemplateFolder = new File(OpenCreative.getPlugin().getDataPath()
                    + File.separator + "templates" + File.separator + template.getFolderName());
            if (!worldTemplateFolder.exists() || !worldTemplateFolder.isDirectory()) {
                sendPlayerErrorMessage(owner, "Failed to create world by template " + template.getID() + ", because folder doesn't exist.");
                sendCriticalErrorMessage("Failed to create world for planet " + id + " by " + owner.getName() + ". Folder " + template.getFolderName()
                        + " doesn't exist, or it's not directory.");
                wander.setConnectingToPlanet(false);
                return;
            }
            File devTemplateFolder = new File(worldTemplateFolder.getPath() + "dev");
            File planetFolder = new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator + "planet" + id + File.separator);
            File planetDevFolder = new File(planetFolder.getPath() + "dev");
            FileUtils.copyFilesToDirectory(worldTemplateFolder, planetFolder);
            if (devTemplateFolder.exists() && devTemplateFolder.isDirectory()) {
                FileUtils.copyFilesToDirectory(devTemplateFolder, planetDevFolder);
            }
            if (!owner.isOnline()) {
                FileUtils.deleteFolder(planetFolder);
                return;
            }
            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                wander.setConnectingToPlanet(false);
                owner.showTitle(Title.title(
                        toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
                ));
                OpenCreative.getPlugin().getLogger().info("Creating new planet " + id + " by " + owner.getName() + "...");

                createWorldSettings(id, owner, World.Environment.NORMAL, template.getID());
                Planet planet = new Planet(id);

                if (planet.getTerritory().generateWorld(template, World.Environment.NORMAL, 0, false, "") != null) {
                    long endTime = System.currentTimeMillis();
                    OpenCreative.getPlugin().getLogger().info("World for planet " + id + " successfully generated in " + (endTime - startTime) + " ms");
                    new PlanetCreationEvent(planet, owner, template, World.Environment.NORMAL, 0, false).callEvent();
                    planet.connectPlayer(owner);
                } else {
                    ErrorUtils.sendCriticalErrorMessage("Failed to create world for planet " + id + " by " + owner.getName() + ". World is null.");
                    sendPlayerErrorMessage(owner, "Failed to create world, world is null.");
                }
            });
        });
    }

    @Override
    public void createPlanet(@NotNull Player owner, int id, @NotNull WorldGenerator generator,
                             World.@NotNull Environment environment, long seed, boolean generateStructures,
                             @NotNull String biome) {
        long startTime = System.currentTimeMillis();

        owner.showTitle(Title.title(
                toComponent(getLocaleMessage("creating-world.title")), toComponent(getLocaleMessage("creating-world.subtitle")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(30), Duration.ofSeconds(2))
        ));
        OpenCreative.getPlugin().getLogger().info("Creating new planet " + id + " by " + owner.getName() + "...");

        createWorldSettings(id, owner, environment, generator.getID());
        Planet planet = new Planet(id);

        if (planet.getTerritory().generateWorld(generator, environment, seed, generateStructures, biome) != null) {
            long endTime = System.currentTimeMillis();
            OpenCreative.getPlugin().getLogger().info("World for planet " + id + " successfully generated in " + (endTime - startTime) + " ms");
            new PlanetCreationEvent(planet, owner, generator, environment, seed, generateStructures).callEvent();
            planet.connectPlayer(owner);
        } else {
            ErrorUtils.sendCriticalErrorMessage("Failed to create world for planet " + id + " by " + owner.getName() + ". World is null.");
            sendPlayerErrorMessage(owner, "Failed to create world, world is null.");
        }
    }

    public @NotNull Set<Planet> getPlanetsByOwner(@NotNull Player player) {
        return getPlanetsByOwner(player.getName());
    }

    @Override
    public boolean deletePlanet(@NotNull Planet planet) {
        new PlanetDeletionEvent(planet).callEvent();
        try {
            planet.getTerritory().setIgnoreUnloading(true);
            for (Player p : planet.getPlayers()) {
                PlayerUtils.teleportToLobby(p);
                if (p.getOpenInventory().getTopInventory().getHolder() instanceof WorldMenu) {
                    p.closeInventory();
                }
            }
            PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
            planetEvent.callEvent();
            if (!planetEvent.isCancelled()) {
                planet.setSharing(Planet.Sharing.CLOSED);
            }
            unregisterPlanet(planet);
            Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                if (planet.isLoaded()) {
                    Bukkit.unloadWorld(planet.getWorldName(), false);
                }
                if (planet.getDevPlanet().isLoaded()) {
                    Bukkit.unloadWorld(planet.getDevPlanet().getWorldName(), false);
                }
                Bukkit.getScheduler().runTaskLaterAsynchronously(OpenCreative.getPlugin(), () -> {
                    FileUtils.deleteFolder(FileUtils.getPlanetFolder(planet));
                    FileUtils.deleteFolder(FileUtils.getDevPlanetFolder(planet.getDevPlanet()));
                    FileUtils.deleteWorldFoldersInPlugins(planet.getId());
                }, 5L);
            }, 5L);
            return true;
        } catch (Exception error) {
            planet.getTerritory().setIgnoreUnloading(false);
            ErrorUtils.sendCriticalErrorMessage("Error while deleting world " + planet.getId(), error);
            return false;
        }
    }

    @Override
    public @NotNull List<Planet> getRecommendedPlanets() {
        List<Planet> featuredPlanets = new ArrayList<>();
        Set<Integer> featuredIds = OpenCreative.getSettings().getRecommendedWorldsIDs();
        for (int id : featuredIds) {
            Planet planet = getPlanetById(String.valueOf(id));
            if (planet != null) {
                featuredPlanets.add(planet);
            }
        }
        return featuredPlanets;
    }

    @Override
    public @NotNull Set<Planet> getFavoritePlanets(@NotNull OfflineWander wander) {
        Set<Planet> favoritePlanets = new LinkedHashSet<>();
        Set<Integer> favoriteIds = wander.getFavoriteWorlds();
        for (int id : favoriteIds) {
            Planet planet = getPlanetById(String.valueOf(id));
            if (planet != null) {
                favoritePlanets.add(planet);
            }
        }
        return favoritePlanets;
    }

    public @NotNull Set<Planet> getPlanetsContainingName(@NotNull String worldName) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets.values()) {
            if (planet.getInformation().getDisplayName().toLowerCase().contains(worldName.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public @NotNull Set<Planet> getPlanetsContainingID(@NotNull String worldID) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets.values()) {
            if (planet.getInformation().getCustomID().toLowerCase().contains(worldID.toLowerCase())) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public @NotNull Set<Planet> getPlanetsByOwner(@NotNull String owner) {
        Set<Planet> foundPlanets = new HashSet<>();
        for (Planet planet : planets.values()) {
            if (planet.isOwner(owner)) {
                foundPlanets.add(planet);
            }
        }
        return foundPlanets;
    }

    @Override
    public Planet getPlanetByPlayer(@NotNull Player player) {
        World world = player.getWorld();
        if (!isPlanet(world)) return null;
        String worldID = world.getName()
                .replace("./planets/planet", "")
                .replace("dev", "");
        return getPlanetById(worldID);
    }

    @Override
    public DevPlanet getDevPlanet(@NotNull Player player) {
        if (!isDevPlanet(player.getWorld())) return null;
        Planet planet = getPlanetByPlayer(player);
        return planet != null ? planet.getDevPlanet() : null;
    }

    @Override
    public DevPlanet getDevPlanet(@NotNull World world) {
        if (!isDevPlanet(world)) return null;
        String worldID = world.getName()
                .replace("./planets/planet", "")
                .replace("dev", "");
        Planet planet = getPlanetById(worldID);
        if (planet == null) return null;
        return planet.getDevPlanet();
    }

    @Override
    public Planet getPlanetByWorld(@NotNull World world) {
        if (!isPlanet(world) && !isDevPlanet(world)) return null;
        String worldID = world.getName()
                .replace("./planets/planet", "")
                .replace("dev", "");
        return getPlanetById(worldID);
    }

    @Override
    public Planet getPlanetByWorldName(@NotNull String worldName) {
        if (!isPlanet(worldName) && !isDevPlanet(worldName)) return null;
        String worldID = worldName
                .replace("./planets/planet", "")
                .replace("dev", "");
        return getPlanetById(worldID);
    }

    @Override
    public Planet getPlanetById(@NotNull String id) {
        try {
            int planetId = Integer.parseInt(id);
            return planets.get(planetId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public @Nullable Planet getPlanetByAnyID(@NotNull String id) {
        Planet found = getPlanetById(id);
        if (found != null) return found;
        return getPlanetByCustomID(id);
    }

    @Override
    public Planet getPlanetByCustomID(@NotNull String customID) {
        for (Planet planet : planets.values()) {
            if (planet.getInformation().getCustomID().equalsIgnoreCase(customID)) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isStableConnection() {
        // True, because it depends only on file system
        return true;
    }

    @Override
    public String getName() {
        return "Planet Manager";
    }
}
