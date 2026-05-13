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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.number;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public final class RoundNumberAction extends VariableAction {
    public RoundNumberAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        double number = getArguments().getDouble("number", 0.7, this);
        int digits = getArguments().getInt("digits", 0, this);
        String type = getArguments().getText("type", "ceil", this);

        double result = (type.equalsIgnoreCase("floor")) ? Math.floor(number) : Math.ceil(number);
        if (digits <= 0) {
            setVarValue(variable, Math.round(result));
            return;
        }

        double scale = Math.pow(10, digits);
        result = type.equalsIgnoreCase("floor")
                ? Math.floor(number * scale) / scale
                : Math.ceil(number * scale) / scale;
        setVarValue(variable, result);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_ROUND_NUMBER;
    }
}
