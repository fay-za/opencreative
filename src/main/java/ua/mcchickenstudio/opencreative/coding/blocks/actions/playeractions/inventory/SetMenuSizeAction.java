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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;

public final class SetMenuSizeAction extends PlayerAction {
    public SetMenuSizeAction(Executor executor, Target target, int x, Arguments args) {
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
        InventoryView oldInventory = player.getOpenInventory();
        int rows = getArguments().getInt("rows", 6, this);
        if (rows > 6) rows = 6;
        else if (rows < 1) rows = 1;
        Inventory newInventory = new CustomMenu(rows * 9, oldInventory.getTitle()).getInventory();
        for (int i = 0; i < newInventory.getSize(); i++) {
            if (i >= oldInventory.getTopInventory().getSize()) break;
            newInventory.setItem(i, oldInventory.getTopInventory().getItem(i));
        }
        if (getPlanet().getLimits().cantOpenMenu(player)) {
            /*
             * This check prevents player from opening
             * too many menus, that can prevent from
             * quiting the game.
             */
            throw new TooManyOpenedMenusException(player.getName());
        }
        player.openInventory(newInventory);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_INVENTORY_VIEW_ROWS;
    }
}
