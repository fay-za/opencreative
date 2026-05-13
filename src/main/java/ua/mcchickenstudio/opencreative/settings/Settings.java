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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiment;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiments;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceEndEvent;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceStartEvent;
import ua.mcchickenstudio.opencreative.managers.stability.DisabledWatchdog;
import ua.mcchickenstudio.opencreative.managers.stability.Watchdog;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.groups.Groups;
import ua.mcchickenstudio.opencreative.settings.items.*;
import ua.mcchickenstudio.opencreative.utils.ErrorUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.world.generators.*;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformer;
import ua.mcchickenstudio.opencreative.utils.world.platforms.DevPlatformers;
import ua.mcchickenstudio.opencreative.utils.world.platforms.HorizontalPlatformer;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>Settings</h1>
 * This class represents Settings, that stores
 * values which are used in plugin.
 */
public final class Settings {

    private final Groups groups;
    private final Commands commands;
    private final WebSettings webSettings;
    private final Requirements requirements;
    private final LobbySettings lobbySettings;
    private final CodingSettings codingSettings;
    private final EconomySettings economySettings;
    private final ItemFixerSettings itemFixerSettings;
    private final WorldFixerSettings worldFixerSettings;
    private final DownloaderSettings downloaderSettings;
    private final Set<Integer> recommendedWorldsIDs = new HashSet<>();
    private final Set<String> allowedResourcePackLinks = new HashSet<>();
    private final Set<String> messagesIgnoringReset = new HashSet<>();
    private final Map<Sounds, SettingsSound> sounds = new HashMap<>();
    private final Map<ItemsGroup, SettingsItemsGroup> itemsGroups = new HashMap<>();
    private boolean debug = false;
    private boolean maintenance = false;
    private boolean creativeChatEnabled = true;
    private boolean consoleCriticalErrors = true;
    private boolean consoleNotFoundMessage = false;
    private boolean consoleWarnings = true;
    private boolean consoleGriefStats = true;
    private boolean consoleSignEdits = true;
    private boolean notifyNoPlayersAround = true;
    private boolean cancelChatOnConfirmation = false;
    private boolean handleWorldChat = true;
    private boolean generateFlatWorldHigher = false;
    private boolean firstLaunch = false;
    private BukkitRunnable announcer;
    private PlayerListChanger listChanger = PlayerListChanger.FULL;

    public Settings() {
        if (!OpenCreative.getPlugin().getDataFolder().exists()) {
            firstLaunch = true;
        }
        OpenCreative.getPlugin().saveDefaultConfig();
        groups = new Groups();
        commands = new Commands();
        requirements = new Requirements();
        webSettings = new WebSettings();
        lobbySettings = new LobbySettings();
        codingSettings = new CodingSettings();
        economySettings = new EconomySettings();
        itemFixerSettings = new ItemFixerSettings();
        worldFixerSettings = new WorldFixerSettings();
        downloaderSettings = new DownloaderSettings();
    }

