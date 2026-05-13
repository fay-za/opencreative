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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public final class EntityGetItemAction extends EntityAction {
    public EntityGetItemAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        VariableLink link = getArguments().getVariableLink("variable", this);
        int index = getArguments().getInt("slot", 1, this);
        ItemStack item;
        if (entity instanceof InventoryHolder holder) {
            item = holder.getInventory().getItem(index - 1);
        } else if (entity instanceof LivingEntity living && living.getEquipment() != null) {
            item = living.getEquipment().getItem(EquipmentSlot.values()[index - 1]);
        } else {
            throw new UnsupportedEntityException(InventoryHolder.class, entity);
        }
        if (item != null) setVarValue(link, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_GET_ITEM_BY_SLOT;
    }
}
