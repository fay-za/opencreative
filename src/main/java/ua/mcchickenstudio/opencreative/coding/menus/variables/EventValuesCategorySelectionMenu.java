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

package ua.mcchickenstudio.opencreative.coding.menus.variables;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.ContentWithMenusCategoryMenu;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.MenusCategorySelectionMenu;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu where player
 * can select category of event values.
 */
public final class EventValuesCategorySelectionMenu extends MenusCategorySelectionMenu {

    public EventValuesCategorySelectionMenu(@NotNull Player player,
                                            @NotNull ItemStack currentItem) {
        super(player, currentItem, Material.ORANGE_STAINED_GLASS_PANE, EventValues.getInstance().getCategories(),
                getLocaleMessage("menus.developer.event-values.title", false),
                "event-values", "");
    }

    @Override
    public @NotNull ContentWithMenusCategoryMenu<?> getContentBrowserMenu(Object frequency) {
        return new EventValueSelectionMenu(player);
    }

}
