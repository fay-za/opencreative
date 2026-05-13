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
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockLeavesDecayedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.WorldExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class WorldBlockLeavesDecayedExecutor extends WorldExecutor {

    public WorldBlockLeavesDecayedExecutor() {
        super("block_leaves_decayed");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.OAK_LEAVES);
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.WORLD_BLOCKS;
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return BlockLeavesDecayedEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Block Leaves Decay Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When leaves block decays and disappears";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }
}
