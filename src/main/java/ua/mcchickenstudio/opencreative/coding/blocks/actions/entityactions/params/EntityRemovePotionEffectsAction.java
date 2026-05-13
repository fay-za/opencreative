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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.params;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

import java.util.ArrayList;
import java.util.List;

public final class EntityRemovePotionEffectsAction extends EntityAction {
    public EntityRemovePotionEffectsAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            throw new UnsupportedEntityException(LivingEntity.class, entity);
        }
        List<ItemStack> potionsItems = getArguments().getItemList("potions", this);
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
                livingEntity.removePotionEffect(potionEffect.getType());
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_REMOVE_POTION_EFFECTS;
    }
}
