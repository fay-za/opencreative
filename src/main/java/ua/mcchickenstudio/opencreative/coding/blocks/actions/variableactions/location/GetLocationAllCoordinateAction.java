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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public class GetLocationAllCoordinateAction extends VariableAction {
    public GetLocationAllCoordinateAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        Location location = getArguments().getLocation("location", getDefaultLocation(), this);

        VariableLink x = getArguments().getVariableLink("x", this);
        VariableLink y = getArguments().getVariableLink("y", this);
        VariableLink z = getArguments().getVariableLink("z", this);
        VariableLink pitch = getArguments().getVariableLink("pitch", this);
        VariableLink yaw = getArguments().getVariableLink("yaw", this);

        if (getArguments().pathExists("location")) {
            if (x != null) setVarValue(x, location.getX());
            if (y != null) setVarValue(y, location.getY());
            if (z != null) setVarValue(z, location.getZ());
            if (pitch != null) setVarValue(pitch, location.getPitch());
            if (yaw != null) setVarValue(yaw, location.getYaw());
        } else {
            if (getArguments().pathExists("x") || getArguments().pathExists("y") || getArguments().pathExists("z") || getArguments().pathExists("pitch") || getArguments().pathExists("yaw")) {
                throw new IllegalArgumentException("Not found location");
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_GET_LOCATION_ALL;
    }
}
