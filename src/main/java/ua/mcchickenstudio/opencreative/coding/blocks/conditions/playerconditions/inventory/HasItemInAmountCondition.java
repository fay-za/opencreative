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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class HasItemInAmountCondition extends PlayerCondition {

    public HasItemInAmountCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(@NotNull Player player) {
        ItemStack item = getArguments().getItem("item", new ItemStack(Material.APPLE), this);
        int amount = getArguments().getInt("amount", item.getAmount(), this);
        String compareMode = getArguments().getText("compare-mode", "full", this);

        Inventory inv = player.getInventory();

        return switch (compareMode.toLowerCase()) {
            case "full" -> inv.containsAtLeast(item, amount);
            case "material" -> inv.contains(item.getType(), amount);
            default -> false;
        };
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_PLAYER_HAS_ITEM_IN_AMOUNT;
    }
}
