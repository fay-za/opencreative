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

package ua.mcchickenstudio.opencreative.indev.values;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>CodingValue</h1>
 * This class represents a coding value, that will be used
 * for storing data in variables and event values.
 */
public abstract class CodingValue<T> {

    private final String id;
    private final Material icon;
    private final Material pane;

    /**
     * Constructor of coding value.
     * @param id short id of coding value that will be used for identifying value.
     *           <p>
     *           It must be lower-snake-cased, for example: "text", "event_value".
     *           If some of registered coding values has same ID as new, it will be not added.
     * @param icon icon of event value that will be displayed in event values list.
     * @param pane category of event value for event values list.
     */
    public CodingValue(@NotNull String id, @NotNull Material icon, @NotNull Material pane) {
        this.id = id;
        this.icon = icon;
        this.pane = pane;
    }

    /**
     * Parses object and creates value from it.
     * @param value value to parse.
     * @param type type of saver (yaml or json).
     * @return value, or null - if failed to deserialize.
     */
    public abstract @Nullable T deserialize(@NotNull Object value,
                                            @NotNull Saver type);

    /**
     * Saves value as string or map object to save it in file.
     * @param value value to save.
     * @param type type of saver (yaml or json).
     * @return value, converted to YAML object.
     */
    public abstract @NotNull Object serialize(@NotNull T value,
                                              @NotNull Saver type);

    /**
     * Type of value saver format.
     */
    public enum Saver {

        /**
         * Used for saving arguments in Code Script.
         */
        YAML,

        /**
         * Used for saving variables.
         */
        JSON,

        /**
         * Used for saving items data.
         */
        ITEM

    }

}
