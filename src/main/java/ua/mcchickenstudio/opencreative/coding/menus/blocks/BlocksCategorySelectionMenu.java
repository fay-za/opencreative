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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>BlocksCategorySelectionMenu</h1>
 * This class represents a menu for selecting menus category
 * and opening a coding block type selection menu.
 */
public final class BlocksCategorySelectionMenu extends MenusCategorySelectionMenu implements BlockMenu {

    private final Location location;
    private ExecutorCategory executorCategory = null;
    private ActionCategory actionCategory = null;
    private String firstLine = null;

    public BlocksCategorySelectionMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull ExecutorCategory category) {
        super(player, category.getItem(),
                category.getStainedPane(),
                Executors.getInstance().getCategories(category),
                ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())),
                "events", category);
        this.location = location;
    }

    public BlocksCategorySelectionMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull ActionCategory category) {
        super(player, category.getItem(),
                category.getStainedPane(),
                ActionType.getMenusCategories(category),
                ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())),
                category.isCondition() ? "conditions" : "actions", category);
        this.location = location;
    }

    public BlocksCategorySelectionMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull ActionCategory category,
                                       @NotNull String firstLine) {
        super(player, category.getItem(),
                category.getStainedPane(),
                ActionType.getMenusCategories(category),
                ChatColor.stripColor(getLocaleMessage("blocks." + category.name().toLowerCase())),
                category.isCondition() ? "conditions" : "actions", category);
        this.firstLine = firstLine;
        this.location = location;
    }

    @Override
    public @NotNull ContentWithMenusCategoryMenu<?> getContentBrowserMenu(final Object frequency) {
        BlocksWithMenusCategoryMenu<?> content;
        if (frequency instanceof ActionCategory category) {
            this.actionCategory = category;
        } else if (frequency instanceof ExecutorCategory category) {
            this.executorCategory = category;
        }
        if (mainCategory.equalsIgnoreCase("events")) {
            content = new ExecutorTypeSelectionMenu(player, location, executorCategory);
        } else {
            content = new ActionTypeSelectionMenu(player, location, actionCategory, firstLine);
        }
        content.setSignLocation(location);
        return content;
    }

    @Override
    public @NotNull BlockState getBlockState() {
        return location.getBlock().getState();
    }

    @Override
    public @Nullable Location getLocation() {
        return location;
    }
}