    /**
     * Loads settings values from configuration file.
     */
    public void load(boolean reload) {
        File configFile = new File(OpenCreative.getPlugin().getDataFolder(), "config.yml");
        FileConfiguration config = new YamlConfiguration();
        if (!configFile.exists()) {
            OpenCreative.getPlugin().saveDefaultConfig();
        }
        try {
            config.load(configFile);
            fillMissingLines(config);
            if (reload) {
                config.save(configFile);
                OpenCreative.getPlugin().reloadConfig();
            }
        } catch (Exception error) {
            String corruptedName = "config-corrupted-" + new SimpleDateFormat("hh-mm--dd-MM-yyyy")
                    .format(new Date()) + ".yml";
            OpenCreative.getPlugin().getLogger().severe(
                "Oops! Failed to load config.yml" + ErrorUtils.parseException(error, false) +
                        String.join("\n", "", "",
                                " ^ ^ ^ ^ ^",
                                "Seems like config.yml was corrupted, so we renamed it and replaced with default config.",
                                "For old config, see /plugins/OpenCreative/" + corruptedName,
                                "Maybe you forgot space, tab, or brackets {} []? See above for details."
                        ));
            try {
                File movedConfig = new File(OpenCreative.getPlugin().getDataFolder(), corruptedName);
                if (!configFile.renameTo(movedConfig)) {
                    OpenCreative.getPlugin().getLogger().severe("Failed to rename old config.yml to " + movedConfig.getName());
                }
            } catch (Exception ignored) {}
            OpenCreative.getPlugin().saveDefaultConfig();
            config = OpenCreative.getPlugin().getConfig();
        }
        allowedResourcePackLinks.clear();
        recommendedWorldsIDs.clear();
        messagesIgnoringReset.clear();
        sounds.clear();

        listChanger = PlayerListChanger.fromString(config.getString("hide-from-tab", "full"));
        recommendedWorldsIDs.addAll(config.getIntegerList("recommended-worlds"));
        allowedResourcePackLinks.addAll(config.getStringList("allowed-links.resource-pack"));
        messagesIgnoringReset.addAll(config.getStringList("messages.do-not-reset"));

        debug = config.getBoolean("debug", false);
        maintenance = config.getBoolean("maintenance", false);
        consoleCriticalErrors = config.getBoolean("messages.critical-errors", true);
        consoleNotFoundMessage = config.getBoolean("messages.not-found", false);
        consoleWarnings = config.getBoolean("messages.warnings", true);
        consoleGriefStats = config.getBoolean("messages.grief-stats", true);
        consoleSignEdits = config.getBoolean("messages.sign-edits", true);
        cancelChatOnConfirmation = config.getBoolean("messages.cancel-chat-on-confirmation", false);
        handleWorldChat = config.getBoolean("messages.handle-world-chat", true);

        boolean enabledWatchdog = config.getBoolean("watchdog.enabled", false);
        notifyNoPlayersAround = config.getBoolean("messages.notify-no-players-around", true);

        lobbySettings.load();
        requirements.load();
        itemFixerSettings.load();
        worldFixerSettings.load();
        economySettings.load();
        webSettings.load();
        downloaderSettings.load();
        groups.load();
        commands.load();

        String soundsTheme = config.getString("sounds.theme", "default");
        loadSounds(config, soundsTheme);
        loadWorldGenerators(config);

        if (maintenance) {
            OpenCreative.getPlugin().getLogger().warning("Maintenance mode is still enabled in config.yml, to disable: /maintenance end");
        }
        if (debug) {
            OpenCreative.getPlugin().getLogger().warning("Debug Mode is enabled in config.yml, some logs will appear in console.");
        }
        String platformsGeneratorId = config.getString("coding.platforms-generator", "horizontal");
        DevPlatformer platformer = DevPlatformers.getInstance().getById(platformsGeneratorId);
        if (platformer == null) {
            sendWarningErrorMessage("[PLATFORMERS] Unknown coding platform generator: " + platformsGeneratorId + ", using the default horizontal.");
            platformer = new HorizontalPlatformer();
        }
        OpenCreative.setDevPlatformer(platformer);

        OpenCreative.setStability(enabledWatchdog ? new Watchdog() : new DisabledWatchdog());
        codingSettings.load();
        loadExperiments(config);
        checkDebugAnnouncer();

        loadItems(config);
    }

