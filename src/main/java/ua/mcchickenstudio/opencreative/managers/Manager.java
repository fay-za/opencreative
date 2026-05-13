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

package ua.mcchickenstudio.opencreative.managers;

/**
 * This interface represents a manager, that controls
 * something and can be replaced with your realization.
 */
public interface Manager {

    /**
     * Initialization of manager.
     */
    void init();

    /**
     * Checks if manager is ready to work.
     *
     * @return true - if enabled, false - disabled.
     */
    boolean isEnabled();

    /**
     * Returns name of manager, that will be
     * displayed by request in the logs.
     *
     * @return name of manager.
     */
    String getName();

}
