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
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

/**
 * <h1>Placeholders</h1>
 * This class represents a placeholders storage, that
 * has methods to register custom placeholders.
 *
 * @see Placeholder
 */
public final class Placeholders {

    private static Placeholders instance;
    private final List<Placeholder> placeholders = new ArrayList<>();

    /**
     * Returns instance of placeholders controller class.
     *
     * @return instance of placeholders.
     */
    public synchronized static @NotNull Placeholders getInstance() {
        if (instance == null) {
            instance = new Placeholders();
            instance.registerDefaults();
        }
        return instance;
    }

    /**
     * Registers default placeholders.
     */
    private void registerDefaults() {
        registerPlaceholder(new SymbolPlaceholder());
        registerPlaceholder(new PlayerPlaceholder());
        registerPlaceholder(new TargetPlaceholder());
        registerPlaceholder(new EntityPlaceholder());
        registerPlaceholder(new RandomPlaceholder());
        registerPlaceholder(new EventPlaceholder());
        registerPlaceholder(new PlanetPlaceholder());
        registerPlaceholder(new VarPlaceholder());
        registerPlaceholder(new ListPlaceholder());
    }

    /**
     * Registers placeholder, that will be parsed in text coding values.
     *
     * @param placeholder placeholder to register.
     */
    public void registerPlaceholder(@NotNull Placeholder placeholder) {
        if (placeholder instanceof KeyPlaceholder key) {
            if (key.getKeys().length == 0) {
                sendWarningErrorMessage("[PLACEHOLDERS] " + placeholder + " will be not registered, because has 0 keys.");
                return;
            }
            for (Placeholder listedPlaceholder : placeholders) {
                if (listedPlaceholder instanceof KeyPlaceholder key2) {
                    Set<String> sameKeys = getSameKeys(key, key2);
                    if (sameKeys.isEmpty()) break;
                    sendWarningErrorMessage("[PLACEHOLDERS] Same placeholders keys conflict " + key + ", " + key2 + " in: " + String.join(", ", sameKeys));
                }
            }
        }
        sendDebug("[PLACEHOLDERS] Registered " + placeholder);
        placeholders.add(placeholder);
    }

    /**
     * Unregisters placeholder if list contains it.
     *
     * @param placeholder placeholder to unregister.
     */
    public void unregisterPlaceholder(@NotNull Placeholder placeholder) {
        placeholders.remove(placeholder);
    }

    /**
     * Returns a copy of list that contains all registered placeholders.
     *
     * @return placeholders list.
     */
    public @NotNull List<Placeholder> getPlaceholders() {
        return new ArrayList<>(placeholders);
    }

    private @NotNull Set<String> getSameKeys(@NotNull KeyPlaceholder first, @NotNull KeyPlaceholder second) {
        Set<String> sameKeys = new HashSet<>();
        for (String key : first.getKeys()) {
            if (Arrays.asList(second.getKeys()).contains(key)) {
                sameKeys.add(key);
            }
        }
        return sameKeys;
    }

    /**
     * Returns text with parsed placeholders.
     *
     * @param text    text to parse.
     * @param handler action handler.
     * @param action  action.
     * @return parsed text.
     */
    public @NotNull String parsePlaceholders(String text, ActionsHandler handler, Action action) {
        text = text.replace("\\n", "\n");
        try {
            for (Placeholder placeholder : placeholders) {
                if (placeholder.matches(text)) {
                    text = placeholder.parse(text, handler, action);
                }
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("[PLACEHOLDERS] Can't parse placeholder", error);
        }
        return text;
    }

}
