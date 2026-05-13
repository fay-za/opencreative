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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.lines;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.ControlAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class BreakRepeatAction extends ControlAction {
    public BreakRepeatAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        ActionsHandler handler = getHandler().getFirstActionsHandler(ActionCategory.REPEAT_ACTION);
        if (handler == null) {
            return;
        }
        if (handler.getAction() instanceof RepeatAction repeatAction) {
            repeatAction.setMustStop(true);
            handler.removeAllActions();
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.CONTROL_BREAK_REPEAT;
    }
}
