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

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * <h1>WrappedCodingBlock</h1>
 * This class represents a coding metadata provider
 * for coding block, for example: coordinates in coding world.
 *
 * @param <T> coding block type to store data.
 */
public abstract class WrappedCodingBlock<T extends CodingBlock> implements ConfigurationSerializable {

    protected final T codingBlock;

    private final int x;
    private final int y;
    private final int z;

    protected WrappedCodingBlock(T codingBlock, int x, int y, int z) {
        this.codingBlock = codingBlock;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "category", codingBlock.getId(),
                "location.x", x,
                "location.y", y,
                "location.z", z
        );
    }

    public T getBlock() {
        return codingBlock;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
