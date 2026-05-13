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

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.ArrayList;
import java.util.List;

public final class RepeatWhileConditionAction extends RepeatAction {

    private final ActionType conditionType;

    public RepeatWhileConditionAction(Executor executor, Target target, int x, Arguments args, List<Action> actions,
                                      ActionType conditionType) {
        super(executor, target, x, args, actions);
        this.conditionType = conditionType;
    }

    @Override
    public boolean checkCanContinue() {
        Action action;
        try {
            action = conditionType.getActionClass().getConstructor(Executor.class,
                            Target.class, int.class, Arguments.class,
                            List.class, List.class, boolean.class)
                    .newInstance(getExecutor(), getTarget(), getX(), getArguments(),
                            new ArrayList<>(), new ArrayList<>(), false);
        } catch (Exception error) {
            throw new RuntimeException("Failed to construct condition for repeat while action: " + conditionType.name());
        }
        if (!(action instanceof Condition condition)) {
            return false;
        }
        condition.setEvent(this.getHandler().getEvent());
        condition.setHandler(this.getHandler());
        for (Entity entity : getTargets()) {
            if (!condition.check(entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.REPEAT_WHILE;
    }
}
