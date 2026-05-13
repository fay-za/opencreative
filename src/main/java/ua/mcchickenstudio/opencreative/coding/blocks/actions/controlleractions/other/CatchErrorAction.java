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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.other;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.ControllerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCodeCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class CatchErrorAction extends ControllerAction {

    public CatchErrorAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    protected void execute(Entity entity) {
        this.entity = entity;
        VariableLink link = getArguments().getVariableLink("variable", this);
        ActionsHandler errorHandler = new ActionsHandler(this);
        try {
            errorHandler.executeActions(getActions());
        } catch (Exception error) {
            setVarValue(link, error.getClass().getSimpleName().toLowerCase());
            if (getPlanet().getLimits().isTooManyCodingErrors()) {
                getPlanet().getTerritory().getScript().getExecutors().stopCode("errors limit");
                sendPlanetCodeCriticalErrorMessage(getPlanet(), getExecutor(), getLocaleMessage("coding-error.errors-limit", false)
                        .replace("%limit%", String.valueOf(getPlanet().getLimits().getCodingErrorsLimit())));
            }
            errorHandler.removeAllActions();
            errorHandler.executeNextAction();
            getHandler().executeNextAction();
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.CONTROLLER_CATCH_ERROR;
    }
}
