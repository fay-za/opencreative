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
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;

public final class OpenInventoryAction extends PlayerAction {
    public OpenInventoryAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        if (getPlanet().getLimits().cantOpenMenu(player)) {
            /*
             * This check prevents player from opening
             * too many menus, that can prevent from
             * quiting the game.
             */
            throw new TooManyOpenedMenusException(player.getName());
        }
        String inventoryTypeString = getArguments().getText("type", "chest", this);
        InventoryType inventoryType = InventoryType.CHEST;
        try {
            inventoryType = InventoryType.valueOf(inventoryTypeString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        String title = getArguments().getText("title", inventoryType.getDefaultTitle(), this);
        if (!inventoryType.isCreatable()) inventoryType = InventoryType.CHEST;
        player.openInventory(new CustomMenu(inventoryType, title).getInventory());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_OPEN_INVENTORY_VIEW;
    }
}
