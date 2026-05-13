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

package ua.mcchickenstudio.opencreative.utils.millennium.math;

public final class GeneralMath {
    public static float sin(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                ? (float) Math.sin(value)
                : (s.equals(BuildSpeed.FAST)
                ? FastMath.sin(value)
                : FastMath.fastCos(value));
    }

    public static float cos(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                ? (float) Math.cos(value)
                : (s.equals(BuildSpeed.FAST)
                ? FastMath.cos(value)
                : FastMath.fastCos(value));
    }
}
