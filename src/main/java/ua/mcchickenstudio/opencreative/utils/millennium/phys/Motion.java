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

package ua.mcchickenstudio.opencreative.utils.millennium.phys;

import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

public final class Motion {

    private final MotionValue x, y, z;

    /**
     * Create an empty constructor if we do not want initial values for our motion.
     */
    public Motion() {
        this.x = new MotionValue(0.0D);
        this.y = new MotionValue(0.0D);
        this.z = new MotionValue(0.0D);
    }

    /**
     * Set an initial value for our base motion.
     */
    public Motion(final double x, final double y, final double z) {
        this.x = new MotionValue(x);
        this.y = new MotionValue(y);
        this.z = new MotionValue(z);
    }

    /**
     * Set an initial value for our base motion.
     */
    public Motion(final MotionValue x, final MotionValue y, final MotionValue z) {
        this.x = new MotionValue(x.get());
        this.y = new MotionValue(y.get());
        this.z = new MotionValue(z.get());
    }

    public void set(final Vec3 vector) {
        this.x.set(vector.xCoord);
        this.y.set(vector.yCoord);
        this.z.set(vector.zCoord);
    }

    public void add(final Vec3 vector) {
        this.x.add(vector.xCoord);
        this.y.add(vector.yCoord);
        this.z.add(vector.zCoord);
    }

    public double length() {
        return Math.sqrt(this.x.get() * this.x.get()
                + this.y.get() * this.y.get() + this.z.get() * this.z.get());
    }

    public Motion normalize() {
        final double d0 = this.length();
        return d0 < 1.0E-4D ? new Motion(0.0D, 0.0D, 0.0D) : new Motion(this.x.get() / d0, this.y.get() / d0, this.z.get() / d0);
    }

    public Motion clone() {
        return new Motion(x.get(), y.get(), z.get());
    }

    public MotionValue getX() {
        return x;
    }

    public MotionValue getY() {
        return y;
    }

    public MotionValue getZ() {
        return z;
    }

}
