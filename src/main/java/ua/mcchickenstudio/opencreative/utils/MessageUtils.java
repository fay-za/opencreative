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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.indev.messages.PlaceholderReplacer;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.PAPIUtils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>MessageUtils</h1>
 * This class contains utils, that can return messages,
 * modify and format them. Uses translation files.
 */
public final class MessageUtils {

    private static final Map<Integer, Long> recentPlanetMessages = new HashMap<>();
    private static final Pattern CAMEL_TO_KEBAB = Pattern.compile("([a-z])([A-Z])");
    private static final Map<Character, String> LEGACY_TO_MINI = Map.ofEntries(
            Map.entry('0', "<black>"),
            Map.entry('1', "<dark_blue>"),
            Map.entry('2', "<dark_green>"),
            Map.entry('3', "<dark_aqua>"),
            Map.entry('4', "<dark_red>"),
            Map.entry('5', "<dark_purple>"),
            Map.entry('6', "<gold>"),
            Map.entry('7', "<gray>"),
            Map.entry('8', "<dark_gray>"),
            Map.entry('9', "<blue>"),
            Map.entry('a', "<green>"),
            Map.entry('b', "<aqua>"),
            Map.entry('c', "<red>"),
            Map.entry('d', "<light_purple>"),
            Map.entry('e', "<yellow>"),
            Map.entry('f', "<white>"),
            Map.entry('k', "<obfuscated>"),
            Map.entry('l', "<bold>"),
            Map.entry('m', "<strikethrough>"),
            Map.entry('n', "<underlined>"),
            Map.entry('o', "<italic>"),
            Map.entry('r', "<reset>")
    );
    private static File localizationFile;
    private static FileConfiguration localizationConfig;

    /**
     * Converts text into component by deserializing it with
     * legacy serializer (if message has & or § symbol), or with
     * minimessage format.
     *
     * @param text text to convert.
     * @return text component.
     */
    public static @NotNull Component toComponent(String text) {
        return MiniMessage.miniMessage().deserialize(fromLegacyToMiniMessage(text));
    }

    /**
     * Converts text into component by deserializing it with
     * legacy serializer (if message has & or § symbol), or with
     * minimessage format, but without hover and click events.
     *
     * @param input text to convert.
     * @return text component without hover and click events.
     */
    public static @NotNull Component fromInputToComponent(@NotNull String input) {
        return MiniMessage.miniMessage().deserialize(fromLegacyToMiniMessage(input))
                .clickEvent(null).hoverEvent(null);
    }

