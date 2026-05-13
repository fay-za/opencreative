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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>KeyPlaceholder</h1>
 * This class represents a key placeholder, that
 * has keys that will be replaced, if text will contain
 * them in "percents format": <code>%example%</code>
 * Should be used for creation.
 */
public abstract class KeyPlaceholder extends Placeholder {

    private final static int limit = 20;
    private final static Pattern PATTERN_PLACEHOLDER = Pattern.compile("%[A-Za-z0-9]+%");
    private final String[] keys;

    public KeyPlaceholder(String... keys) {
        this.keys = keys;
    }

    public static Pattern getPatternPlaceholder() {
        return PATTERN_PLACEHOLDER;
    }

    @Override
    public boolean matches(String text) {
        if (!text.contains("%")) return false;
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            String placeholder = matcher.group();
            String key = placeholder.substring(1, placeholder.length() - 1);
            for (String listedKey : keys) {
                if (key.equals(listedKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns array of keys that can be replaced, without %.
     *
     * @return array of keys.
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * Parses placeholder key, without %.
     *
     * @param key     key to replace.
     * @param handler action handler.
     * @param action  action
     * @return string - if parsed, or null - nothing parsed.
     */
    public abstract @Nullable String parseKey(String key, ActionsHandler handler, Action action);

    @Override
    public @NotNull String parse(String text, ActionsHandler handler, Action action) {
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(text);
        StringBuilder result = new StringBuilder();
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count >= limit) break;
            String key = matcher.group();
            key = key.substring(1, key.length() - 1);
            String replacement = parseKey(key, handler, action);
            if (replacement == null) replacement = "%" + key + "%";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

}