    /**
     * Checks config for missing lines and compares
     * it with default config.yml.
     *
     * @param config config to check.
     */
    private void fillMissingLines(@NotNull FileConfiguration config) {
        try {
            InputStream input = OpenCreative.getPlugin().getResource("config.yml");
            if (input == null) {
                return;
            }
            YamlConfiguration defaultConfig = new YamlConfiguration();
            defaultConfig.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            List<String> addedKeys = new ArrayList<>();
            for (String key : defaultConfig.getKeys(true)) {
                if (key.equals("version")) continue;
                if (shouldNotAddDefaultKey(config, key)) continue;
                if (!config.contains(key, true)) {
                    config.set(key, defaultConfig.get(key));
                    addedKeys.add(key);
                }
            }
            if (!addedKeys.isEmpty()) {
                config.set("version", OpenCreative.getVersion());
                config.setComments("version", List.of("Last config update: " +
                        new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)").format(new Date())));
                OpenCreative.getPlugin().getLogger().warning("Added " + addedKeys.size() +
                        " missing lines in config.yml: " + String.join(", ", addedKeys));
                config.save(new File(OpenCreative.getPlugin().getDataFolder(), "config.yml"));
                OpenCreative.getPlugin().reloadConfig();
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to check config.yml", error);
        }
    }

    /**
     * Checks whether specified key should be added to config,
     * if it's missing in config.
     *
     * @param config config, that will be updated.
     * @param key key to check.
     * @return true - shouldn't set key, false - will be set.
     */
    private boolean shouldNotAddDefaultKey(FileConfiguration config, String key) {
        // Specified keys are keys from sections, that may contain
        // custom data by server owners. We will recover sections,
        // but not keys, if their sections exist.
        if (key.startsWith("groups.premium") && config.contains("groups", true)) return true;
        if (key.startsWith("sounds.christmas") && config.contains("sounds", true)) return true;
        if (key.startsWith("commands.onLobby") && config.contains("commands", true)) return true;
        if (key.startsWith("items.") && config.contains("items")) {
            String[] parts = key.split("\\.");
            // If config has item group section "items.lobby" - ignore.
            // If config doesn't have item group "items.lobby", it will be added.
            // If config doesn't have "items" section, it will be added.
            return parts.length >= 2 && config.contains("items." + parts[1], true);
        }
        return false;
    }

    private void loadWorldGenerators(FileConfiguration config) {
        WorldGenerators instance = WorldGenerators.getInstance();
        instance.clearWorldGenerators();
        generateFlatWorldHigher = config.getBoolean("generators.generate-flat-higher", false);
        if (config.getBoolean("generators.flat", true)) instance.registerWorldGenerator(new FlatGenerator());
        if (config.getBoolean("generators.empty", true)) instance.registerWorldGenerator(new EmptyGenerator());
        if (config.getBoolean("generators.water", true)) instance.registerWorldGenerator(new OceanGenerator());
        if (config.getBoolean("generators.large-biomes", false))
            instance.registerWorldGenerator(new LargeBiomesGenerator());
        ConfigurationSection customFlatsSection = config.getConfigurationSection("generators.custom-flats");
        if (customFlatsSection != null) {
            for (String customFlatId : customFlatsSection.getKeys(false)) {
                String generation = customFlatsSection.getString(customFlatId + ".generation");
                if (generation == null) continue;
                String iconMaterial = customFlatsSection.getString(customFlatId + ".icon", "GRASS_BLOCK");
                Material material = Material.getMaterial(iconMaterial.toUpperCase());
                if (material == null || !material.isItem()) material = Material.GRASS_BLOCK;
                boolean generateTrees = customFlatsSection.getBoolean(customFlatId + ".generate-trees", true);
                instance.registerWorldGenerator(new CustomFlatGenerator(customFlatId, new ItemStack(material), generation, generateTrees));
            }
        }
        ConfigurationSection templatesSection = config.getConfigurationSection("generators.templates");
        if (templatesSection != null) {
            for (String templateId : templatesSection.getKeys(false)) {
                boolean enabled = templatesSection.getBoolean(templateId + ".enabled", true);
                if (!enabled) continue;
                String folderName = templatesSection.getString(templateId + ".folder");
                if (folderName == null) continue;
                File template = new File(OpenCreative.getPlugin().getDataPath()
                        + File.separator + "templates" + File.separator + folderName);
                if (!template.exists() || !template.isDirectory()) {
                    sendWarningErrorMessage("Can't register template world " + templateId + " because it's folder " + folderName + " doesn't exist.");
                    continue;
                }
                String iconMaterial = templatesSection.getString(templateId + ".icon", "GRASS_BLOCK");
                Material material = Material.getMaterial(iconMaterial.toUpperCase());
                if (material == null || !material.isItem()) material = Material.GRASS_BLOCK;
                WorldTemplate generator = new WorldTemplate(templateId, new ItemStack(material), folderName);
                if (templatesSection.isConfigurationSection(templateId + ".spawn")) {
                    double x = templatesSection.getDouble(templateId + ".spawn.x", 0);
                    double y = templatesSection.getDouble(templateId + ".spawn.y", 70);
                    double z = templatesSection.getDouble(templateId + ".spawn.z", 0);
                    generator.changeLocation(new Location(null, x, y, z));
                }
                instance.registerWorldGenerator(generator);
            }
        }
        int registeredGenerators = instance.getWorldGenerators().size();
        if (registeredGenerators != 5) {
            OpenCreative.getPlugin().getLogger().info("Registered " + registeredGenerators + " world generators");
        }
    }

    public boolean isWorldGenerationUnavailable() {
        return WorldGenerators.getInstance().getWorldGenerators().isEmpty();
    }

    private void loadExperiments(FileConfiguration config) {
        int count = 0;
        ConfigurationSection section = config.getConfigurationSection("experiments");
        if (section == null) return;
        for (Experiment experiment : Experiments.getInstance().getExperiments()) {
            boolean enabled = section.getBoolean(experiment.getId(), false);
            Experiments.getInstance().setEnabled(experiment, enabled);
            if (enabled) count++;
        }
        if (count >= 1) {
            OpenCreative.getPlugin().getLogger().info("Enabled " + count + " experiments.");
        }
    }

    private void loadSounds(FileConfiguration config, String soundsTheme) {
        sounds.clear();
        ConfigurationSection soundsSection = config.getConfigurationSection("sounds." + soundsTheme);
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                try {
                    Sounds type = Sounds.valueOf(key.toUpperCase().replace("-", "_"));
                    String sound = soundsSection.getString(key + ".name", "");
                    float pitch = (float) soundsSection.getDouble(key + ".pitch", 1.0f);
                    sounds.put(type, new SettingsSound(sound, pitch));
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Sound " + key.toLowerCase() + " doesn't exist.");
                }
            }
        }
        if (!sounds.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Added " + sounds.size() + " custom sounds");
        }
    }

