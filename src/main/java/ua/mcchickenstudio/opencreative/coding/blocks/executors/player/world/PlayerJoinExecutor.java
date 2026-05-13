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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.world;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.JoinEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class PlayerJoinExecutor extends PlayerExecutor {

    public PlayerJoinExecutor() {
        super("join");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.POTATO);
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.WORLD;
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return JoinEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Player Join Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When player joins the world";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }
}
