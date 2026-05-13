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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.Map;

public final class AddItemEnchantmentAction extends VariableAction {
    public AddItemEnchantmentAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        ItemStack item = getArguments().getItem("item", getArguments().getItem("variable", new ItemStack(Material.APPLE), this), this);
        ItemStack enchantmentItem = getArguments().getItem("enchantment", new ItemStack(Material.ENCHANTED_BOOK), this);
        int level = getArguments().getInt("level", 1, this);
        if (enchantmentItem.getItemMeta() instanceof EnchantmentStorageMeta enchantmentMeta) {
            Map<Enchantment, Integer> enchantments = enchantmentMeta.getStoredEnchants();
            for (Enchantment enchantment : enchantments.keySet()) {
                if (!enchantment.canEnchantItem(item)) {
                    continue;
                }
                if (level > 5) {
                    level = 5;
                } else if (level < 1) {
                    level = 1;
                }
                item.addEnchantment(enchantment, level);
            }
        }
        setVarValue(link, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_ADD_ITEM_ENCHANTMENT;
    }
}
