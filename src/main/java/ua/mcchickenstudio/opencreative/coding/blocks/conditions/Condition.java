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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugAction;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public abstract class Condition extends Action {

    private final List<Action> actions = new ArrayList<>();
    private final List<Action> reactions = new ArrayList<>();
    private final boolean isOpposed;

    /**
     * Creates a Condition with linked executor and specified arguments.
     *
     * @param executor Executor where this action will be added.
     * @param x        X from Action's block location in developers planet.
     * @param args     List of arguments for action.
     */
    public Condition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args);
        this.isOpposed = isOpposed;
        this.actions.addAll(actions);
        this.reactions.addAll(reactions);
    }

    public abstract boolean check(Entity entity);

    @Override
    public final void prepareAndExecute(@NotNull ActionsHandler handler) {
        if (getActionType().isDisabled()) {
            sendCodingDebugLog(getPlanet(), "Action is disabled, cannot work: " + getActionType().getLocaleName());
            return;
        }
        this.handler = handler;
        this.event = handler.getEvent();
        sendCodingDebugAction(this);
        boolean check = false;
        if (getTargets().isEmpty()) return;
        for (Entity entity : getTargets()) {
            if (entity == null) continue;
            if (entity.getWorld().equals(getPlanet().getWorld()) || !OpenCreative.getSettings().getCodingSettings().isIgnoreActionsIfEntityNotInWorld()) {
                this.entity = entity;
                if (check(entity)) {
                    check = true;
                }
            }
        }
        if (getExecutor().isDebug()) {
            sendCodingDebugLog(getPlanet(), getLocaleMessage("coding-debug.condition.returned-" + check, false).replace("%type%", getActionType().getLocaleName()));
        }
        if (check ^ isOpposed) {
            new ActionsHandler(this).executeActions(actions);
        } else {
            new ActionsHandler(this).executeActions(reactions);
        }
    }

    @Override
    protected void execute(Entity entity) {
    }

    public abstract @NotNull ActionCategory getActionCategory();

    public List<Action> getActions() {
        return actions;
    }

    public List<Action> getElseActions() {
        return reactions;
    }

    public boolean isOpposed() {
        return isOpposed;
    }

    protected @Nullable Object getEventValue(@NotNull Class<? extends EventValue> clazz) {
        return EventValues.getInstance().getValue(clazz, handler, this, this.getEntity());
    }

}
