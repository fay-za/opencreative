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

public final class Vec2f {

    private final float x;
    private final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f(Number x, Number y) {
        this.x = x.floatValue();
        this.y = y.floatValue();
    }

    public double distance(Vec2f vector2) {
        return Math.sqrt(Math.pow(this.x - vector2.x, 2) + Math.pow(this.y - vector2.y, 2));
    }

    public Vec2f add(Vec2f vector2) {
        return new Vec2f(this.x + vector2.x, this.y + vector2.y);
    }

    public Vec2f subtract(Vec2f vector2) {
        return new Vec2f(this.x - vector2.x, this.y - vector2.y);
    }

    public Vec2f scale(double factor) {
        return new Vec2f(this.x * factor, this.y * factor);
    }

    public boolean compare(Vec2f vector2) {
        return this.x == vector2.x && this.y == vector2.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
