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


package ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.other;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;

public final class RepeatForLoopAction extends RepeatAction {

    public RepeatForLoopAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean checkCanContinue() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        if (link == null) {
            return false;
        }
        double add = getArguments().getDouble("add", 1.0d, this);
        String type = getArguments().getText("type", "less", this);
        double untilValue = getArguments().getDouble("range", 10.0d, this);
        double currentValue = getArguments().getDouble("variable", 0.0d, this);
        boolean execute = switch (type.toLowerCase()) {
            case "less" -> currentValue < untilValue;
            case "less-equals" -> currentValue <= untilValue;
            case "greater" -> currentValue > untilValue;
            case "greater-equals" -> currentValue >= untilValue;
            default -> false;
        };
        if (execute) {
            this.setVarValue(link, currentValue + add);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.REPEAT_FOR_NUMBERS;
    }
}

