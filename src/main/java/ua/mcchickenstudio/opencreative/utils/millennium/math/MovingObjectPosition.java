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

public final class MovingObjectPosition {

    /**
     * What type of ray trace hit was this? 0 = block, 1 = entity
     */
    public final MovingObjectType typeOfHit;
    public final EnumFacing sideHit;
    /**
     * The vector position of the hit
     */
    public final Vec3 hitVec;
    private final BlockPos blockPos;

    public MovingObjectPosition(Vec3 vec3, EnumFacing facing) {
        this(MovingObjectType.BLOCK, vec3, facing, BlockPos.ORIGIN);
    }

    public MovingObjectPosition(MovingObjectType typeOfHitIn, Vec3 hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        this.typeOfHit = typeOfHitIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vec3(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
    }

    public String toString() {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + '}';
    }

    public enum MovingObjectType {
        MISS,
        BLOCK,
        ENTITY
    }
}
