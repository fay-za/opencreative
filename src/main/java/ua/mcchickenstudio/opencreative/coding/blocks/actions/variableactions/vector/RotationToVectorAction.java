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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public final class RotationToVectorAction extends VariableAction {

    // Made by pawsashatoy :)
    public RotationToVectorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        final float
                yaw = getArguments().getInt("yaw", 0, this),
                pitch = getArguments().getInt("pitch", 0, this);
        final double
                yawRad = Math.toRadians(yaw),
                pitchRad = Math.toRadians(pitch),
                x = -Math.cos(pitchRad) * Math.sin(yawRad),
                y = -Math.sin(pitchRad),
                z = Math.cos(pitchRad) * Math.cos(yawRad);
        final Vector vector = new Vector(x, y, z);
        setVarValue(link, vector);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_ROTATION_TO_VECTOR;
    }
}
