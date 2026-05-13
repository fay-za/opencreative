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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing {

    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X);

    /**
     * All facings in D-U-N-S-W-E order
     */
    public static final EnumFacing[] VALUES = new EnumFacing[6];
    /**
     * All Facings with horizontal axis in order S-W-N-E
     */
    private static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
    private static final Map NAME_LOOKUP = Maps.newHashMap();

    static {
        EnumFacing[] var0 = values();

        for (EnumFacing var3 : var0) {
            VALUES[var3.index] = var3;

            if (var3.getAxis().isHorizontal()) {
                HORIZONTALS[var3.horizontalIndex] = var3;
            }

            NAME_LOOKUP.put(var3.getName2().toLowerCase(), var3);
        }
    }

    /**
     * Ordering index for D-U-N-S-W-E
     */
    private final int index;
    /**
     * Index of the opposite Facing in the VALUES array
     */
    private final int opposite;
    /**
     * Oredering index for the HORIZONTALS field (S-W-N-E)
     */
    private final int horizontalIndex;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;

    EnumFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, AxisDirection axisDirectionIn, Axis axisIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
    }

    /**
     * Choose a random Facing using the given Random
     */
    public static EnumFacing random(Random rand) {
        return values()[rand.nextInt(values().length)];
    }

    /**
     * Get the opposite Facing (e.g. DOWN => UP)
     */
    public EnumFacing getOpposite() {
        return VALUES[this.opposite];
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFrontOffsetX() {
        return this.axis == Axis.X ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetY() {
        return this.axis == Axis.Y ? this.axisDirection.getOffset() : 0;
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFrontOffsetZ() {
        return this.axis == Axis.Z ? this.axisDirection.getOffset() : 0;
    }

    /**
     * Same as getName, but does not override the method from Enum.
     */
    public String getName2() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public Axis getAxis() {
        return axis;
    }

    public enum Axis implements Predicate {

        X("x", Plane.HORIZONTAL),
        Y("y", Plane.VERTICAL),
        Z("z", Plane.HORIZONTAL);
        private static final Map NAME_LOOKUP = Maps.newHashMap();

        static {
            Axis[] var0 = values();

            for (Axis var3 : var0) {
                NAME_LOOKUP.put(var3.getName2().toLowerCase(), var3);
            }
        }

        private final String name;
        private final Plane plane;

        Axis(String name, Plane plane) {
            this.name = name;
            this.plane = plane;
        }

        public String getName2() {
            return this.name;
        }

        public boolean isVertical() {
            return this.plane == Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return this.plane == Plane.HORIZONTAL;
        }

        public String toString() {
            return this.name;
        }

        public boolean apply(EnumFacing facing) {
            return facing != null && facing.getAxis() == this;
        }

        public boolean apply(Object p_apply_1_) {
            return this.apply((EnumFacing) p_apply_1_);
        }

        public Plane getPlane() {
            return plane;
        }
    }

    public enum AxisDirection {

        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return offset;
        }

        public String toString() {
            return this.description;
        }
    }

    public enum Plane implements Predicate, Iterable {
        HORIZONTAL(),
        VERTICAL();


        Plane() {
        }

        public EnumFacing[] facings() {
            return switch (SwitchPlane.PLANE_LOOKUP[this.ordinal()]) {
                case 1 -> new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
                case 2 -> new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
                default -> throw new Error("Someone's been tampering with the universe!");
            };
        }

        public EnumFacing random(Random rand) {
            EnumFacing[] var2 = this.facings();
            return var2[rand.nextInt(var2.length)];
        }

        public boolean apply(EnumFacing facing) {
            return facing != null && facing.getAxis().getPlane() == this;
        }

        public Iterator iterator() {
            return Iterators.forArray(this.facings());
        }

        public boolean apply(Object p_apply_1_) {
            return this.apply((EnumFacing) p_apply_1_);
        }
    }

    static final class SwitchPlane {
        static final int[] AXIS_LOOKUP;
        static final int[] FACING_LOOKUP;
        static final int[] PLANE_LOOKUP = new int[Plane.values().length];

        static {
            try {
                PLANE_LOOKUP[Plane.HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                PLANE_LOOKUP[Plane.VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            FACING_LOOKUP = new int[EnumFacing.values().length];

            try {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
            } catch (NoSuchFieldError ignored) {
            }

            AXIS_LOOKUP = new int[Axis.values().length];

            try {
                AXIS_LOOKUP[Axis.X.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                AXIS_LOOKUP[Axis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                AXIS_LOOKUP[Axis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }
}
