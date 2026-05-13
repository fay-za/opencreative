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

package ua.mcchickenstudio.opencreative.utils.millennium.vectors;

import org.bukkit.util.Vector;

public final class Vec3 {
    /**
     * X coordinate of Vec3D
     */
    public final double xCoord;

    /**
     * Y coordinate of Vec3D
     */
    public final double yCoord;

    /**
     * Z coordinate of Vec3D
     */
    public final double zCoord;

    public Vec3(double x, double y, double z) {
        if (x == -0.0D) {
            x = 0.0D;
        }

        if (y == -0.0D) {
            y = 0.0D;
        }

        if (z == -0.0D) {
            z = 0.0D;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3(Vector vector) {
        this.xCoord = vector.getX();
        this.yCoord = vector.getY();
        this.zCoord = vector.getZ();
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize() {
        double d0 = Math.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public Vec3 subtract(Vec3 vec) {
        return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3 subtract(double x, double y, double z) {
        return this.addVector(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec) {
        return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(double x, double y, double z) {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(Vec3 vec) {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(Vec3 vec) {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(Vec3 vec, double x) {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d0 * d0 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (x - this.xCoord) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(Vec3 vec, double y) {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (y - this.yCoord) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(Vec3 vec, double z) {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (z - this.zCoord) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public Vec3 multiply(double m) {
        return new Vec3(this.xCoord * m, this.yCoord * m, this.zCoord);
    }

    public double speed() {
        return Math.sqrt(xCoord * xCoord + zCoord * zCoord);
    }
}