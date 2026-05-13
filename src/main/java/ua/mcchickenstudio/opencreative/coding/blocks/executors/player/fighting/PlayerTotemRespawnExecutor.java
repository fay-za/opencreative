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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player.fighting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerTotemRespawnEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class PlayerTotemRespawnExecutor extends PlayerExecutor {

    public PlayerTotemRespawnExecutor() {
        super("totem_respawn");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.TOTEM_OF_UNDYING);
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return PlayerTotemRespawnEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Player Totem Respawn Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When player resurrects with totem of undying";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.FIGHTING;
    }
}
