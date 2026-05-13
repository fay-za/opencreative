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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;

import java.util.List;

public final class SetMenuItemsRowAction extends PlayerAction {
    public SetMenuItemsRowAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof CustomMenu)) {
            /*
             * This check prevents from modifying server
             * menus and OpenCreative+ menus too.
             */
            return;
        }
        List<ItemStack> items = getArguments().getItemList("items", this);
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getType() != InventoryType.CHEST && inventory.getType() != InventoryType.ENDER_CHEST) return;
        int row = getArguments().getInt("row", 1, this);
        if (row > 6) row = 6;
        else if (row < 1) row = 1;
        if (inventory.getSize() < row * 9) {
            inventory = new CustomMenu(row * 9, player.getOpenInventory().getTitle()).getInventory();
            if (getPlanet().getLimits().cantOpenMenu(player)) {
                /*
                 * This check prevents player from opening
                 * too many menus, that can prevent from
                 * quiting the game.
                 */
                throw new TooManyOpenedMenusException(player.getName());
            }
            player.openInventory(inventory);
        }
        boolean replaceWithAir = getArguments().getBoolean("replace-with-air", true, this);
        for (int slot = (row * 9) - 9; slot < row * 9; slot++) {
            int i = slot % 9;
            if (i >= items.size()) break;
            ItemStack item = items.get(slot % 9);
            if (replaceWithAir || !item.isEmpty()) {
                player.getOpenInventory().getTopInventory().setItem(slot, item);
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_INVENTORY_VIEW_ROW_ITEMS;
    }
}