    /**
     * Checks if text has & or § symbol.
     *
     * @param text text to check.
     * @return true - if contains & or §, false - otherwise.
     */
    public static boolean isLegacyFormat(String text) {
        return text.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) != -1 || text.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1;
    }

    /**
     * Returns text that could be shortened to specified length,
     * if text length is greater than specified one.
     * <pre>
     * {@code
     * substring("Hello",5); // "Hello"
     * substring("World",4); // "Wo..."
     * substring("World",0); // ""
     * }
     * </pre>
     *
     * @param text   text to substring.
     * @param length maximum length of text.
     * @return shortened text.
     */
    public static String substring(String text, int length) {
        if (text.length() <= length || length <= 0) return text;
        String dots = text.endsWith("...") ? "" : text.endsWith("..") ? "." : "...";
        return text.substring(0, length - dots.length()) + dots;
    }

    /**
     * Loads localization file, that located in ./plugins/OpenCreative/locales/ folder.
     */
    public static void loadLocalizationFile() {

        File localeFile = new File((OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator), getLanguage() + ".yml");
        if (localeFile.exists()) {
            localizationFile = localeFile;
        } else {
            String defaultLanguage = getLanguage().equalsIgnoreCase("ru")
                    ? "ru" : getLanguage().equalsIgnoreCase("ua") ? "ua" : "en";
            OpenCreative.getPlugin().getConfig().set("messages.locale", defaultLanguage);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "en.yml", false);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "ru.yml", false);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "ua.yml", false);
            OpenCreative.getPlugin().reloadConfig();
            localeFile = new File((OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator), defaultLanguage + ".yml");
        }

        addMissingMessageLines();
        localizationConfig = YamlConfiguration.loadConfiguration(localeFile);
    }

    public static int addMissingMessageLines() {
        String selectedLang = OpenCreative.getPlugin().getConfig().getString("messages.locale", "en");
        File folder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator);
        File file = new File(folder.getPath() + File.separator + selectedLang + ".yml");
        if (!file.exists()) {
            return -1;
        }
        InputStream input = OpenCreative.getPlugin().getResource("locales/" + selectedLang + ".yml");
        if (input == null) {
            return -1;
        }
        YamlConfiguration resource = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
        YamlConfiguration localization = YamlConfiguration.loadConfiguration(file);
        int changes = 0;
        for (String key : resource.getKeys(true)) {
            if (!localization.contains(key)) {
                localization.set(key, resource.get(key));
                changes++;
            }
        }
        if (changes == 0) return 0;
        try {
            localization.save(file);
            OpenCreative.getPlugin().getLogger().info("Added " + changes + " lines to localization file.");
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to update lines in localization file " + file.getName(), error);
        }
        return changes;
    }

    /**
     * Sets message in localization file and saves changes.
     *
     * @param path   path of message.
     * @param object message.
     */
    public static void setMessage(@NotNull String path, @Nullable Object object) {
        getLocalization().set(path, object);
        try {
            getLocalization().save(getLocalizationFile());
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to set message in localization file " + getLocalizationFile().getName(), error);
        }
    }

    /**
     * Checks if localization file exists in locales directory.
     *
     * @param languageName name of language, without ".yml".
     * @return true - exists, false - not exists.
     */
    public static boolean localizationFileExists(String languageName) {
        return "en".equalsIgnoreCase(languageName) || "ru".equalsIgnoreCase(languageName) || "ua".equalsIgnoreCase(languageName) || new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales" + File.separator, languageName + ".yml").exists();
    }

    /**
     * Returns prefix of plugin, that will be used for
     * sending some messages in game.
     *
     * @return prefix of plugin.
     */
    private static @NotNull String getPrefix() {
        String prefix = OpenCreative.getPlugin().getConfig().getString("messages.prefix");
        if (prefix == null) {
            prefix = ChatColor.translateAlternateColorCodes('&', "&6 Worlds &8| &f");
            OpenCreative.getPlugin().getConfig().set("messages.prefix", "&6 Worlds &8| &f");
            OpenCreative.getPlugin().saveConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    /**
     * Returns creative chat prefix, that will be used
     * in creative chat messages.
     *
     * @return prefix of creative chat.
     */
    private static String getCreativeChatPrefix() {
        String prefix = OpenCreative.getPlugin().getConfig().getString("messages.cc-prefix");
        if (prefix == null) {
            prefix = ChatColor.translateAlternateColorCodes('&', "&6 Creative Chat &8| &7");
            OpenCreative.getPlugin().getConfig().set("messages.cc-prefix", "&6 Creative Chat &8| &7");
            OpenCreative.getPlugin().reloadConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    /**
     * Returns server branding, that should be changed by
     * server owners in plugin's config.yml. Used in lobby
     * message.
     *
     * @return server branding.
     */
    private static String getBranding() {
        String prefix = OpenCreative.getPlugin().getConfig().getString("messages.branding");
        if (prefix == null) {
            prefix = ChatColor.translateAlternateColorCodes('&', "&fOpen&7Creative&a+");
            OpenCreative.getPlugin().getConfig().set("messages.branding", "&fOpen&7Creative&a+");
            OpenCreative.getPlugin().reloadConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    /**
     * Returns current language of OpenCreative+.
     *
     * @return global language of plugin.
     */
    public static String getLanguage() {
        Object language = OpenCreative.getPlugin().getConfig().get("messages.locale");
        if (language != null) {
            return String.valueOf(language);
        } else {
            Object defaultLanguage = "en";
            OpenCreative.getPlugin().getConfig().set("messages.locale", defaultLanguage);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "en.yml", false);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "ru.yml", false);
            OpenCreative.getPlugin().saveResource("locales" + File.separator + "ua.yml", false);
            OpenCreative.getPlugin().reloadConfig();
            return "en";
        }
    }

    /**
     * Returns current localization file.
     *
     * @return localization file.
     */
    private static File getLocalizationFile() {
        return localizationFile;
    }

    /**
     * Returns translation stored in FileConfiguration.
     *
     * @return translation config.
     */
    public static FileConfiguration getLocalization() {
        return localizationConfig;
    }

    /**
     * Returns component message from translation.
     *
     * @param messageID id of message.
     * @return component message, or "Error | Not found message.path...", if message was not found.
     */
    public static @NotNull Component getLocaleComponent(String messageID) {
        return toComponent(getLocaleMessage(messageID));
    }

    /**
     * Returns component message from translation
     * with parsed player placeholders.
     *
     * @param messageID id of message.
     * @param player    player to parse.
     * @return component message, or "Error | Not found message.path...", if message was not found.
     */
    public static @NotNull Component getPlayerLocaleComponent(@NotNull String messageID, @NotNull OfflinePlayer player) {
        return toComponent(getPlayerLocaleMessage(messageID, player));
    }

    /**
     * Returns component message from translation
     * with parsed player placeholders and custom placeholders.
     *
     * @param messageID             id of message.
     * @param player                player to parse.
     * @param placeholdersAndValues placeholders and values <p>
     *                              It must start with placeholder and end with value. <p>
     *                              {@code "placeholder", value, "placeholder-2", value-2}
     * @return component message, or "Error | Not found message.path...", if message was not found.
     */
    public static @NotNull Component getComponentWithPlaceholders(@NotNull String messageID,
                                                                  @NotNull OfflinePlayer player,
                                                                  @Nullable Object... placeholdersAndValues) {
        Component result = toComponent(getPlayerLocaleMessage(messageID, player));
        if (placeholdersAndValues == null || placeholdersAndValues.length == 0) return result;

        for (int i = 0; i < placeholdersAndValues.length; i += 2) {
            if (i + 1 >= placeholdersAndValues.length) break;
            String placeholder = String.valueOf(placeholdersAndValues[i]);
            Object rawReplacement = placeholdersAndValues[i + 1];

            Component replacement;
            if (rawReplacement instanceof Component component) {
                replacement = component;
            } else if (rawReplacement instanceof String string) {
                replacement = toComponent(string);
            } else {
                replacement = Component.text(String.valueOf(rawReplacement));
            }

            result = result.replaceText(TextReplacementConfig.builder()
                    .match("%" + Pattern.quote(placeholder) + "%")
                    .replacement(replacement)
                    .build());
        }

        return result;
    }

    /**
     * Returns component message from translation
     * with parsed player placeholders.
     *
     * @param messageID           id of message.
     * @param returnDetailedError if true - returns detailed error when message
     *                            was not found, false - will return only path.
     * @return component message.
     */
    public static Component getLocaleComponent(String messageID, boolean returnDetailedError) {
        return toComponent(getLocaleMessage(messageID, returnDetailedError));
    }

    /**
     * Returns message from translation.
     *
     * @param messageID id of message.
     * @return translated message, or "Error | Not found message.path...", if message was not found.
     */
    public static String getLocaleMessage(String messageID) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales" + File.separator + getLanguage() + ".yml";
        } else {
            return ChatColor.translateAlternateColorCodes('&',
                    originalMessage.replace("%prefix%", getPrefix())
                            .replace("%branding%", getBranding())
                            .replace("%cc-prefix%", getCreativeChatPrefix()));
        }
    }

    /**
     * Returns message from translation
     * with parsed player placeholders.
     *
     * @param messageID id of message.
     * @param player    player to parse.
     * @return translated message, or "Error | Not found message.path...", if message was not found.
     */
    public static String getPlayerLocaleMessage(String messageID, OfflinePlayer player) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales" + File.separator + getLanguage() + ".yml";
        } else {
            return ChatColor.translateAlternateColorCodes('&', parsePAPI(player, originalMessage
                    .replace("%prefix%", getPrefix())
                    .replace("%branding%", getBranding())
                    .replace("%cc-prefix%", getCreativeChatPrefix())
                    .replace("%player%", player.getName() == null ? "Unknown player" : player.getName())));
        }
    }

    /**
     * Returns message from translation
     * with parsed player placeholders.
     *
     * @param messageID           id of message.
     * @param returnDetailedError if true - returns detailed error when message
     *                            was not found, false - will return only path.
     * @return translated message.
     */
    public static String getLocaleMessage(String messageID, boolean returnDetailedError) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            if (returnDetailedError) {
                return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales" + File.separator + getLanguage() + ".yml";
            } else {
                return messageID;
            }
        } else {
            return ChatColor.translateAlternateColorCodes('&', originalMessage.replace("%prefix%", getPrefix()).replace("%branding%", getBranding()).replace("%cc-prefix%", getCreativeChatPrefix()));
        }
    }

    /**
     * Returns item name from translation.
     *
     * @param nameID id of message.
     * @return translated item name, or "Not found: message.path", if message was not found.
     */
    public static String getLocaleItemName(String nameID) {
        String originalName = getLocalization().getString(nameID);
        if (originalName == null || originalName.equalsIgnoreCase("null")) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found item name " + nameID + " in localization file!");
            return "§fNot found: " + nameID;
        } else {
            if (originalName.length() > 50) originalName = originalName.substring(0, 50);
            return ChatColor.translateAlternateColorCodes('&', originalName.replace("%prefix%", getPrefix()).replace("%cc-prefix%", getCreativeChatPrefix()).replace("%branding%", getBranding()));
        }
    }

    /**
     * Returns item description from translation
     *
     * @param descriptionID id of message.
     * @return translated item description, or "Not found item description...", if message was not found.
     */
    public static List<String> getLocaleItemDescription(String descriptionID) {
        List<String> originalDescription = getLocalization().getStringList(descriptionID);
        List<String> parsedDescription = new ArrayList<>();
        if (originalDescription.isEmpty()) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found item description " + descriptionID);
            parsedDescription.add("§6Not found item description");
            parsedDescription.add("§6" + descriptionID);
            parsedDescription.add("§fPlease send this to server administration!");
            parsedDescription.add("§f They need to fill this line in ");
            parsedDescription.add("§f localization file: locales" + File.separator + getLanguage() + ".yml");
        } else {
            for (String descriptionLine : originalDescription) {
                parsedDescription.add(ChatColor.translateAlternateColorCodes('&', descriptionLine.replace("%prefix%", getPrefix()).replace("%cc-prefix%", getCreativeChatPrefix()).replace("%branding%", getBranding())));
            }
        }
        return parsedDescription;
    }

    /**
     * Returns book pages from translation.
     *
     * @param localizationID id of message.
     * @return translated book pages, or "Not found pages...", if message was not found.
     */
    @SuppressWarnings("unused")
    public static @NotNull List<Component> getBookPages(@NotNull Player player, @NotNull String localizationID) {
        List<String> foundPages = getLocalization().getStringList(localizationID);
        List<Component> pages = new ArrayList<>();
        if (foundPages.isEmpty()) {
            if (OpenCreative.getSettings().shouldLogNotFoundMessages())
                ErrorUtils.sendWarningErrorMessage("Not found book pages " + localizationID);
            pages.add(Component.text("§4Not found pages: §0" + localizationID + " \nPlease report server administration, they need to fill this line in locales" + File.separator + getLanguage() + ".yml"));
        } else {
            for (String page : foundPages) {
                pages.add(MiniMessage.miniMessage().deserialize(fromLegacyToMiniMessageBook(page)
                        .replace("%prefix%", getPrefix())
                        .replace("%cc-prefix%", getCreativeChatPrefix())
                        .replace("%version%", OpenCreative.getVersion())
                ));
            }
        }
        return pages;
    }

    /**
     * Returns elapsed time from old time to current with localized message. For example: if elapsed time is 2 seconds, it will return "2 sec ago".
     **/
    public static @NotNull String getElapsedTime(long currentTime, long oldTime) {

        String elapsedTime = "";

        long elapsedTimeInSeconds = (currentTime - oldTime) / 1000;
        long elapsedTimeInMinutes = elapsedTimeInSeconds / 60;
        long elapsedTimeInHours = elapsedTimeInMinutes / 60;
        long elapsedTimeInDays = elapsedTimeInHours / 24;

        elapsedTimeInSeconds %= 60;
        elapsedTimeInMinutes %= 60;
        elapsedTimeInHours %= 24;

        if (elapsedTimeInDays > 0)
            elapsedTime = elapsedTime.concat(elapsedTimeInDays + " " + getLocaleMessage("time.days", false) + " ");
        if (elapsedTimeInHours > 0)
            elapsedTime = elapsedTime.concat(elapsedTimeInHours + " " + getLocaleMessage("time.hours", false) + " ");
        if (elapsedTimeInMinutes > 0)
            elapsedTime = elapsedTime.concat(elapsedTimeInMinutes + " " + getLocaleMessage("time.minutes", false) + " ");
        if (elapsedTimeInSeconds > 0)
            elapsedTime = elapsedTime.concat(elapsedTimeInSeconds + " " + getLocaleMessage("time.seconds", false) + " ");
        if ((currentTime - oldTime) < 1000) elapsedTime = getLocaleMessage("time.less-second", false) + " ";

        return elapsedTime + getLocaleMessage("time.ago", false);

    }

    /**
     * Converts duration in millis to user-friendly text.
     * <pre>
     * {@code
     * convertTime(1000); // 1 sec
     * convertTime(1); // less a second
     * convertTime(60000); // 1 min
     * convertTime(121000); // 2 min 1 sec
     * }
     * </pre>
     *
     * @param duration duration to convert
     * @return user-friendly duration text.
     */
    public static @NotNull String convertTime(long duration) {

        String convertedTime = "";

        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        if (days > 0) convertedTime = convertedTime.concat(days + " " + getLocaleMessage("time.days", false) + " ");
        if (hours > 0) convertedTime = convertedTime.concat(hours + " " + getLocaleMessage("time.hours", false) + " ");
        if (minutes > 0)
            convertedTime = convertedTime.concat(minutes + " " + getLocaleMessage("time.minutes", false) + " ");
        if (seconds > 0) convertedTime = convertedTime.concat(seconds + " " + getLocaleMessage("time.seconds", false));
        if (duration < 1000) convertedTime = getLocaleMessage("time.less-second", false);
        if (duration < 0) convertedTime = "∞";

        return convertedTime;

    }

    /**
     * Sends message by path for every player in planet, if it was not sent recently.
     *
     * @param planet        planet to send message.
     * @param path          path of message.
     * @param placeholder   placeholder for message.
     * @param clickCommand  command for click.
     * @param hoverPath     path of hover message.
     * @param onceInSeconds cooldown of messages.
     */
    public static void sendMessageOnce(@NotNull Planet planet, @NotNull String path,
                                       @NotNull PlaceholderReplacer placeholder,
                                       @Nullable String clickCommand,
                                       @Nullable String hoverPath,
                                       int onceInSeconds) {
        if (cantSendOnceMessage(planet, onceInSeconds)) return;
        for (Player player : planet.getPlayers()) {
            Component text = getPlayerLocaleComponent(path, player).replaceText(placeholder.get());
            if (clickCommand != null) {
                text = text.clickEvent(ClickEvent.runCommand(clickCommand));
            }
            if (hoverPath != null) {
                text = text.hoverEvent(HoverEvent.showText(getPlayerLocaleComponent(hoverPath, player)));
            }
            player.sendMessage(text);
        }

    }

    public static boolean cantSendOnceMessage(@NotNull Planet planet, int onceInSeconds) {
        long currentTime = System.currentTimeMillis();
        if (recentPlanetMessages.get(planet.getId()) != null) {
            long timeInMap = recentPlanetMessages.get(planet.getId());
            long elapsedTime = currentTime - timeInMap;
            long elapsedSeconds = elapsedTime / 1000;
            if (elapsedSeconds < onceInSeconds) return true;
        }
        recentPlanetMessages.put(planet.getId(), currentTime);
        return false;
    }

    public static void clearOnceMessages(Planet planet) {
        recentPlanetMessages.remove(planet.getId());
    }

    /**
     * Returns string, that parsed planet lines: planet name, description, online, reputation, owner, id, category, uniques, last activity time, creation time.
     **/
    public static String parsePlanetLines(Planet planet, String string) {
        String planetReputation = String.valueOf(planet.getInformation().getReputation());

        if (planet.getInformation().getReputation() >= 1) planetReputation = "§a+" + planetReputation;
        else if (planet.getInformation().getReputation() <= -1) planetReputation = "§c" + planetReputation;
        else planetReputation = "§e" + planetReputation;

        return parsePAPI(Bukkit.getOfflinePlayer(planet.getOwner()), string)
                .replace("%planetName%", planet.getInformation().getDisplayName())
                .replace("%planetOnline%", String.valueOf(planet.getOnline()))
                .replace("%planetOwner%", planet.getOwner())
                .replace("%planetID%", String.valueOf(planet.getId()))
                .replace("%planetCustomID%", planet.getInformation().getCustomID())
                .replace("%planetCategory%", planet.getInformation().getCategory().getLocaleName())
                .replace("%planetUniques%", String.valueOf(planet.getInformation().getUniques()))
                .replace("%planetReputation%", planetReputation)
                .replace("%planetLastTime%", getElapsedTime(System.currentTimeMillis(), planet.getLastActivityTime()))
                .replace("%planetCreationTime%", getElapsedTime(System.currentTimeMillis(), planet.getCreationTime()));
    }

    public static Component parsePlanetLines(Planet planet, Component component) {
        String planetReputation = String.valueOf(planet.getInformation().getReputation());

        if (planet.getInformation().getReputation() >= 1) planetReputation = "§a+" + planetReputation;
        else if (planet.getInformation().getReputation() <= -1) planetReputation = "§c" + planetReputation;
        else planetReputation = "§e" + planetReputation;

        return component
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetName%")
                        .replacement(planet.getInformation().displayName()).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetOnline%")
                        .replacement(String.valueOf(planet.getOnline())).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetOwner%")
                        .replacement(planet.getOwner()).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetID%")
                        .replacement(String.valueOf(planet.getId())).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetCustomID%")
                        .replacement(planet.getInformation().getCustomID()).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetCategory%")
                        .replacement(toComponent(planet.getInformation().getCategory().getLocaleName())).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetUniques%")
                        .replacement(String.valueOf(planet.getInformation().getUniques())).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetReputation%")
                        .replacement(toComponent(planetReputation)).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetLastTime%")
                        .replacement(getElapsedTime(System.currentTimeMillis(), planet.getLastActivityTime())).build())
                .replaceText(TextReplacementConfig.builder()
                        .match("%planetCreationTime%")
                        .replacement(getElapsedTime(System.currentTimeMillis(), planet.getCreationTime())).build());
    }

    /**
     * Returns string with parsed module lines (
     */
    public static String parseModuleLines(Module module, String string) {
        String reputation = String.valueOf(module.getInformation().getReputation());

        if (module.getInformation().getReputation() >= 1) reputation = "§a+" + reputation;
        else if (module.getInformation().getReputation() <= -1) reputation = "§c" + reputation;
        else reputation = "§e" + reputation;

        return parsePAPI(Bukkit.getOfflinePlayer(module.getOwner()), string)
                .replace("%moduleName%", module.getInformation().getDisplayName())
                .replace("%moduleDescription%", module.getInformation().getDescription())
                .replace("%moduleOwner%", module.getOwnerName())
                .replace("%moduleID%", String.valueOf(module.getId()))
                .replace("%moduleDownloads%", String.valueOf(module.getInformation().getDownloads()))
                .replace("%moduleReputation%", reputation)
                .replace("%moduleCreationTime%", getElapsedTime(System.currentTimeMillis(), module.getInformation().getCreationTime()))
                .replace("%moduleSharing%", getLocaleMessage("modules.sharing." + (module.getInformation().isPublic() ? "public" : "private"), false));
    }

    /**
     * Returns string, that parsed player's placeholders if PlaceholderAPI is working.
     **/
    public static String parsePAPI(OfflinePlayer player, String string) {
        if (HookUtils.isPlaceholderAPIEnabled) {
            try {
                return PAPIUtils.parsePlaceholdersAPI(player, string);
            } catch (Exception ignored) {
                return string;
            }
        } else {
            return string;
        }
    }

    public static double parseTicks(@NotNull String message, double defaultTicks) {
        Double ticks = parseTicks(message);
        return ticks == null ? defaultTicks : ticks;
    }

    public static @Nullable Double parseTicks(@NotNull String message) {
        if (message.isEmpty()) return null;
        double ticks;
        double modifier = switch (message.toLowerCase().charAt(message.length() - 1)) {
            case 's' -> 20;
            case 'm' -> 1200;
            case 'h' -> 72000;
            case 'd' -> 1728000;
            default -> 1;
        };
        if (modifier != 1) {
            message = message.substring(0, message.length() - 1);
        }
        try {
            ticks = Double.parseDouble(message);
        } catch (NumberFormatException ignored) {
            return null;
        }
        return ticks * modifier;
    }

    /**
     * Checks whether message exists in localization file.
     *
     * @param messageID id of message.
     * @return true - message exists, false - not found.
     */
    public static boolean messageExists(String messageID) {
        String originalMessage = getLocalization().getString(messageID);
        return originalMessage != null && !originalMessage.equalsIgnoreCase("null");
    }

    /**
     * Converts text from snake_case, camelCase and PascalCase
     * to kebab-case, that can be used in YAML.
     * <pre>
     * {@code
     * toKebabCase("EnderDragon"); // "ender-dragon"
     * toKebabCase("ARMOR_STAND"); // "armor-stand"
     * toKebabCase("Creeper"); // "creeper"
     * }
     * </pre>
     *
     * @param input text to convert.
     * @return kebab-cased text.
     */
    public static @NotNull String toKebabCase(@NotNull String input) {
        if (input.contains("_")) {
            return input.toLowerCase().replace("_", "-");
        }
        return CAMEL_TO_KEBAB.matcher(input).replaceAll("$1-$2").toLowerCase();
    }

    /**
     * Converts legacy text with § or & to MiniMessage format,
     * so this text can be deserialized to MiniMessage.
     * <pre>
     * {@code
     * fromLegacyToMiniMessage("&4Hello"); // "<red>Hello"
     * fromLegacyToMiniMessage("&nHello"); // "<underlined>Hello"
     * fromLegacyToMiniMessage("&rHello<red>"); // "<reset>Hello<red>"
     * }
     * </pre>
     *
     * @param input text to convert.
     * @return text, that can be used as MiniMessage.
     */
    public static @NotNull String fromLegacyToMiniMessage(@NotNull String input) {
        if (input.contains("§")) {
            input = input.replace('§', '&');
        }
        if (!input.contains("&")) return input;
        StringBuilder result = new StringBuilder();
        boolean hadStyle = false;
        for (int charIndex = 0; charIndex < input.length(); charIndex++) {
            // for characters in text
            char current = input.charAt(charIndex);
            if (current == '&' && charIndex + 7 < input.length() && input.charAt(charIndex + 1) == '#') {
                // for legacy gradient format &#ffffff
                String hex = input.substring(charIndex + 2, charIndex + 8); // "ffffff"
                if (hex.matches("[0-9A-Fa-f]{6}")) {
                    result.append("<#").append(hex).append(">");
                    charIndex += 7;
                    if (hadStyle) result.append("<reset>");
                    continue;
                }
            }
            if (current == '&' && charIndex + 13 < input.length() && input.charAt(charIndex + 1) == 'x') {
                // for legacy gradient format &x&f&f&f&f&f&f
                StringBuilder gradientColor = new StringBuilder(); // ffffff
                boolean isValidFormat = true;
                for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                    //
                    // &x&a&b&c&d&e&j
                    // charIndex = 0 -> current = &
                    //   &f
                    //   23
                    // j = 0 a, 1 b, 2 c, 3 d, 4 e, 5 j
                    //
                    int ampersandIndex = charIndex + 2 + colorIndex * 2; // f
                    if (input.charAt(ampersandIndex) == '&' && ampersandIndex + 1 < input.length()) {
                        gradientColor.append(input.charAt(ampersandIndex + 1));
                    } else {
                        isValidFormat = false;
                        break;
                    }
                }
                if (isValidFormat) {
                    result.append("<#").append(gradientColor).append(">");
                    charIndex += 13;
                    if (hadStyle) result.append("<reset>");
                    continue;
                }
            }
            if (current == '&' && charIndex + 1 < input.length()) {
                // for classic legacy colors and styles
                char code = Character.toLowerCase(input.charAt(charIndex + 1));
                String replacement = LEGACY_TO_MINI.get(code);
                boolean isDecoration = code == 'k' || code == 'n' || code == 'm' || code == 'l' || code == 'o';
                if (replacement != null) {
                    result.append(replacement);
                    if (hadStyle && !isDecoration) {
                        // If it's color, then reset decorations,
                        // like legacy behaviour
                        result.append("<reset>");
                        hadStyle = false;
                    } else if (isDecoration) {
                        hadStyle = true;
                    }
                    charIndex++;
                    continue;
                }
            }
            if (current == '\n') {
                if (hadStyle) {
                    // If it's new line, then reset decorations,
                    // like legacy behaviour
                    result.append("<reset>");
                    hadStyle = false;
                }
            }
            result.append(current);
        }
        return result.toString();
    }

    /**
     * Converts legacy text with § or & to book's MiniMessage format,
     * so this text can be deserialized to MiniMessage and used
     * in written books.
     * <pre>
     * {@code
     * fromLegacyToMiniMessage("&4Hello"); // "<red>Hello"
     * fromLegacyToMiniMessage("&nHello"); // "<underlined>Hello"
     * fromLegacyToMiniMessage("&rHello<red>"); // "<reset>Hello<red>"
     * }
     * </pre>
     *
     * @param input text to convert.
     * @return text, that can be used as MiniMessage in books.
     */
    public static @NotNull String fromLegacyToMiniMessageBook(@NotNull String input) {
        if (input.contains("§")) {
            input = input.replace('§', '&');
        }
        if (!input.contains("&")) return input;
        StringBuilder result = new StringBuilder();
        boolean hadStyle = false;
        for (int charIndex = 0; charIndex < input.length(); charIndex++) {
            // for characters in text
            char current = input.charAt(charIndex);
            if (current == '&' && charIndex + 7 < input.length() && input.charAt(charIndex + 1) == '#') {
                // for legacy gradient format &#ffffff
                String hex = input.substring(charIndex + 2, charIndex + 8); // "ffffff"
                if (hex.matches("[0-9A-Fa-f]{6}")) {
                    result.append("<#").append(hex).append(">");
                    charIndex += 7;
                    if (hadStyle) result.append("<reset><black>");
                    continue;
                }
            }
            if (current == '&' && charIndex + 13 < input.length() && input.charAt(charIndex + 1) == 'x') {
                // for legacy gradient format &x&f&f&f&f&f&f
                StringBuilder gradientColor = new StringBuilder(); // ffffff
                boolean isValidFormat = true;
                for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                    //
                    // &x&a&b&c&d&e&j
                    // charIndex = 0 -> current = &
                    //   &f
                    //   23
                    // j = 0 a, 1 b, 2 c, 3 d, 4 e, 5 j
                    //
                    int ampersandIndex = charIndex + 2 + colorIndex * 2; // f
                    if (input.charAt(ampersandIndex) == '&' && ampersandIndex + 1 < input.length()) {
                        gradientColor.append(input.charAt(ampersandIndex + 1));
                    } else {
                        isValidFormat = false;
                        break;
                    }
                }
                if (isValidFormat) {
                    result.append("<#").append(gradientColor).append(">");
                    charIndex += 13;
                    if (hadStyle) result.append("<reset><black>");
                    continue;
                }
            }
            if (current == '&' && charIndex + 1 < input.length()) {
                // for classic legacy colors and styles
                char code = Character.toLowerCase(input.charAt(charIndex + 1));
                String replacement = LEGACY_TO_MINI.get(code);
                boolean isDecoration = code == 'k' || code == 'n' || code == 'm' || code == 'l' || code == 'o';
                if (replacement != null) {
                    result.append(replacement);
                    if (code == 'r') {
                        result.append("<black>");
                    }
                    if (hadStyle && !isDecoration) {
                        // If it's color, then reset decorations,
                        // like legacy behaviour
                        result.append("<reset><black>");
                        hadStyle = false;
                    } else if (isDecoration) {
                        hadStyle = true;
                    }
                    charIndex++;
                    continue;
                }
            }
            result.append(current);
        }
        return result.toString();
    }

}
