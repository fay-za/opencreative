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

package ua.mcchickenstudio.opencreative.managers.stability;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

public interface StabilityManager extends Manager {

    /**
     * Returns stability state of storage.
     *
     * @return state of storage.
     */
    @NotNull StabilityState getStorageState();

    /**
     * Returns stability state of database connection.
     *
     * @return state of database.
     */
    @NotNull StabilityState getDatabaseState();

    /**
     * Returns stability state of memory.
     *
     * @return state of RAM.
     */
    @NotNull StabilityState getMemoryState();

    /**
     * Returns stability state of ticks.
     *
     * @return state of TPS.
     */
    @NotNull StabilityState getTicksState();

    /**
     * Returns stability state of all systems.
     *
     * @return state of plugin
     */
    @NotNull StabilityState getState();

    /**
     * Checks if plugin's stability is fine.
     *
     * @return true - stability is fine, false - not stable.
     */
    default boolean isFine() {
        return getState() == StabilityState.FINE;
    }

    /**
     * Checks if plugin's stability is very unstable.
     *
     * @return true - stability is bad, false - normal.
     */
    default boolean isVeryBad() {
        return getState() == StabilityState.NIGHTMARE;
    }

}
