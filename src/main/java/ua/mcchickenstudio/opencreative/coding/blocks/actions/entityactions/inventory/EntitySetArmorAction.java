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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.inventory;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetArmorAction extends EntityAction {
    public EntitySetArmorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            throw new UnsupportedEntityException(LivingEntity.class, entity);
        }
        if (living.getEquipment() == null) return;
        ItemStack helmet = getArguments().getItem("helmet", ItemStack.empty(), this);
        ItemStack chestplate = getArguments().getItem("chestplate", ItemStack.empty(), this);
        ItemStack leggings = getArguments().getItem("leggings", ItemStack.empty(), this);
        ItemStack boots = getArguments().getItem("boots", ItemStack.empty(), this);
        boolean replaceWithAir = getArguments().getBoolean("replace-with-air", false, this);
        if (replaceWithAir || !helmet.isEmpty()) {
            living.getEquipment().setHelmet(helmet);
        }
        if (replaceWithAir || !chestplate.isEmpty()) {
            living.getEquipment().setChestplate(chestplate);
        }
        if (replaceWithAir || !leggings.isEmpty()) {
            living.getEquipment().setLeggings(leggings);
        }
        if (replaceWithAir || !boots.isEmpty()) {
            living.getEquipment().setBoots(boots);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_ARMOR;
    }
}
