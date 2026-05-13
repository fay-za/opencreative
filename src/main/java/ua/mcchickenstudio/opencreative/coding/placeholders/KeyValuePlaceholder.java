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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KeyValuePlaceholder extends Placeholder {

    private static final Pattern PATTERN = Pattern.compile("%([A-Za-z0-9_]+)\\(([^)]+)\\)");
    private static final int limit = 20;
    private final String[] keys;

    public KeyValuePlaceholder(String... keys) {
        this.keys = keys;
    }

    public static Pattern getPattern() {
        return PATTERN;
    }

    public String[] getKeys() {
        return keys;
    }

    public abstract @Nullable String parseKeyValue(String key, String value, ActionsHandler handler, Action action);

    @Override
    public boolean matches(String text) {
        if (!text.contains("%") && !text.contains("(") && !text.contains(")")) return false;
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (Arrays.asList(keys).contains(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull String parse(String text, ActionsHandler handler, Action action) {
        Matcher matcher = PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count >= limit) break;
            String key = matcher.group(1);
            String value = matcher.group(2);
            String replacement = parseKeyValue(key, value, handler, action);
            if (replacement == null) {
                replacement = "%" + key + "(" + value + ")";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
