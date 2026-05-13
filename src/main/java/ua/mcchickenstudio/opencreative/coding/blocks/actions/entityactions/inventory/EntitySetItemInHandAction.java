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

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetItemInHandAction extends EntityAction {
    public EntitySetItemInHandAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        ItemStack mainItem = getArguments().getItem("main", new ItemStack(Material.AIR), this);
        ItemStack offItem = getArguments().getItem("off", new ItemStack(Material.AIR), this);
        boolean replaceWithAir = getArguments().getBoolean("replace-with-air", false, this);
        if (entity instanceof HumanEntity human) {
            if (replaceWithAir || !mainItem.isEmpty()) human.getInventory().setItemInMainHand(mainItem);
            if (replaceWithAir || !offItem.isEmpty()) human.getInventory().setItemInOffHand(offItem);
        } else if (entity instanceof LivingEntity living && living.getEquipment() != null) {
            if (replaceWithAir || !mainItem.isEmpty()) living.getEquipment().setItemInMainHand(mainItem);
            if (replaceWithAir || !offItem.isEmpty()) living.getEquipment().setItemInOffHand(offItem);
        } else {
            throw new UnsupportedEntityException(LivingEntity.class, entity);
        }


    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_ITEM_IN_HAND;
    }
}
