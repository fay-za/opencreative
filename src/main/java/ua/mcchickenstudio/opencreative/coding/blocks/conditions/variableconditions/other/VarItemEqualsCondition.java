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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.other;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.List;

public final class VarItemEqualsCondition extends VariableCondition {

    public VarItemEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        boolean ignoreAmount = getArguments().getBoolean("ignore-amount", true, this);
        boolean ignoreName = getArguments().getBoolean("ignore-name", false, this);
        boolean ignoreLore = getArguments().getBoolean("ignore-lore", false, this);
        boolean ignoreEnchantments = getArguments().getBoolean("ignore-enchantments", false, this);
        boolean ignoreFlags = getArguments().getBoolean("ignore-flags", false, this);
        boolean ignoreMaterial = getArguments().getBoolean("ignore-material", false, this);
        boolean ignoreDamage = getArguments().getBoolean("ignore-damage", false, this);

        boolean check = false;
        List<ItemStack> items = getArguments().getItemList("items", this);
        if (items.isEmpty()) return false;

        ItemStack comparedItem = getArguments().getItem("item", new ItemStack(Material.AIR, 1), this);
        comparedItem = ItemUtils.getItemWithIgnoreData(comparedItem, ignoreAmount, ignoreName, ignoreLore, ignoreFlags, ignoreEnchantments, ignoreMaterial, ignoreDamage);
        for (ItemStack checkItem : items) {
            checkItem = ItemUtils.getItemWithIgnoreData(checkItem, ignoreAmount, ignoreName, ignoreLore, ignoreFlags, ignoreEnchantments, ignoreMaterial, ignoreDamage);
            if (comparedItem.equals(checkItem)) {
                check = true;
            }
        }
        return check;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_VAR_ITEM_EQUALS;
    }
}
