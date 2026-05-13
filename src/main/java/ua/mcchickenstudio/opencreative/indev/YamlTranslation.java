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

package ua.mcchickenstudio.opencreative.indev;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

public class YamlTranslation implements TranslationManager {

    private final @NotNull List<@NotNull Translation> translations = new ArrayList<>();

    @Override
    public @NotNull List<Translation> getTranslations() {
        return new ArrayList<>(translations);
    }

    @Override
    public @Nullable Translation getTranslation(@NotNull String lang) {
        for (Translation translation : getTranslations()) {
            if (translation.getLang().equals(lang)) {
                return translation;
            }
        }
        return null;
    }

    @Override
    public @NotNull Component getLocaleComponent(@NotNull String id, @NotNull String lang) {
        Translation translation = getTranslation(lang);
        if (translation == null) return Component.text(id).hoverEvent(HoverEvent.showText(
                Component.text("Not found translation file: .plugins/OpenCreative/locales/ " + lang + ".yml.").color(NamedTextColor.YELLOW)));
        if (translation.getMessages().containsKey(id)) {
            return toComponent(translation.getMessages().get(id));
        }
        return Component.text(id).hoverEvent(HoverEvent.showText(
                Component.text("Not found translation for this message in .plugins/OpenCreative/locales/" + lang + ".yml. Please report about this to server administration.").color(NamedTextColor.YELLOW)));
    }

    @Override
    public boolean hasLocaleComponent(@NotNull String id, @NotNull String lang) {
        Translation translation = getTranslation(lang);
        return translation != null && translation.getMessages().containsKey(id);
    }

    @Override
    public @NotNull Component getLocaleComponent(@NotNull String id, @NotNull String lang, @NotNull Component def) {
        return hasLocaleComponent(id,lang) ? getLocaleComponent(id,lang) : def;
    }

    @Override
    public @NotNull Component getLocaleComponent(@NotNull String id, @NotNull OfflinePlayer player) {
        return MessageUtils.getLocaleComponent(id);
    }

    @Override
    public @NotNull Component getLocaleComponent(@NotNull String id, @NotNull OfflinePlayer player, @NotNull Component def) {
        String playerLanguage = "en";
        return hasLocaleComponent(id,playerLanguage) ? getLocaleComponent(id,playerLanguage) : def;
    }

    @Override
    public void init() {
        /*
         * Loads translations from file
         */
        try {
            File localesFolder = new File(OpenCreative.getPlugin().getDataFolder() + File.separator + "locales");
            if (localesFolder.exists() && !localesFolder.isDirectory()) {
                if (!localesFolder.delete()) {
                    sendDebug("Failed to delete `./plugins/OpenCreative/locales` file.");
                }
            }
            if (!localesFolder.exists()) {
                if (!localesFolder.mkdirs()) {
                    sendDebug("Failed to create `./plugins/OpenCreative/locales` folder.");
                }
            }
            File[] localesFiles = localesFolder.listFiles();
            if (localesFiles == null) return;
            for (File localeFile : localesFiles) {
                if (localeFile.getPath().endsWith(".yml")) {
                    Translation translation = new Translation(localeFile.getName(),new ItemStack(Material.APPLE));
                    FileConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
                    for (String key : config.getKeys(true)) {
                        String message = config.getString(key);
                        if (message != null) {
                            translation.getMessages().put(key,message);
                        }
                    }
                    if (!translation.getMessages().isEmpty()) {
                        translations.add(translation);
                    }
                }
            }
        } catch (Exception exception) {
            sendCriticalErrorMessage("Cannot load localization files.",exception);
        }


    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Translation Manager";
    }
}
