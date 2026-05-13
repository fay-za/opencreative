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

public final class Vec2 {

    private final double x;
    private final double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Vec2 vector2) {
        return Math.sqrt(Math.pow(this.x - vector2.x, 2) + Math.pow(this.y - vector2.y, 2));
    }

    public Vec2 add(Vec2 vector2) {
        return new Vec2(this.x + vector2.x, this.y + vector2.y);
    }

    public Vec2 subtract(Vec2 vector2) {
        return new Vec2(this.x - vector2.x, this.y - vector2.y);
    }

    public Vec2 scale(double factor) {
        return new Vec2(this.x * factor, this.y * factor);
    }

    public boolean compare(Vec2 vector2) {
        return this.x == vector2.x && this.y == vector2.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
