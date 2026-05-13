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

package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodeStorage;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

/**
 * <h1>FileUtils</h1>
 * This class contains utils for creating, reading, modifying
 * and removing files for worlds.
 */
public final class FileUtils {

    private static Boolean usingNewWorldsContainer;

    /**
     * Creates planet's settings.yml file.
     *
     * @param id          Planet's ID.
     * @param owner       Owner of new world.
     * @param environment Environment of world.
     * @param generatorID ID of world generator.
     */
    public static void createWorldSettings(int id,
                                           @NotNull Player owner,
                                           @NotNull World.Environment environment,
                                           @NotNull String generatorID) {
        String worldFolderPath = getPlanetsStorageFolder().getPath() + File.separator + "planet" + id + File.separator;
        File folder = new File(worldFolderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(worldFolderPath, "settings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a settings.yml for world " + id, error);
                return;
            }
        }
        FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        fillDefaultSettings(worldFile, id, owner, environment, generatorID);
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world settings.yml for " + id, error);
        }
    }

    /**
     * Fills world's settings configuration with default values.
     *
     * @param config      settings configuration.
     * @param id          world's id.
     * @param owner       world's owner.
     * @param environment environment on world creation.
     */
    public static void fillDefaultSettings(@NotNull FileConfiguration config,
                                           int id,
                                           @NotNull Player owner,
                                           @NotNull World.Environment environment,
                                           @NotNull String generatorID) {
        config.set("owner", owner.getName());
        config.set("owner-uuid", owner.getUniqueId().toString());
        config.set("owner-group", OpenCreative.getSettings().getGroups().getGroup(owner).getName().toLowerCase());
        config.set("environment", environment.name());
        config.set("generator", generatorID);
        config.set("world", "planet" + id);
        config.set("creation-time", System.currentTimeMillis());
        config.set("last-activity-time", System.currentTimeMillis());
        config.set("name", MessageUtils.getLocaleMessage("creating-world.default-world-name").replace("%player%", owner.getName()));
        config.set("description", MessageUtils.getLocaleMessage("creating-world.default-world-description").replace("%player%", owner.getName()));
        config.set("icon", String.valueOf(Material.DIAMOND));
        config.set("sharing", String.valueOf(Planet.Sharing.PUBLIC));
        config.set("category", String.valueOf(PlanetInfo.Category.SANDBOX));
        config.set("customID", String.valueOf(id));
        config.set("players.unique", new ArrayList<String>());
        config.set("players.liked", new ArrayList<String>());
        config.set("players.builders.trusted", new ArrayList<String>());
        config.set("players.builders.not-trusted", new ArrayList<String>());
        config.set("players.developers.trusted", new ArrayList<String>());
        config.set("players.developers.not-trusted", new ArrayList<String>());
        config.set("players.whitelist", new ArrayList<String>());
        config.set("players.blacklist", new ArrayList<String>());
        config.set("flags", new HashMap<String, Integer>());
    }

    /**
     * Creates planet's codeScript.yml file.
     **/
    public static void createCodeScript(@NotNull String path, @NotNull String worldName) {
        File file = new File(path, "codeScript.yml");
        FileConfiguration worldFile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException error) {
                sendCriticalErrorMessage("Couldn't create a codeScript.yml for planet " + getPlanetIdFromName(worldName) + " because of IOException. Maybe it is already exists? " + error.getMessage());
                return;
            }
        }
        worldFile.set("world", getPlanetIdFromName(worldName));
        worldFile.set("creation-time", System.currentTimeMillis());
        worldFile.set("last-activity-time", System.currentTimeMillis());
        worldFile.createSection("code");
        try {
            worldFile.save(file);
        } catch (IOException | IllegalArgumentException error) {
            sendCriticalErrorMessage("Couldn't save world codeScript.yml for " + worldName + " because of " + error.getClass().getName() + " " + error.getMessage());
        }
    }

    /**
     * Loads localization file from OpenCreative/locales/ folder. If no localization file was found, then it creates a new one.
     **/
    public static void loadLocales() {
        OpenCreative.getPlugin().getLogger().info("Loading localization file...");
        File folder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }
        String selectedLang = OpenCreative.getPlugin().getConfig().getString("messages.locale", "en");
        File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (!file.exists()) {
            OpenCreative.getPlugin().getConfig().set("messages.locale", "en");
        }
        MessageUtils.loadLocalizationFile();
        OpenCreative.getPlugin().getLogger().info("Loaded localization file...");
    }

    /**
     * Resets localization file from OpenCreative/locales/ folder. If localization file is detected in folder, then it will be removed and replaced with plugin's new one.
     **/
    public static void resetLocales() {
        OpenCreative.getPlugin().getLogger().info("Resetting localization file...");
        File folder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sendCriticalErrorMessage("Couldn't create directory for locales... " + folder.getPath());
            }
        }

        Map<String, Object> oldMessages = new LinkedHashMap<>();
        FileConfiguration oldLocalization = MessageUtils.getLocalization();
        for (String path : OpenCreative.getSettings().getMessagesIgnoringReset()) {
            Object oldMessage = oldLocalization.get(path);
            if (oldMessage != null) oldMessages.put(path, oldMessage);
        }

        String selectedLang = OpenCreative.getPlugin().getConfig().getString("messages.locale", "en");
        File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (file.exists()) {
            file.delete();
        }

        MessageUtils.loadLocalizationFile();
        for (Map.Entry<String, Object> entry : oldMessages.entrySet()) {
            MessageUtils.setMessage(entry.getKey(), entry.getValue());
        }
        if (!oldMessages.isEmpty())
            OpenCreative.getPlugin().getLogger().info(oldMessages.size() + " old messages (" + String.join(", ", oldMessages.keySet()) + ") will be not reset, because they're specified in messages.do-not-reset in config.yml.");

        OpenCreative.getPlugin().getLogger().info("Reset localization file!");
    }

    /**
     * Loads all planets to base.
     **/
    public static void loadPlanets() {
        OpenCreative.getPlugin().getLogger().info("Registering worlds to base...");
        try {
            convertOldPlanetFolders();
            File[] planetsFolders = getWorldsFolders();
            int foundWorlds = planetsFolders.length;
            if (foundWorlds == 0) {
                OpenCreative.getPlugin().getLogger().info("No worlds have been detected.");
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Found " + foundWorlds + " worlds, adding...");
            int corruptedWorlds = 0;
            int deprecatedWorlds = 0;
            int addedWorlds = 0;
            int nextPercent = 10;
            boolean shouldLogEveryWorld = OpenCreative.getSettings().isDebug() || foundWorlds < 100;
            long currentTime = System.currentTimeMillis();
            for (File planetFolder : planetsFolders) {
                String worldName = planetFolder.getName();
                if (!worldName.endsWith("dev")) {
                    int id = -1;
                    try {
                        id = Integer.parseInt(worldName.replace("planet", ""));
                    } catch (NumberFormatException ignored) {}
                    if (id == -1) continue;
                    if (shouldLogEveryWorld) {
                        OpenCreative.getPlugin().getLogger().info("Adding world " + id + " to base...");
                    } else {
                        int percent = (addedWorlds * 100) / foundWorlds;
                        if (percent >= nextPercent) {
                            OpenCreative.getPlugin().getLogger().info("Added " + nextPercent + "% worlds to base... (" + addedWorlds + "/" + foundWorlds + ")");
                            nextPercent += 10;
                        }
                    }
                    Planet planet = new Planet(id);
                    addedWorlds++;
                    if (planet.isCorrupted()) {
                        corruptedWorlds++;
                    } else if (currentTime - planet.getCreationTime() > 2592000000L) {
                        OfflinePlayer planetOwner = Bukkit.getOfflinePlayer(planet.getOwner());
                        if (planetOwner.getLastSeen() == 0 || currentTime - planetOwner.getLastSeen() > 2592000000L) {
                            deprecatedWorlds++;
                        }
                    }
                }
            }
            OpenCreative.getPlugin().getLogger().info("Loaded " + OpenCreative.getPlanetsManager().getPlanets().size() + " worlds for " + (System.currentTimeMillis() - currentTime) + " ms.");
            if (!shouldLogEveryWorld) OpenCreative.getPlugin().getLogger().info(" All worlds: " + String.join(", ", OpenCreative.getPlanetsManager().getPlanets()
                    .stream().map(planet -> String.valueOf(planet.getId())).toList()));
            OpenCreative.getPlugin().getLogger().info(" Deprecated worlds: " + deprecatedWorlds);
            OpenCreative.getPlugin().getLogger().info(" Corrupted worlds: " + corruptedWorlds);
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading worlds...", error);
        }
    }

    /**
     * Loads all modules to base.
     **/
    public static void loadModules() {
        OpenCreative.getPlugin().getLogger().info("Registering modules to base...");
        try {
            File[] modulesList = getModulesStorageFolder().listFiles();
            if (modulesList == null) {
                OpenCreative.getPlugin().getLogger().info("No modules have been detected.");
                return;
            }
            int foundModules = modulesList.length;
            OpenCreative.getPlugin().getLogger().info("Found " + modulesList.length + " modules, adding...");
            long currentTime = System.currentTimeMillis();
            int addedModules = 0;
            int nextPercent = 10;
            boolean shouldLogEveryModule = OpenCreative.getSettings().isDebug() || foundModules < 100;
            for (File moduleFile : getModulesFiles()) {
                String moduleName = moduleFile.getPath()
                        .replace(Bukkit.getServer().getWorldContainer() + File.separator, "")
                        .replace("modules" + File.separator, "")
                        .replace(".yml", "");
                int id = -1;
                try {
                    id = Integer.parseInt(moduleName.replace("module", ""));
                } catch (NumberFormatException ignored) {
                }
                if (id == -1) continue;
                if (shouldLogEveryModule) {
                    OpenCreative.getPlugin().getLogger().info("Adding module " + id + " to base...");
                } else {
                    int percent = (addedModules * 100) / foundModules;
                    if (percent >= nextPercent) {
                        OpenCreative.getPlugin().getLogger().info("Added " + nextPercent + "% modules to base... (" + addedModules + "/" + foundModules + ")");
                        nextPercent += 10;
                    }
                }
                Module module = new Module(id);
                OpenCreative.getModuleManager().registerModule(module);
                addedModules++;
            }
            OpenCreative.getPlugin().getLogger().info("Loaded " + OpenCreative.getModuleManager().getModules().size() + " modules for " + (System.currentTimeMillis() - currentTime) + " ms.");
            if (!shouldLogEveryModule) OpenCreative.getPlugin().getLogger().info(" All modules: " + String.join(", ", OpenCreative.getModuleManager().getModules()
                    .stream().map(module -> String.valueOf(module.getId())).toList()));
        } catch (Exception error) {
            sendCriticalErrorMessage("An error has occurred while loading modules...", error);
        }
    }

    public static @NotNull YamlConfiguration getDefaultConfig() {
        InputStream input = OpenCreative.getPlugin().getResource("config.yml");
        if (input == null) {
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    /**
     * Returns planet's folder, that stores planet's build world data, settings, script and players data.
     *
     * @param planet planet to get folder.
     * @return planet's folder.
     */
    public static File getPlanetFolder(Planet planet) {
        return new File(getPlanetFolderPath(planet.getId()));
    }

    /**
     * Returns development planet's folder. It contains world's map.
     **/
    public static File getDevPlanetFolder(DevPlanet devPlanet) {
        return new File(getDevPlanetFolderPath(devPlanet.getPlanet().getId()));
    }

    /**
     * Returns planet's settings.yml configuration.
     **/
    public static FileConfiguration getPlanetConfig(Planet planet) {
        File file = getPlanetConfigFile(planet);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Returns planet's settings.yml file.
     **/
    public static File getPlanetConfigFile(Planet planet) {
        return new File(getPlanetFolder(planet), "settings.yml");
    }

    /**
     * Returns planet's codeScript.yml file.
     **/
    public static File getPlanetScriptFile(Planet planet) {
        File scriptFile = new File((getPlanetFolder(planet)), "codeScript.yml");
        if (!scriptFile.exists()) {
            createCodeScript(getPlanetFolder(planet).getPath(), planet.getWorldName());
        }
        return scriptFile;
    }

    /**
     * Returns planet's variables.yml configuration.
     **/
    public static File getPlanetVariablesJson(Planet planet) {
        File variablesFile = new File(getPlanetFolder(planet), "variables.json");
        if (!variablesFile.exists()) {
            try {
                variablesFile.createNewFile();
            } catch (Exception error) {
                sendCriticalErrorMessage("Failed to create world's variables file.", error);
                return null;
            }
        }
        return variablesFile;
    }

    /**
     * Returns wanders file with his data, located in ./wanders/UUID.json.
     *
     * @param wander wander to get data.
     * @param create create file if it doesn't exist.
     * @return file - if exists, otherwise - null.
     */
    public static @Nullable File getWanderJsonFile(@NotNull OfflineWander wander, boolean create) {
        File wandersFolder = getWandersStorageFolder();
        if (!wandersFolder.exists()) {
            wandersFolder.mkdirs();
        }
        File wanderFile = new File(getWandersStorageFolder(), wander.getUniqueId() + ".json");
        if (!wanderFile.exists()) {
            if (!create) return null;
            try {
                if (!wanderFile.createNewFile()) {
                    sendDebug("Cannot create new wander's file: " + wanderFile);
                }
            } catch (Exception error) {
                sendCriticalErrorMessage("Failed to create new wander's file: " + wanderFile, error);
                return null;
            }
        }
        return wanderFile;
    }

    /**
     * Returns player's data json from planet folder.
     **/
    public static @Nullable File getPlayerDataJson(@NotNull Planet planet, @NotNull Player player) {
        File planetFolder = getPlanetFolder(planet);
        File folder = new File(planetFolder.getPath() + File.separator + "playersData");
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File dataFile = new File(folder, player.getUniqueId() + ".json");
            if (dataFile.exists()) {
                return dataFile;
            } else {
                dataFile.createNewFile();
                return dataFile;
            }
        } catch (IOException error) {
            return null;
        }
    }

    /**
     * Returns folders of all planets worlds.
     *
     * @return planets worlds folders.
     */
    public static File[] getWorldsFolders() {
        List<File> worldsFolders = new ArrayList<>();
        File planetsFolder = getPlanetsStorageFolder();
        if (!planetsFolder.exists()) {
            planetsFolder.mkdirs();
        }
        File[] planetsWorlds = planetsFolder.listFiles();
        if (planetsWorlds == null) {
            return worldsFolders.toArray(new File[0]);
        }
        for (File file : planetsWorlds) {
            if (isPlanetFolder(file) && !file.getName().endsWith("dev")) worldsFolders.add(file);
        }
        return worldsFolders.toArray(new File[0]);
    }

    /**
     * Returns folders of all modules yaml files.
     *
     * @return modules files.
     */
    public static File[] getModulesFiles() {
        List<File> modules = new ArrayList<>();
        File modulesFolder = getModulesStorageFolder();
        if (!modulesFolder.exists()) {
            modulesFolder.mkdirs();
        }
        File[] modulesFiles = modulesFolder.listFiles();
        if (modulesFiles == null) {
            return modules.toArray(new File[0]);
        }
        for (File moduleFile : modulesFiles) {
            if (moduleFile.isDirectory()) continue;
            if (!moduleFile.getName().endsWith(".yml")) continue;
            modules.add(moduleFile);
        }
        return modules.toArray(new File[0]);
    }

    /**
     * Returns folders of planets worlds that are
     * stored in server container or /unloadedWorlds/ folder.
     */
    public static void convertOldPlanetFolders() {
        File serverDirectory = Bukkit.getServer().getWorldContainer();
        File[] serverDirectoryFiles = serverDirectory.listFiles();
        int count = 0;
        if (serverDirectoryFiles != null) {
            for (File file : serverDirectoryFiles) {
                if (isOpenCreativeWorldFolder(file) && convertOldPlanetFolder(file)) {
                    count++;
                }
            }
        }
        File unloadedWorldsFolder = new File(serverDirectory + File.separator + "unloadedWorlds" + File.separator);
        if (unloadedWorldsFolder.exists()) {
            File[] unloadedWorlds = unloadedWorldsFolder.listFiles();
            if (unloadedWorlds != null) {
                for (File file : unloadedWorlds) {
                    if (convertOldPlanetFolder(file)) count++;
                }
                unloadedWorlds = unloadedWorldsFolder.listFiles();
                if (unloadedWorlds != null && unloadedWorlds.length == 0) {
                    unloadedWorldsFolder.delete();
                }
            }
        }
        if (usingNewWorldsContainer()) {
            File oldPlanetsFolder = new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator);
            if (oldPlanetsFolder.exists() && oldPlanetsFolder.isDirectory()) {
                File[] oldFolders = oldPlanetsFolder.listFiles();
                if (oldFolders != null) {
                    for (File file : oldFolders) {
                        if (isOpenCreativeWorldFolder(file) && convertOldPlanetFolder(file)) {
                            count++;
                        }
                    }
                }
            }
        }
        if (count > 0) {
            OpenCreative.getPlugin().getLogger().info("Converted " + count + " old worlds!");
        }
    }

    /**
     * Checks whether specified folder has similar OpenCreative+ files.
     *
     * @param folder folder to check.
     * @return true - it's OpenCreative+ folder, false - not.
     */
    public static boolean isOpenCreativeWorldFolder(@NotNull File folder) {
        File settings = new File(folder, "settings.yml");
        if (settings.exists()) return true;
        File codeScript = new File(folder, "codeScript.yml");
        if (codeScript.exists()) return true;
        File variables = new File(folder, "variables.json");
        return variables.exists();
    }

    /**
     * Returns a new renamed folder if planet is "plot",
     * otherwise it will return same folder.
     *
     * @param folder planet folder to convert.
     * @return renamed or same planet folder.
     */
    public static boolean convertOldPlanetFolder(File folder) {
        try {
            boolean converted = false;
            if (folder.getName().startsWith("plot")) {
                OpenCreative.getPlugin().getLogger().info("Renaming " + folder.getName() + " to " + folder.getName().replace("plot", "planet") + "...");
                File newFile = new File(folder.getParent() + File.separator + folder.getName().replace("plot", "planet"));
                folder.renameTo(newFile);
                folder = newFile;
                converted = true;
            }
            if (folder.getPath().contains("planet") && !folder.getPath().contains("planets")) {
                File newFolder = new File(getPlanetsStorageFolder().getPath() + File.separator + folder.getName());
                OpenCreative.getPlugin().getLogger().info("Moving " + folder.getName() + " to planets folder...");
                copyFilesToDirectory(folder, newFolder);
                deleteFolder(folder);
                folder = newFolder;
                converted = true;
            } else if (usingNewWorldsContainer() && folder.getPath().contains("planet") && !folder.getPath().contains("dimensions")) {
                File newFolder = new File(getPlanetsStorageFolder().getPath() + File.separator + folder.getName());
                OpenCreative.getPlugin().getLogger().info("Moving " + folder.getName() + " to world/dimensions/minecraft/planets folder...");
                copyFilesToDirectory(folder, newFolder);
                deleteFolder(folder);
                folder = newFolder;
                converted = true;
            }
            return converted;
        } catch (Exception error) {
            sendCriticalErrorMessage("Can't rename from plot to planet: " + folder.getName(), error);
            return false;
        }
    }

    /**
     * Checks if specified folder is directory of planet world.
     *
     * @param folder folder to check.
     * @return true - if folder is planet world, false - not.
     */
    public static boolean isPlanetFolder(File folder) {
        return folder.isDirectory() && folder.getName().startsWith("planet") && folder.getPath().contains(getPlanetsStorageFolder().getPath());
    }

    /**
     * Unloads all loaded planets worlds.
     */
    public static void unloadPlanets() {
        OpenCreative.getPlugin().getLogger().info("Unloading worlds, please wait...");
        try {
            for (Planet planet : OpenCreative.getPlanetsManager().getPlanets()) {
                if (planet.isLoaded()) {
                    OpenCreative.getPlugin().getLogger().info("Unloading planet " + planet.getId() + "...");
                    planet.getTerritory().unload();
                } else if (planet.getDevPlanet().isLoaded()) {
                    OpenCreative.getPlugin().getLogger().info("Unloading planet dev " + planet.getId() + "...");
                    planet.getDevPlanet().unload(false);
                }
            }
            OpenCreative.getPlanetsManager().getPlanets().clear();
        } catch (Exception error) {
            sendCriticalErrorMessage("Error while unloading worlds.", error);
        }
    }

    /**
     * Copies input files into output directory.
     **/
    public static boolean copyFilesToDirectory(File input, File output) {
        try {
            File[] inputFiles = input.listFiles();
            if (!output.exists()) {
                if (!output.mkdirs())
                    sendCriticalErrorMessage("Can't create a output directory " + output.getPath() + " for copying from input " + input.getPath());
            }
            if (inputFiles != null) {
                for (File worldFile : inputFiles) {
                    if (worldFile.isDirectory())
                        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(worldFile, output);
                    else org.apache.commons.io.FileUtils.copyFileToDirectory(worldFile, output);
                }
            }
            return true;
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't copy files from directory " + input.getPath() + " to directory: " + output.getPath(), error);
            return false;
        }
    }

    /**
     * Deletes Minecraft files, that are interrupting world copying process.
     *
     * @param worldFolder folder of world.
     */
    public static void deleteUnnecessaryWorldFiles(File worldFolder) {
        try {
            if (!worldFolder.exists()) return;
            File uidFile = new File(worldFolder, "uid.dat");
            File sessionFile = new File(worldFolder, "session.lock");
            uidFile.delete();
            sessionFile.delete();
        } catch (Exception error) {
            sendCriticalErrorMessage("Cannot delete uid.dat file.", error);
        }
    }


    /**
     * Deletes world folders from other plugins.
     *
     * @param planetId id of planet.
     */
    public static void deleteWorldFoldersInPlugins(int planetId) {
        if (!new File(Bukkit.getWorldContainer(), "plugins/WorldGuard").exists()) {
            return;
        }
        File worldGuardWorld = new File(Bukkit.getWorldContainer(), "plugins/WorldGuard/worlds/planets/planet" + planetId);
        File worldGuardDevWorld = new File(Bukkit.getWorldContainer(), "plugins/WorldGuard/worlds/planets/planet" + planetId + "dev");
        deleteFolder(worldGuardWorld);
        deleteFolder(worldGuardDevWorld);
    }

    /**
     * Deletes directory and files inside it, if exists.
     *
     * @param directory path of directory.
     */
    public static void deleteFolder(File directory) {
        if (!directory.exists()) return;
        if (directory.equals(Bukkit.getWorldContainer())) return;
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(directory);
        } catch (IOException error) {
            sendCriticalErrorMessage("Couldn't delete a folder with path " + directory.getPath(), error);
        }
    }

    /**
     * Sets parameter to Long value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, long parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, String.valueOf(parameterValue));
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Removes parameter from planet's config.
     *
     * @param planet        planet to set.
     * @param parameterPath path of parameter in config.
     */
    public static void removePlanetConfigParameter(Planet planet, String parameterPath) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, null);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to Int value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, int parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to Object value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, Object parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to Object value in module's settings.
     *
     * @param module         module to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setModuleConfigParameter(Module module, String parameterPath, Object parameterValue) {
        CodeStorage moduleConfig = getModuleConfig(module);
        File moduleConfigFile = getModuleConfigFile(module.getId());
        moduleConfig.set(parameterPath, parameterValue);
        try {
            moduleConfig.saveToFile(moduleConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save module's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to String value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, String parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to List value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, List<String> parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, parameterValue);
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Sets parameter to Set value in planet's settings.
     *
     * @param planet         planet to set.
     * @param parameterPath  path of parameter in config.
     * @param parameterValue value.
     */
    public static void setPlanetConfigParameter(Planet planet, String parameterPath, Set<String> parameterValue) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        File planetConfigFile = getPlanetConfigFile(planet);
        planetConfig.set(parameterPath, new ArrayList<>(parameterValue));
        try {
            planetConfig.save(planetConfigFile);
        } catch (IOException error) {
            sendCriticalErrorMessage("Can't save planet's settings configuration to file.", error);
        }
    }

    /**
     * Returns a specified list of players nicknames.
     *
     * @param planet planet to get list.
     * @param type   type of players list.
     * @return list of nicknames.
     */
    public static List<String> getPlayersFromPlanetList(Planet planet, Planet.PlayersType type) {
        return new ArrayList<>(getPlanetConfig(planet).getStringList(type.getPath()));
    }

    /**
     * Adds player to list, that located in planet's settings.yml file.
     *
     * @param planet   planet to add player.
     * @param nickname nickname of player.
     * @param type     id of player list.
     * @return true - if successfully added, false - if failed.
     */
    public static boolean addPlayerInPlanetList(Planet planet, String nickname, Planet.PlayersType type) {
        FileConfiguration planetConfig = getPlanetConfig(planet);
        List<String> playersList = planetConfig.getStringList(type.getPath());
        for (String player : playersList) {
            /*
             * We will not add player, if list
             * already contains him.
             */
            if (player.equalsIgnoreCase(nickname)) {
                return false;
            }
        }
        playersList.add(nickname);
        setPlanetConfigParameter(planet, type.getPath(), playersList);
        return true;
    }

    /**
     * Returns size of folder.
     *
     * @param file folder to get size.
     * @return size of folder.
     */
    public static long getFolderSize(File file) {
        try {
            return org.apache.commons.io.FileUtils.sizeOfDirectory(file);
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Returns module's configuration.
     **/
    public static CodeConfiguration getModuleConfig(Module module) {
        CodeConfiguration script = new CodeConfiguration();
        script.loadCode(getModuleConfigFile(module.getId()));
        return script;
    }

    /**
     * Returns module's config file.
     *
     * @return file of module's config.
     */
    public static File getModuleConfigFile(int id) {
        return new File(getModulesStorageFolder(), "module" + id + ".yml");
    }

    /**
     * Returns folder that stores all modules folders.
     *
     * @return modules folder.
     */
    public static File getModulesStorageFolder() {
        return new File(Bukkit.getWorldContainer().getPath() + File.separator + "modules" + File.separator);
    }

    /**
     * Returns size of file.
     *
     * @param file file to get size.
     * @return size of file.
     */
    public static long getFileSize(File file) {
        try {
            return org.apache.commons.io.FileUtils.sizeOf(file);
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Returns temporary folder, that stores temporary files.
     *
     * @return temporary folder.
     */
    public static File getTempFolder() {
        return new File(OpenCreative.getPlugin().getDataFolder().getPath() + File.separator + "temp" +  File.separator);
    }

    /**
     * Returns file path of planet's world folder.
     *
     * @param id planet to get folder.
     * @return planet's folder path.
     */
    public static String getPlanetFolderPath(int id) {
        return getPlanetsStorageFolder().getPath() + File.separator + "planet" + id + File.separator;
    }

    /**
     * Returns file path of dev planet's world folder.
     *
     * @param id planet to get folder.
     * @return dev planet's folder path.
     */
    public static String getDevPlanetFolderPath(int id) {
        return getPlanetsStorageFolder().getPath() + File.separator + "planet" + id + "dev" + File.separator;
    }

    /**
     * Returns folder that stores all planets folders.
     *
     * @return planets folder.
     */
    public static File getPlanetsStorageFolder() {
        if (usingNewWorldsContainer()) {
            return new File(Bukkit.getWorldContainer().getPath()
                    + File.separator + "world" + File.separator + "dimensions"
                    + File.separator + "minecraft" + File.separator + "planets" + File.separator);
        }
        return new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator);
    }

    /**
     * Returns folder that stores all wanders folders.
     *
     * @return wanders folder.
     */
    public static File getWandersStorageFolder() {
        return new File(Bukkit.getWorldContainer().getPath() + File.separator + "wanders" + File.separator);
    }

    /**
     * Returns planet's ID without other symbols.
     *
     * @param name name of planet's folder.
     * @return planet's ID.
     */
    public static @NotNull String getPlanetIdFromName(@NotNull String name) {
        return name
                .replace(Bukkit.getServer().getWorldContainer().getPath().replace("\\", "/") + "/", "")
                .replace("planets/planet", "");
    }

    /**
     * Checks whether plugin should store all worlds
     * in {@code /world/dimensions/minecraft/planets} folder
     * (from 26.1+) instead of {@code /planets/} folder (1.21.x).
     *
     * @return true - planets will be saved in {@code /world/dimensions/minecraft/planets/},
     * false - planets will be saved in {@code /planets/}.
     */
    public static boolean usingNewWorldsContainer() {
        if (usingNewWorldsContainer == null) {
            usingNewWorldsContainer = !Bukkit.getMinecraftVersion().startsWith("1.");
        }
        return usingNewWorldsContainer;
    }

}
