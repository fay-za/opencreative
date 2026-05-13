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

import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

public final class RayTrace {

    public static MovingObjectPosition rayCast(final float yaw, final float pitch, final AxisAlignedBB bb, Vec3 locationIn, double dist, BuildSpeed s) {
        double lastX = locationIn.xCoord,
                lastY = locationIn.yCoord,
                lastZ = locationIn.zCoord;
        Vec3 vec3 = new Vec3(lastX, lastY, lastZ),
                vec31 = getVectorForRotation(pitch, yaw, s),
                vec32 = vec3.add(new Vec3(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist));
        return bb.calculateIntercept(vec3, vec32);
    }

    public static Vec3 getVectorForRotation(final float pitch, final float yaw, BuildSpeed s) {
        float f = FastMath.cos(-yaw * 0.017453292F - (float) Math.PI),
                f1 = GeneralMath.sin(-yaw * 0.017453292F - (float) Math.PI, s),
                f2 = -GeneralMath.cos(-pitch * 0.017453292F, s),
                f3 = GeneralMath.sin(-pitch * 0.017453292F, s);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}