    private void loadItems(FileConfiguration config) {
        itemsGroups.clear();
        ConfigurationSection groupsSection = config.getConfigurationSection("items");
        if (groupsSection == null) {
            return;
        }
        for (String groupId : groupsSection.getKeys(false)) {
            ItemsGroup group = ItemsGroup.getById(groupId.toUpperCase().replace("-", "_"));
            if (group == null) {
                sendWarningErrorMessage("Unknown items kit in config: " + groupId);
                continue;
            }
            if (groupsSection.isString(groupId)) {
                itemsGroups.put(group, new SettingsItemsGroup());
                continue;
            }
            ConfigurationSection slots = groupsSection.getConfigurationSection(groupId);
            if (slots == null) continue;
            SettingsItemsGroup settingsItemsGroup = new SettingsItemsGroup();
            for (String slotString : slots.getKeys(false)) {
                int slot;
                try {
                    slot = Math.clamp(Integer.parseInt(slotString), 1, 36);
                } catch (Exception ignored) {
                    sendWarningErrorMessage("Unknown slot, not a number: " + slotString + " (kit: " + groupId + ")");
                    continue;
                }
                SettingsItem item = loadItem(slots, slotString);
                if (item == null) continue;
                settingsItemsGroup.setItem(slot, item);
            }
            itemsGroups.put(group, settingsItemsGroup);
        }
        if (!itemsGroups.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("Changed " + itemsGroups.size() + " item kits");
        }
    }

