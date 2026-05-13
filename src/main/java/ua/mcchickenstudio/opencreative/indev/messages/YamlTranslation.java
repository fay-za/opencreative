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

package ua.mcchickenstudio.opencreative.indev.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningMessage;

public class YamlTranslation implements TranslationManager {

    private final Map<String, Map<String,String>> translation = new HashMap<>();

    public void init() {
        translation.clear();
        File localesFolder = getLocalesFolder();
        if (!localesFolder.exists()) {
            try {
                if (!localesFolder.mkdir()) sendWarningErrorMessage("Failed to create folder: " + localesFolder.getPath());
            } catch (Exception exception) {
                sendWarningMessage("Failed to create folder: " + localesFolder.getPath(), exception);
            }
        }
        File[] localesFolders = localesFolder.listFiles();
        if (localesFolders == null || localesFolders.length == 0) {
            addDefaultLocales(false);
            localesFolders = localesFolder.listFiles();
            if (localesFolders == null || localesFolders.length == 0) {
                sendWarningErrorMessage("Can't load default locales");
                return;
            }
        }
        for (File localeFile : localesFolders) {
            if (!localeFile.isDirectory() && localeFile.getPath().endsWith(".yml")) {
                String translationName = localeFile.getName().replace(".yml","");
                Map<String, String> newTranslation = new HashMap<>();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
                for (String key : config.getKeys(true)) {
                    List<String> contentList = config.getStringList(key);
                    String contentString = config.getString(key);
                    if (!contentList.isEmpty()) {
                        contentString = String.join("\n", contentList);
                    }
                    newTranslation.put(key, contentString);
                }
                OpenCreative.getPlugin().getLogger().info("Loaded locale: " + translationName);
                translation.put(translationName, newTranslation);
            }
        }

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public @NotNull Component getMessage(@NotNull String path) {
        String defaultLanguage = "en";
        return textToComponent(getContent(path, defaultLanguage))
                .replaceText(new PlaceholderReplacer("prefix", "OpenCreative").get());
    }

    @Override
    public @NotNull Component getMessage(@NotNull String path, @NotNull String locale) {
        return textToComponent(getContent(path, locale))
                .replaceText(new PlaceholderReplacer("prefix", "OpenCreative").get());
    }

    @Override
    public @NotNull Component getMessage(@NotNull String path, @NotNull OfflinePlayer player) {
        // FIXME: Add multi-language support.
        return getMessage(path);
    }

    @Override
    public @NotNull Component getMessage(@NotNull String path, @NotNull CommandSender sender) {
        // FIXME: Add multi-language support.
        if (sender instanceof OfflinePlayer player) {
            return getMessage(path, player);
        } else {
            return getMessage(path);
        }
    }

    public @Nullable Map<String,String> getTranslation(@NotNull String locale) {
        return translation.get(locale);
    }

    public @NotNull String getContent(@NotNull String path, @NotNull String locale) {
        Map<String,String> localeTranslation = getTranslation(locale);
        if (localeTranslation == null) {
            return path;
        }
        return localeTranslation.getOrDefault(path, path);
    }

    public void addDefaultLocales(boolean replace) {
        OpenCreative.getPlugin().saveResource("locales" + File.separator + "en.yml", replace);
    }

    @Override
    public @NotNull String getName() {
        return "Yaml Translation Manager";
    }

    public @NotNull File getLocalesFolder() {
        return new File(OpenCreative.getPlugin().getDataFolder().getPath() + File.separator + "locales");
    }

    private static @NotNull Component textToComponent(@NotNull String text) {
        text = text.replace(LegacyComponentSerializer.SECTION_CHAR, LegacyComponentSerializer.AMPERSAND_CHAR);
        if (text.contains("&")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        } else {
            return MiniMessage.miniMessage().deserialize(text);
        }
    }

}
