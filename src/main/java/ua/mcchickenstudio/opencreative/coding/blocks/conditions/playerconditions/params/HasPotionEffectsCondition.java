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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.params;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.ArrayList;
import java.util.List;

public final class HasPotionEffectsCondition extends PlayerCondition {
    public HasPotionEffectsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(@NotNull Player player) {
        List<ItemStack> potionsItems = getArguments().getItemList("potions", this);
        boolean requireAll = getArguments().getBoolean("all", false, this);
        boolean hasEffect = false;
        for (ItemStack potionItem : potionsItems) {
            PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
            List<PotionEffect> effects = new ArrayList<>();
            if (potionMeta.getBasePotionType() != null) {
                effects.addAll(potionMeta.getBasePotionType().getPotionEffects());
            }
            if (potionMeta.hasCustomEffects()) {
                effects.addAll(potionMeta.getCustomEffects());
            }
            for (PotionEffect potionEffect : effects) {
                if (player.hasPotionEffect(potionEffect.getType())) {
                    if (!requireAll) {
                        return true;
                    }
                    hasEffect = true;
                } else {
                    if (requireAll) {
                        return false;
                    }
                }
            }
        }
        return hasEffect;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_PLAYER_HAS_POTION_EFFECTS;
    }
}
