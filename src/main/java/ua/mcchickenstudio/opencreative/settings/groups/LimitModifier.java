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

/**
 * <h1>LimitModifier</h1>
 * This class represents modifier of limit, that is used
 * for calculating limits.
 *
 * @param limit    original limit.
 * @param modifier modifier of world's players count.
 */
public record LimitModifier(int limit, int modifier) {

    /**
     * Returns limit by adding modifier multiplied by players count
     * to original limit.
     *
     * @param playersCount count of players.
     * @return limit, that can be used for checks.
     */
    public int calculateLimit(int playersCount) {
        return limit + modifier * playersCount;
    }

}
