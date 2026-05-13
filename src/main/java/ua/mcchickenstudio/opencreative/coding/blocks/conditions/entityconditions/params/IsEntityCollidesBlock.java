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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.params;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.EntityCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class IsEntityCollidesBlock extends EntityCondition {
    public IsEntityCollidesBlock(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkEntity(@NotNull Entity entity) {
        List<Location> locationPoints = getArguments().getLocationList("locations", this);
        boolean requireAll = getArguments().getBoolean("all", true, this);
        boolean ignoreSize = getArguments().getBoolean("ignore-size", false, this);

        boolean collides = false;
        for (Location location : locationPoints) {
            BoundingBox blockBox = ignoreSize ? new BoundingBox(
                    location.getBlockX() - 0.5, location.getBlockY() - 0.5, location.getBlockZ() - 0.5,
                    location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5
            ) : location.getBlock().getBoundingBox();
            if (entity.getBoundingBox().overlaps(blockBox)) {
                if (!requireAll) {
                    return true;
                }
                collides = true;
            } else {
                if (requireAll) {
                    return false;
                }
            }
        }
        return collides;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_ENTITY_OVERLAPS_BLOCK;
    }

}
