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
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.RightClickEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class PlayerRightClickExecutor extends PlayerExecutor {

    public PlayerRightClickExecutor() {
        super("right_click");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.DIAMOND_PICKAXE);
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.INTERACTION;
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return RightClickEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Player Right Click Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When player clicks with right mouse button";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }
}
