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

package ua.mcchickenstudio.opencreative.coding.blocks;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

/**
 * <h1>DisplayableIcon</h1>
 * This interface is used for coding blocks, that have
 * icon to display in menu.
 */
public interface DisplayableIcon {

    /**
     * Returns icon, that will be displayed
     * in coding block type selection menu.
     *
     * @return icon of coding block.
     */
    @NotNull ItemStack getDisplayIcon();

    /**
     * Returns menus category, that will be
     * used to filter coding block types.
     */
    @NotNull MenusCategory getCategory();

}