    private @Nullable SettingsItem loadItem(@NotNull ConfigurationSection section, @NotNull String slot) {
        if (section.isString(slot)) {
            // 1: "own-worlds"
            String typeString = section.getString(slot, "");
            Items itemType = Items.getById(typeString);
            if (itemType == null) {
                sendWarningErrorMessage("Unknown system item type " + typeString + " for item (kit: " + section.getName() + " in slot: " + slot + ")");
                return null;
            } else {
                return new SettingsPresetItem(itemType);
            }
        }
        if (section.isConfigurationSection(slot)) {
            section = section.getConfigurationSection(slot);
            if (section == null) return null;
            SettingsCustomItem item = new SettingsCustomItem();
            /*
             * 1:
             *   type: "own-worlds"
             *   material: "nether_star"
             *   name: "Create your worlds"
             *   lore:
             *     - "Use right click"
             *   amount: 1
             *   glowing: true
             */
            if (section.isString("type")) {
                // Preset will copy material, name, lore from Items enum.
                String typeString = section.getString("type", "");
                Items itemType = Items.getById(typeString);
                if (itemType == null) {
                    sendWarningErrorMessage("Unknown system item type " + typeString + " for item (kit: " + section.getName() + " in slot: " + slot + ")");
                } else {
                    item.setPreset(itemType);
                }

            }
            if (section.isString("data")) {
                // Data will copy all item content from specified text.
                item.setData(section.getString("data", ""));
            }

            // Overrides material
            if (section.isString("material")) {
                String materialString = section.getString("material", "");
                Material material = Material.matchMaterial(materialString);
                if (material == null) {
                    sendWarningErrorMessage("Unknown material " + materialString + " for item (kit: " + section.getName() + " in slot: " + slot + ")");
                } else if (!material.isItem()) {
                    sendWarningErrorMessage("Material " + materialString + " is not an obtainable item (kit: " + section.getName() + " in slot: " + slot + ")");
                } else {
                    item.setMaterial(material);
                }
            }
            if (section.isString("translation")) {
                // Overrides name and lore.
                item.setTranslationKey(section.getString("translation", ""));
            }


            // Overrides name
            if (section.isString("name")) item.setName(section.getString("name", ""));
            // Overrides lore
            if (section.isList("lore")) {
                List<String> list = section.getStringList("lore");
                item.setDescription(section.getString("lore", String.join("\n", list)));
            }

            // Overrides glowing
            if (section.isBoolean("glowing")) item.setGlowing(section.getBoolean("glowing", false));
            // Overrides amount
            if (section.isInt("amount")) item.setAmount(section.getInt("amount", 1));
            return item;
        }
        return null;
    }

