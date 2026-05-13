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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class RepeatConditionSelectionMenu extends AbstractMenu {

    private final Player player;
    private final Location signLocation;
    private final boolean opposed;

    private final ItemStack varCondition = createItem(Material.OBSIDIAN, 1, "items.developer.variable-condition");
    private final ItemStack playerCondition = createItem(Material.OAK_PLANKS, 1, "items.developer.player-condition");
    private final ItemStack entityCondition = createItem(Material.BRICKS, 1, "items.developer.entity-condition");

    public RepeatConditionSelectionMenu(Player player, Location location, boolean opposed) {
        super(3, getLocaleMessage("blocks.repeat_while" + (opposed ? "_not" : ""),
                false));
        this.player = player;
        this.signLocation = location;
        this.opposed = opposed;
    }

    @Override
    public void fillItems(Player player) {
        setItem(11, playerCondition);
        setItem(13, entityCondition);
        setItem(15, varCondition);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        String path = "repeat_while" + (opposed ? "_not" : "");
        if (itemEquals(currentItem, playerCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.PLAYER_CONDITION, path).open(player);
        } else if (itemEquals(currentItem, entityCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.ENTITY_CONDITION, path).open(player);
        } else if (itemEquals(currentItem, varCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.VARIABLE_CONDITION, path).open(player);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}
