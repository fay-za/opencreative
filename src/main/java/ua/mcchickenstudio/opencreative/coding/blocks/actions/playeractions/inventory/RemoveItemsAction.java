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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;

public final class RemoveItemsAction extends PlayerAction {
    public RemoveItemsAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    public static void removeItems(@NotNull Player player, ItemStack item) {
        ItemStack[] contents = player.getInventory().getContents();
        int amount = item.getAmount();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;
            if (!itemEquals(stack, item)) continue;

            int take = Math.min(amount, stack.getAmount());
            stack.setAmount(stack.getAmount() - take);
            amount -= take;

            if (stack.getAmount() <= 0) {
                contents[i] = null;
            }

            if (amount <= 0) break;
        }

        player.getInventory().setContents(contents);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        for (ItemStack item : getArguments().getItemList("items", this)) {
            removeItems(player, item);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_REMOVE_ITEMS;
    }
}
