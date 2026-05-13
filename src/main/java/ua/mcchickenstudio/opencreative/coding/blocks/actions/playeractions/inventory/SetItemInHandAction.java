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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class SetItemInHandAction extends PlayerAction {
    public SetItemInHandAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        ItemStack mainItem = getArguments().getItem("main", new ItemStack(Material.AIR), this);
        ItemStack offItem = getArguments().getItem("off", new ItemStack(Material.AIR), this);
        boolean replaceWithAir = getArguments().getBoolean("replace-with-air", false, this);
        if (replaceWithAir || !mainItem.isEmpty()) player.getInventory().setItemInMainHand(mainItem);
        if (replaceWithAir || !offItem.isEmpty()) player.getInventory().setItemInOffHand(offItem);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_ITEM_IN_HAND;
    }
}
