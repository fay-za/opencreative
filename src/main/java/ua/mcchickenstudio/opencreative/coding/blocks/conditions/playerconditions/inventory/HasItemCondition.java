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

public final class HasItemCondition extends PlayerCondition {

    public HasItemCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(@NotNull Player player) {
        List<ItemStack> items = getArguments().getItemList("items", this);
        boolean all = getArguments().getBoolean("all", false, this);
        String compareMode = getArguments().getText("compare-mode", "min-items", this);

        if (all) {
            return items.stream().allMatch(itemStack -> matches(itemStack, player, compareMode));
        } else {
            return items.stream().anyMatch(itemStack -> matches(itemStack, player, compareMode));
        }
    }

    private boolean matches(ItemStack item, Player player, String compareMode) {
        Inventory inv = player.getInventory();

        return switch (compareMode.toLowerCase()) {
            case "min-items" -> inv.containsAtLeast(item, item.getAmount());
            case "full" -> inv.contains(item);
            case "material" -> inv.contains(item.getType(), item.getAmount());
            default -> false;
        };
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_PLAYER_HAS_ITEM;
    }
}
