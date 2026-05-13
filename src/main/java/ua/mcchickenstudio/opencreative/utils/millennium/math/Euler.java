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

import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

import static java.lang.Math.PI;

public final class Euler {

    public static Vec2 calculateVec2Vec(final Vec3 from, final Vec3 to) {
        final Vec3 diff = to.subtract(from);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0F / PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(diff.yCoord, distance) * 180.0F / PI));
        return new Vec2(yaw, pitch);
    }

}
