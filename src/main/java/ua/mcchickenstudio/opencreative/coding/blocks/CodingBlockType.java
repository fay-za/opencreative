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

/**
 * <h1>CodingBlockType</h1>
 * This interface represents coding block type enum, that
 * has category, icon, locale name and disabled state.
 */
public interface CodingBlockType {

    /**
     * Returns category of coding block.
     *
     * @return coding block category.
     */
    @NotNull CodingBlockCategory getCategory();

    /**
     * Returns icon, that will be used for displaying
     * in Coding Block Type Selection menus.
     *
     * @return icon of coding block.
     */
    @NotNull ItemStack getIcon();

    /**
     * Returns localized name of coding block.
     *
     * @return localized name of coding block.
     */
    @NotNull String getLocaleName();

    /**
     * Checks whether coding block is disabled
     * and cannot be executed.
     *
     * @return true - disabled, false - enabled.
     */
    boolean isDisabled();

}
