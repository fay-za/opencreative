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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.world.blocks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockFadedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.WorldExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class WorldBlockFadedExecutor extends WorldExecutor {

    public WorldBlockFadedExecutor() {
        super("block_faded");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.ICE);
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.WORLD_BLOCKS;
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return BlockFadedEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Block Fade Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When block fades out and disappears";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }
}
