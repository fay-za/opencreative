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

package ua.mcchickenstudio.opencreative.settings.groups;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LimitType {

    ENTITIES("entities-amount", 50, 50),
    CODE_OPERATIONS("executor-calls", 100, 100),
    SCOREBOARDS("scoreboards-amount", 10, 1),
    BOSSBARS("bossbars-amount", 10, 1),
    RECIPES("recipes-amount", 10, 0),
    REDSTONE_OPERATIONS("redstone-changes", 100, 20),
    OPENING_INVENTORIES("opening-inventories", 5),
    SENDING_WEB_REQUESTS("sending-web-requests", 5),
    PHYSICAL_OBJECTS("physical-objects", 140, 5),
    VARIABLES("variables-amount", 50, 50),
    CODING_ERRORS("errors-amount", 10),
    CODING_PLATFORMS("coding-platforms", 4),
    CODING_ACTIONS_CALLS("coding-actions-calls", 400, 0),
    LIST_ELEMENTS_CHANGES("changing-list-elements", 50, 10),
    TARGETS_CHANGES("changing-targets", 500, 100),
    REPEATS_AMOUNT("repeats-amount", 100, 10),
    MODIFYING_BLOCKS("modifying-blocks", 5000),
    BUILDERS_AMOUNT("builders-amount", 10),
    DEVELOPERS_AMOUNT("developers-amount", 10),
    BLACKLISTED_AMOUNT("blacklisted-amount", 10),
    WHITELISTED_AMOUNT("whitelisted-amount", 10),
    SELECTED_LINES_AMOUNT("selected-lines-amount", 3);

    private final String path;
    private final int defaultLimit;
    private final int defaultModifier;

    LimitType(@NotNull String path, int defaultLimit, int defaultModifier) {
        this.path = path;
        this.defaultLimit = defaultLimit;
        this.defaultModifier = defaultModifier;
    }

    LimitType(@NotNull String path, int defaultLimit) {
        this(path, defaultLimit, 0);
    }

    public static @Nullable LimitType getByPath(@NotNull String id) {
        for (LimitType type : LimitType.values()) {
            if (type.getPath().equalsIgnoreCase(id.replace("_", "-"))) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns default modifier of limit type.
     *
     * @return default limit.
     */
    public int getDefaultLimit() {
        return defaultLimit;
    }

    /**
     * Returns default modifier of limit type.
     *
     * @return default modifier.
     */
    public int getDefaultModifier() {
        return defaultModifier;
    }

    /**
     * Returns path for getting limit and
     * modifier from config.
     *
     * @return path in config.
     */
    public @NotNull String getPath() {
        return path;
    }

}