    public boolean setSoundsTheme(String theme) {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        if (config.getConfigurationSection("sounds." + theme) == null) {
            return false;
        }
        loadSounds(config, theme);
        OpenCreative.getPlugin().getConfig().set("sounds.theme", theme);
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean addRecommendedWorld(int worldID) {
        if (recommendedWorldsIDs.contains(worldID)) {
            return false;
        }
        recommendedWorldsIDs.add(worldID);
        OpenCreative.getPlugin().getConfig().set("recommended-worlds", new ArrayList<>(recommendedWorldsIDs));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean removeRecommendedWorld(int worldID) {
        if (!recommendedWorldsIDs.contains(worldID)) {
            return false;
        }
        recommendedWorldsIDs.remove(worldID);
        OpenCreative.getPlugin().getConfig().set("recommended-worlds", new ArrayList<>(recommendedWorldsIDs));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean addMessageIgnoringReset(@NotNull String path) {
        if (messagesIgnoringReset.contains(path)) {
            return false;
        }
        messagesIgnoringReset.add(path);
        OpenCreative.getPlugin().getConfig().set("messages.do-not-reset", new ArrayList<>(messagesIgnoringReset));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public boolean removeMessageIgnoringReset(@NotNull String path) {
        if (!messagesIgnoringReset.contains(path)) {
            return false;
        }
        messagesIgnoringReset.remove(path);
        OpenCreative.getPlugin().getConfig().set("messages.do-not-reset", new ArrayList<>(messagesIgnoringReset));
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

    public void setCustomItem(ItemsGroup group, int slot, @NotNull ItemStack item) {
        String path = "items." + group.name().toLowerCase().replace("_", "-") + "." + slot;
        SettingsItemsGroup settingsGroup = itemsGroups.getOrDefault(group, new SettingsItemsGroup());
        if (item.isEmpty()) {
            OpenCreative.getPlugin().getConfig().set(path, null);
            settingsGroup.removeItem(slot);
        } else {
            Map<String, String> itemInfo = new HashMap<>();
            String data = ItemUtils.saveItemAsByteArray(item);
            itemInfo.put("data", data);
            OpenCreative.getPlugin().getConfig().set(path, itemInfo);
            SettingsCustomItem customItem = new SettingsCustomItem();
            customItem.setData(data);
            settingsGroup.setItem(slot, customItem);
        }
        OpenCreative.getPlugin().saveConfig();
        itemsGroups.put(group, settingsGroup);
    }

    public void setCustomItem(ItemsGroup group, int slot, @NotNull Items item) {
        String path = "items." + group.name().toLowerCase().replace("_", "-") + "." + slot;
        SettingsItemsGroup settingsGroup = itemsGroups.getOrDefault(group, new SettingsItemsGroup());
        OpenCreative.getPlugin().getConfig().set(path, item.name().toLowerCase().replace("_", "-"));
        SettingsPresetItem customItem = new SettingsPresetItem(item);
        settingsGroup.setItem(slot, customItem);
        OpenCreative.getPlugin().saveConfig();
        itemsGroups.put(group, settingsGroup);
    }

    public void resetItemsGroup(ItemsGroup group) {
        String path = "items." + group.name().toLowerCase();
        // Removes duplicate with underscore if exists
        OpenCreative.getPlugin().getConfig().set(path, null);
        path = path.replace("_", "-");
        OpenCreative.getPlugin().getConfig().set(path, null);

        itemsGroups.remove(group);
        for (ItemPair pair : group.getPairs()) {
            OpenCreative.getPlugin().getConfig().set(path + "." + pair.slot(),
                    pair.item().name().toLowerCase().replace("_", "-"));
        }

        OpenCreative.getPlugin().saveConfig();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        if (this.debug == debug) return;
        this.debug = debug;
        OpenCreative.getPlugin().getConfig().set("debug", debug);
        OpenCreative.getPlugin().saveConfig();
        OpenCreative.getPlugin().getLogger().info("Debug mode is " + (debug ? "enabled." : "disabled."));
        checkDebugAnnouncer();
    }

    /**
     * Checks whether it's maintenance mode or not.
     *
     * @return true - enabled, false - disabled.
     */
    public boolean isMaintenance() {
        return maintenance;
    }

    /**
     * Sets maintenance mode without command sender.
     * <p>
     * If maintenance mode is same as specified, nothing will be changed.
     * <p>
     * If true - will unload all worlds and teleport players to lobby,
     * and then prevent them from browsing and connecting to worlds.
     * <p>
     * If false - will allow players to visit their worlds.
     *
     * @param maintenance true - enabled, false - disabled.
     */
    public void setMaintenance(boolean maintenance) {
        setMaintenance(maintenance, null);
    }

    /**
     * Sets maintenance mode.
     * <p>
     * If maintenance mode is same as specified, nothing will be changed.
     * <p>
     * If true - will unload all worlds and teleport players to lobby,
     * and then prevent them from browsing and connecting to worlds.
     * <p>
     * If false - will allow players to visit their worlds.
     *
     * @param maintenance true - enabled, false - disabled.
     * @param sender      sender, who set maintenance mode.
     */
    public void setMaintenance(boolean maintenance, @Nullable CommandSender sender) {
        if (this.maintenance == maintenance) return;
        this.maintenance = maintenance;
        try {
            OpenCreative.getPlugin().getConfig().set("maintenance", maintenance);
            OpenCreative.getPlugin().saveConfig();
        } catch (Exception ignored) {
        }
        if (maintenance) {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode started! Unloading planets, please wait...");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_START.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.started"));
                for (Planet planet : OpenCreative.getPlanetsManager().getPlanets()) {
                    if (planet.isLoaded()) {
                        for (Player player : planet.getPlayers()) {
                            teleportToLobby(player);
                        }
                    }
                }
            }
            new MaintenanceStartEvent(sender).callEvent();
        } else {
            OpenCreative.getPlugin().getLogger().info("Maintenance mode ended, now players can play in worlds.");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Sounds.MAINTENANCE_END.play(onlinePlayer);
                onlinePlayer.sendMessage(getLocaleMessage("creative.maintenance.ended"));
            }
            new MaintenanceEndEvent(sender).callEvent();
        }
    }

    /**
     * Checks if debug mode is enabled in config.yml.
     */
    private void checkDebugAnnouncer() {
        if (announcer != null) {
            announcer.cancel();
        }
        if (debug) {
            announcer = new BukkitRunnable() {
                private final Component actionbar = MiniMessage.miniMessage()
                        .deserialize("<white>Open<gradient:#dbdbdb:#A3E2FF>Creative</gradient><color:#74D3FF>+ <white>" + OpenCreative.getVersion() + "<gray> Debug Mode. <white>Let's make it possible...");

                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("opencreative.debug")) {
                            Bukkit.getServer().sendActionBar(actionbar);
                        }
                    }
                }
            };
            announcer.runTaskTimer(OpenCreative.getPlugin(), 20L, 20L);
        } else {
            announcer = null;
        }
    }

    public Set<String> getAllowedResourcePackLinks() {
        return allowedResourcePackLinks;
    }

    public Set<Integer> getRecommendedWorldsIDs() {
        return recommendedWorldsIDs;
    }

    public boolean isCreativeChatEnabled() {
        return creativeChatEnabled;
    }

    public void setCreativeChatEnabled(boolean creativeChatEnabled) {
        this.creativeChatEnabled = creativeChatEnabled;
    }

    public PlayerListChanger getListChanger() {
        return listChanger;
    }

    /**
     * Returns instance of groups, that stores
     * all groups and contains methods to
     * get data from them.
     *
     * @return groups instance.
     */
    public @NotNull Groups getGroups() {
        return groups;
    }

    /**
     * Returns settings of item fixer.
     *
     * @return item fixer settings.
     */
    public @NotNull ItemFixerSettings getItemFixerSettings() {
        return itemFixerSettings;
    }

    /**
     * Returns settings of world fixer.
     *
     * @return world fixer settings.
     */
    public @NotNull WorldFixerSettings getWorldFixerSettings() {
        return worldFixerSettings;
    }

    /**
     * Returns settings of coding mode.
     *
     * @return coding settings.
     */
    public @NotNull CodingSettings getCodingSettings() {
        return codingSettings;
    }

    /**
     * Returns settings of requirements.
     *
     * @return requirements settings.
     */
    public @NotNull Requirements getRequirements() {
        return requirements;
    }

    /**
     * Returns settings of lobby world.
     *
     * @return lobby settings.
     */
    public @NotNull LobbySettings getLobbySettings() {
        return lobbySettings;
    }

    /**
     * Returns settings of web services.
     *
     * @return web settings.
     */
    public @NotNull WebSettings getWebSettings() {
        return webSettings;
    }

    /**
     * Returns settings of world downloader.
     *
     * @return world downloader settings.
     */
    public @NotNull DownloaderSettings getDownloaderSettings() {
        return downloaderSettings;
    }

    /**
     * Returns settings of economy.
     *
     * @return economy settings.
     */
    public @NotNull EconomySettings getEconomySettings() {
        return economySettings;
    }

    /**
     * Returns custom commands executions.
     *
     * @return commands events.
     */
    public @NotNull Commands getCommands() {
        return commands;
    }

    /**
     * Checks whether critical errors will be shown in console.
     *
     * @return true - will be shown, false - hidden.
     */
    public boolean shouldLogCriticalErrors() {
        return consoleCriticalErrors;
    }

    /**
     * Checks whether grief stats will be shown in console.
     *
     * @return true - will be shown, false - hidden.
     */
    public boolean shouldLogGriefStats() {
        return consoleGriefStats;
    }

    /**
     * Checks whether sign edits will be shown in console.
     *
     * @return true - will be shown, false - hidden.
     */
    public boolean shouldLogSignEdits() {
        return consoleSignEdits;
    }

    /**
     * Checks whether warnings will be shown in console.
     *
     * @return true - will be shown, false - hidden.
     */
    public boolean shouldLogWarnings() {
        return consoleWarnings;
    }

    /**
     * Checks whether not translated messages will be shown in console.
     *
     * @return true - will be shown, false - hidden.
     */
    public boolean shouldLogNotFoundMessages() {
        return consoleNotFoundMessage;
    }

    /**
     * Checks whether player's messages should be hidden from chat,
     * when they're changing world's or module's name or description.
     *
     * @return true - will be hidden, false - shown.
     */
    public boolean shouldCancelChatOnConfirmation() {
        return cancelChatOnConfirmation;
    }

    /**
     * Checks whether world chat and messages starting with ! will be
     * handled by OpenCreative+.
     *
     * @return true - will be handled, false - ignored, other plugins will be responsible.
     */
    public boolean shouldHandleWorldChat() {
        return handleWorldChat;
    }

    /**
     * Returns a map of sounds and custom sounds from config.
     *
     * @return map of sounds.
     */
    public Map<Sounds, SettingsSound> getSounds() {
        return sounds;
    }

    /**
     * Returns a map of item groups and custom item groups from config.
     *
     * @return map of item groups.
     */
    public Map<ItemsGroup, SettingsItemsGroup> getItemsGroups() {
        return itemsGroups;
    }

    /**
     * Checks whether player should see "No players around message"
     * message, when there is no other players in their world.
     *
     * @return true - will be shown, false - will be hidden.
     */
    public boolean shouldNotifyAboutNoPlayersAround() {
        return notifyNoPlayersAround;
    }

    /**
     * Returns set of messages paths, that will be recovered
     * on resetting locale from old localization file to new.
     *
     * @return set of messages paths, that should be saved.
     */
    public Set<String> getMessagesIgnoringReset() {
        return messagesIgnoringReset;
    }

    /**
     * Checks whether flat world generator should
     * generate a world starting with Y: 0, instead
     * of Y: -64.
     *
     * @return true - will generate higher (old versions), false - lower (newer).
     */
    public boolean shouldGenerateFlatWorldHigher() {
        return generateFlatWorldHigher;
    }

    /**
     * Checks whether plugin was launched for first
     * time, without /plugins/OpenCreative folder.
     *
     * @return true - first time launched, false - was launched on server before.
     */
    public boolean isFirstLaunch() {
        return firstLaunch;
    }

    public enum PlayerListChanger {

        SPECTATOR,
        FULL,
        NONE;

        public static PlayerListChanger fromString(String string) {
            for (PlayerListChanger changer : PlayerListChanger.values()) {
                if (string.equalsIgnoreCase(changer.name())) {
                    return changer;
                }
            }
            return FULL;
        }
    }
}
