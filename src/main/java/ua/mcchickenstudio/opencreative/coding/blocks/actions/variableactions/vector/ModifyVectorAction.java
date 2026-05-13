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

public final class ModifyVectorAction extends VariableAction {
    public ModifyVectorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        Vector vector = getArguments().getVector("vector", new Vector(), this);
        vector = vector.clone();
        boolean add = getArguments().getBoolean("add", false, this);
        double x = (add ? 0.0d : vector.getX());
        double y = (add ? 0.0d : vector.getY());
        double z = (add ? 0.0d : vector.getZ());
        if (getArguments().pathExists("x")) {
            x = getArguments().getDouble("x", x, this);
        }
        if (getArguments().pathExists("y")) {
            y = getArguments().getDouble("y", y, this);
        }
        if (getArguments().pathExists("z")) {
            z = getArguments().getDouble("z", z, this);
        }
        if (add) {
            vector.setX(vector.getX() + x);
            vector.setY(vector.getY() + y);
            vector.setZ(vector.getZ() + z);
        } else {
            vector.setX(x);
            vector.setY(y);
            vector.setZ(z);
        }
        setVarValue(link, vector);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_MODIFY_VECTOR;
    }
}
