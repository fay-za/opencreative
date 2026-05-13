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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.movement;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class EntityTeleportAction extends EntityAction {
    public EntityTeleportAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        Location location = getArguments().getLocation("location", getPlanet().getTerritory().getSpawnLocation(), this);
        String consider = getArguments().getText("consider", "all", this);
        if (consider.equals("only-coordinates")) {
            location.setYaw(entity.getYaw());
            location.setPitch(entity.getPitch());
        } else if (consider.equals("only-rotation")) {
            location.setX(entity.getX());
            location.setY(entity.getY());
            location.setZ(entity.getZ());
        }
        entity.teleport(location);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_TELEPORT;
    }
}
