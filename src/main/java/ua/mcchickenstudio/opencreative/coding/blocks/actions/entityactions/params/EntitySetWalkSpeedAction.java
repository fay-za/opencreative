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

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetWalkSpeedAction extends EntityAction {
    public EntitySetWalkSpeedAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            throw new UnsupportedEntityException(LivingEntity.class, entity);
        }
        boolean add = getArguments().getBoolean("add", false, this);
        double speed = getArguments().getFloat("speed", 0.6f, this);
        if (add) {
            speed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() + speed;
        }
        if (speed > 1 || speed < -1) {
            speed = 0.6f;
        }
        livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_WALK_SPEED;
    }
}
