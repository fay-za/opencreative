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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;
import java.util.Map;

public final class VarItemHasEnchantments extends VariableCondition {

    public VarItemHasEnchantments(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        ItemStack item = getArguments().getItem("item", new ItemStack(Material.APPLE), this);
        ItemStack enchantedBook = getArguments().getItem("enchantment", new ItemStack(Material.ENCHANTED_BOOK), this);
        boolean requireAllEnchants = getArguments().getBoolean("all", true, this);
        String levelCheckMode = getArguments().getText("level-check", "exact", this);

        if (!item.hasItemMeta() || !enchantedBook.hasItemMeta()) {
            return false;
        }

        Map<Enchantment, Integer> requiredEnchantments;

        if (enchantedBook.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            requiredEnchantments = bookMeta.getStoredEnchants();
        } else {
            return false;
        }

        if (requiredEnchantments.isEmpty()) return false;

        int matches = 0;

        for (Map.Entry<Enchantment, Integer> entry : requiredEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int requiredLevel = entry.getValue();

            if (!item.containsEnchantment(enchantment)) continue;

            boolean levelMatches = switch (levelCheckMode) {
                case "exact" -> item.getEnchantmentLevel(enchantment) == requiredLevel;
                case "min-level" -> item.getEnchantmentLevel(enchantment) >= requiredLevel;
                case "max-level" -> item.getEnchantmentLevel(enchantment) <= requiredLevel;
                case "ignore" -> true;
                default -> false;
            };

            if (levelMatches) {
                matches++;
            }
        }

        if (requireAllEnchants) {
            return matches == requiredEnchantments.size();
        } else {
            return matches > 0;
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_VAR_ITEM_HAS_ENCHANTMENTS;
    }
}
