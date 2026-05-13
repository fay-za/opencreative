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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;

import java.util.Map;

public abstract class CodingBlock implements ExtensionContent {

    private final @NotNull String id;
    private final @NotNull Material mainBlock;
    private final @NotNull Material offBlock;

    public CodingBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock) {
        if (!mainBlock.isBlock()) throw new IllegalArgumentException("Main block material needs to be a block.");
        if (!offBlock.isBlock()) throw new IllegalArgumentException("Off block material needs to be a block.");
        this.id = id;
        this.mainBlock = mainBlock;
        this.offBlock = offBlock;
    }

    public abstract @Nullable WrappedCodingBlock<?> createWrapped(@NotNull Map<String, Object> data);

    public abstract void onSignClick(PlayerInteractEvent event);

    public final void placeBlocks(@NotNull Location mainLocation, @NotNull Location offLocation, @NotNull Location containerLocation, @NotNull Location signLocation, @NotNull Material containerMaterial, @NotNull Material signMaterial) {
        mainLocation.getBlock().setType(mainBlock);
        offLocation.getBlock().setType(offBlock);
    }

    public @NotNull Material getMainBlock() {
        return mainBlock;
    }

    public @NotNull Material getOffBlock() {
        return offBlock;
    }

    public @NotNull String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CodingBlock block)) return false;
        return block.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
