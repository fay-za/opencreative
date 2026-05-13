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
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

/**
 * <h1>Placeholder</h1>
 * This class represents a special text, that will be replaced
 * while parsing text coding values.
 *
 * @see Placeholders
 * @see KeyPlaceholder
 */
public abstract class Placeholder implements ExtensionContent {

    /**
     * Returns <code>true</code> if specified text contains
     * placeholders that can be parsed with this class.
     *
     * @param text text to check.
     * @return true - placeholders detected, false - not detected.
     */
    public abstract boolean matches(String text);

    /**
     * Returns text with parsed placeholders from this class.
     *
     * @param text    text to parse.
     * @param handler action handler.
     * @param action  action.
     * @return parsed text
     */
    public abstract @NotNull String parse(String text, ActionsHandler handler, Action action);

    @Override
    public String toString() {
        return "Placeholder (" + getName() + " from " + getExtensionId() + ")";
    }
}
