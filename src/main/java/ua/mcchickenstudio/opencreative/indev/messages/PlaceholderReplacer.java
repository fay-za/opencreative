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
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class PlaceholderReplacer {

    private final List<String> placeholders = new ArrayList<>();
    private final List<Component> replacements = new ArrayList<>();

    public PlaceholderReplacer(Object... objects) {
        if (objects.length == 0) return;
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (i % 2 == 0) { // 0, 2, 4 - placeholders
                String text = object == null ? "null" : object.toString();
                placeholders.add(text);
            } else { // 1, 3, 5 - replacements
                Component replacement;
                if (object instanceof String string) {
                    replacement = Component.text(string);
                } else if (object instanceof Component component) {
                    replacement = component;
                } else {
                    String text = object == null ? "null" : object.toString();
                    replacement = Component.text(text);
                }
                replacements.add(replacement);
            }
        }
    }

    public @NotNull TextReplacementConfig get() {
        TextReplacementConfig.Builder config = TextReplacementConfig.builder();
        for (int i = 0; i < placeholders.size(); i++) {
            if (i >= replacements.size()) {
                break;
            }
            config.match("%" + placeholders.get(i) + "%")
                    .replacement(replacements.get(i));
        }
        return config.build();
    }

    public @NotNull Component apply(@NotNull Component input) {
        Component output = input;
        for (int i = 0; i < placeholders.size(); i++) {
            if (i >= replacements.size()) break;
            @RegExp String placeholder = "%" + placeholders.get(i) + "%";
            Component replacement = replacements.get(i);
            output = output.replaceText(TextReplacementConfig.builder()
                    .match(placeholder)
                    .replacement(replacement)
                    .build());
        }
        return output;
    }

    public @NotNull Component applyPlayer(@NotNull Component input,
                                          @NotNull OfflinePlayer player) {
        Component output = apply(input);

        String text = MiniMessage.miniMessage().serialize(output);
        text = parsePAPI(player, text);
        text = text.replace(LegacyComponentSerializer.SECTION_CHAR,
                LegacyComponentSerializer.AMPERSAND_CHAR);
        text = fromLegacyToMiniMessage(text).replace("\\","");

        return MiniMessage.miniMessage().deserialize(text);
    }

}
