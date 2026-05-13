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

package ua.mcchickenstudio.opencreative.indev.blocks;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>TypedBlock</h1>
 * This interface represents a coding block, that
 * has specified type. Coding block execution will
 * depend on type.
 */
public interface TypedBlock {

    /**
     * Returns a specific type of block.
     * @return type of coding block.
     */
    @NotNull String getType();

}
