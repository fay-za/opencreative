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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.BlockInteractionEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class PlayerBlockInteractExecutor extends PlayerExecutor {

    public PlayerBlockInteractExecutor() {
        super("block_interact");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.CHEST);
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return BlockInteractionEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Player Block Interact Block Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When player interacts with a block";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.INTERACTION;
    }
}
