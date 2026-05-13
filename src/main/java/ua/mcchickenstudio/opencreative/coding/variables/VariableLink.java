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

package ua.mcchickenstudio.opencreative.coding.variables;


import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>VariableLink</h1>
 * This class represents a link to existing or not existing world variable.
 * Links, as variables, have specified name and id. It's used for searching
 * and getting variables values.
 */
public final class VariableLink {

    private final @NotNull String name;
    private final @NotNull VariableType type;

    /**
     * Creates instance of variable link.
     *
     * @param name name of variable.
     * @param type type of variable (local, global, saved).
     */
    public VariableLink(@NotNull String name, @NotNull VariableType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns type of variable (local, global, saved).
     *
     * @return type of variable.
     */
    public @NotNull VariableType getVariableType() {
        return type;
    }

    /**
     * Returns name of variable.
     *
     * @return name of variable.
     */
    public @NotNull String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "null! " + name + " - " + type.name();
    }

    public enum VariableType {

        /**
         * Local variables are stored per code line (main action handler).
         */
        LOCAL(ChatColor.RED),
        /**
         * Global variables are stored when world is loaded.
         */
        GLOBAL(ChatColor.YELLOW),
        /**
         * Saved variables will be stored forever.
         */
        SAVED(ChatColor.GREEN);

        private final ChatColor color;

        VariableType(ChatColor color) {
            this.color = color;
        }

        public static VariableType getEnum(String string) {
            for (VariableType type : VariableType.values()) {
                if (type.name().equals(string.toUpperCase())) {
                    return type;
                }
            }
            return null;
        }

        public ChatColor getColor() {
            return color;
        }

        public String getLocalized() {
            return getLocaleMessage("items.developer.variable." + name().toLowerCase());
        }
    }

}
