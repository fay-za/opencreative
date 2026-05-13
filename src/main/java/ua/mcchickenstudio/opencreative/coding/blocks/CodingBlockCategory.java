/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.coding.blocks;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

/**
 * <h1>CodingBlockCategory</h1>
 * This interface represents category enum of coding block.
 */
public interface CodingBlockCategory {

    /**
     * Returns default menus category, that will be shown
     * when opening {@link ua.mcchickenstudio.opencreative.coding.menus.blocks.BlocksCategorySelectionMenu Coding Block Type Selection menu}
     * with legacy mode, that opens preferred category immediately.
     *
     * @return default menus category.
     */
    @NotNull MenusCategory getDefaultCategory();

    /**
     * Returns first block material, that will be used
     * for placing a coding block.
     * <p>
     * Examples:
     * <ul>
     *     <li>Player Event is Diamond block.</li>
     *     <li>Player Action is Cobblestone.</li>
     *     <li>Player Condition is Oak Planks.</li>
     * </ul>
     * @return first block material.
     */
    @NotNull Material getBlock();

    /**
     * Returns material of colored stained-glass pane,
     * that will be used for decoration in blocks type
     * selection menu.
     *
     * @return material of colored stained-glass pane.
     */
    @NotNull Material getStainedPane();

    /**
     * Returns color, that will be used for displaying
     * executor in chat.
     *
     * @return color of executor.
     */
    @NotNull NamedTextColor getColor();

    /**
     * Returns localized name of category.
     *
     * @return localized name of category.
     */
    @NotNull String getLocaleName();

    /**
     * Returns second block material, that will be used
     * for placing a coding block after first.
     * <p>
     * Examples:
     * <ul>
     *     <li>Player Event is Diamond ore.</li>
     *     <li>Player Action is Stone.</li>
     *     <li>Player Condition is Piston.</li>
     * </ul>
     * @return second block material.
     */
    @NotNull Material getAdditionalBlock();

    /**
     * Returns item of coding block, that will be
     * used for displaying in blocks type selection
     * menu.
     *
     * @return item of coding block.
     */
    @NotNull ItemStack getItem();
    
}
