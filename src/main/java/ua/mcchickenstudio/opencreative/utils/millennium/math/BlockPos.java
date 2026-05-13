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

import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3i;

public class BlockPos extends Vec3i {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public BlockPos add(double x, double y, double z) {
        return new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    /**
     * Add the given Vector to this BlockPos
     */
    public BlockPos add(Vec3i vec) {
        return new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    /**
     * Subtract the given Vector from this BlockPos
     */
    public BlockPos subtract(Vec3i vec) {
        return new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    /**
     * Multiply every coordinate by the given factor
     */
    public BlockPos multiply(int factor) {
        return new BlockPos(this.getX() * factor, this.getY() * factor, this.getZ() * factor);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    /**
     * Offset this BlockPos n blocks in the given direction
     */
    public BlockPos offset(EnumFacing facing, int n) {
        return new BlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    /**
     * Calculate the cross product of this BlockPos and the given Vector. Version of crossProduct that returns a
     * BlockPos instead of a Vec3i
     */
    public BlockPos crossProductBP(Vec3i vec) {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vec3i crossProduct(Vec3i vec) {
        return this.crossProductBP(vec);
    }
}
