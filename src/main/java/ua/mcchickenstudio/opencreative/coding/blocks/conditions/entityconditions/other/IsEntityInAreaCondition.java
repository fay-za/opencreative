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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.other;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.EntityCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class IsEntityInAreaCondition extends EntityCondition {
    public IsEntityInAreaCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkEntity(@NotNull Entity entity) {
        Location first = getArguments().getLocation("first", getPlanet().getTerritory().getSpawnLocation(), this);
        Location location = entity.getLocation();
        Location second = getArguments().getLocation("second", getPlanet().getTerritory().getSpawnLocation(), this);
        double maxX = Math.max(first.getX(), second.getX());
        double maxY = Math.max(first.getY(), second.getY());
        double maxZ = Math.max(first.getZ(), second.getZ());

        double minX = Math.min(first.getX(), second.getX());
        double minY = Math.min(first.getY(), second.getY());
        double minZ = Math.min(first.getZ(), second.getZ());
        return (location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY && location.getZ() >= minZ && location.getZ() <= maxZ);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_ENTITY_IN_AREA;
    }
}
