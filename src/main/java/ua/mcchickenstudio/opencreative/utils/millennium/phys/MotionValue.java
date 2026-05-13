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

@SuppressWarnings("unused")
public final class MotionValue {

    private double alpha;

    public MotionValue() {
    }

    public MotionValue(final double alpha) {
        this.alpha = alpha;
    }

    public double get() {
        return alpha;
    }

    public double set(final double beta) {
        return (alpha = beta);
    }

    public double add(final double beta) {
        return (this.alpha += beta);
    }

    public double subtract(final double beta) {
        return (this.alpha -= beta);
    }

    public double multiply(final double beta) {
        return (this.alpha *= beta);
    }

    public double divide(final double beta) {
        return (this.alpha /= beta);
    }
}

