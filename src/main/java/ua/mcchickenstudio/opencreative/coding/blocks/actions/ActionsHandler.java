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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.lines.WaitAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.other.MeasureTimeAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.other.LaunchFunctionAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.other.LaunchMethodAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.*;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCodeCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCodeErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>ActionHandler</h1>
 * This class represents actions handler that executes every action in list.
 * Executors and code blocks with brackets use handlers to execute code inside
 * brackets.
 *
 * @author McChicken Studio
 * @version 6.0
 * @see Executor
 * @see ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition
 * @since 5.0
 */
public class ActionsHandler {

    private final Executor executor;
    private final WorldEvent event;
    private final Action action;
    private final Set<Entity> selectedTargets;
    private final ActionsHandler parentActionsHandler;
    private final Queue<Action> actionsQueue = new LinkedList<>();
    private final boolean doNotUseTryFlag;
    private final UUID uuid = UUID.randomUUID();

    private Entity lastSpawnedEntity;
    private boolean stopped = false;
    private long waitDelay = 0;

    private static final int MAX_DEPTH = 50;

    /**
     * Constructor of actions handler with executor.
     * <p>
     * The executor is main handler of actions.
     *
     * @param executor executor that contains actions to execute.
     */
    public ActionsHandler(@NotNull Executor executor) {
        this.executor = executor;
        this.event = executor.getEvent();
        this.selectedTargets = new HashSet<>(event.getSelection());
        this.parentActionsHandler = null;
        this.action = null;
        this.doNotUseTryFlag = false;
        this.lastSpawnedEntity = null;
    }

    /**
     * Constructor of actions handler with multi action (with brackets).
     * <p>
     * Changes (to selection, or stopping code line) will be also
     * applied to parent actions handler.
     *
     * @param action multi action that contains actions to execute.
     */
    public ActionsHandler(@NotNull Action action) {
        this.parentActionsHandler = action.getHandler();
        ActionsHandler mainHandler = getMainActionHandler();
        this.executor = mainHandler.executor;
        this.event = mainHandler.event;
        this.lastSpawnedEntity = mainHandler.lastSpawnedEntity;
        this.action = action;
        this.selectedTargets = new HashSet<>(parentActionsHandler.selectedTargets);
        this.doNotUseTryFlag = action.getActionType() == ActionType.CONTROLLER_CATCH_ERROR || parentActionsHandler.doNotUseTryFlag;
    }

    /**
     * Adds actions to queue and executes them.
     *
     * @param actions actions to execute.
     */
    public final void executeActions(@NotNull List<Action> actions) {
        actionsQueue.addAll(actions);
        executeNextAction();
    }

    /**
     * Inserts actions and executes them.
     * Used in {@link ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function functions}.
     * <p>
     * Actions from previous queue will be moved to the end.
     *
     * @param actions actions to execute first.
     */
    public final void addActions(@NotNull List<Action> actions) {
        List<Action> current = new ArrayList<>(actionsQueue);
        actionsQueue.clear();
        actionsQueue.addAll(actions);
        actionsQueue.addAll(current);
    }

    /**
     * Executes next action from queue.
     */
    public void executeNextAction() {
        if (executor.getPlanet().getMode() != Planet.Mode.PLAYING) {
            actionsQueue.clear();
            return;
        }
        if (actionsQueue.isEmpty()) {
            if (action instanceof MeasureTimeAction timer) {
                timer.measure();
            }
            if (getMainActionHandler() == this) {
                executor.getPlanet().getVariables().garbageCollector(this);
            }
            if (action instanceof RepeatAction repeater) {
                repeater.repeat();
                return;
            }
            ActionsHandler parent = getParentActionHandler();
            if (parent != null && parent != this && !(action instanceof LaunchMethodAction)) {
                /*
                 * Executes next action in parent actions handler.
                 */
                parent.executeNextAction();
            }
            return;
        }
        Action nextAction = actionsQueue.poll();
        if (nextAction != null) {
            prepareAction(nextAction);
        }
    }

    /**
     * Prepares action and executes it.
     *
     * @param action action to prepare and execute.
     */
    public void prepareAction(Action action) {
        if (waitDelay < 1) {
            executeAction(action);
        } else {
            BukkitRunnable executeActionLaterRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (action == null || action.getPlanet() == null || action.getPlanet().getMode() != Planet.Mode.PLAYING || !action.getPlanet().isLoaded()) {
                        cancel();
                    }
                    if (action != null) {
                        executeAction(action);
                        action.getPlanet().getTerritory().removeBukkitRunnable(this);
                    }
                }
            };
            action.getPlanet().getTerritory().addBukkitRunnable(executeActionLaterRunnable);
            executeActionLaterRunnable.runTaskLater(OpenCreative.getPlugin(), waitDelay);
        }
    }

    /**
     * Executes action or sends error to planet.
     *
     * @param action action to execute.
     */
    private void executeAction(Action action) {
        if (stopped) {
            if (action instanceof MeasureTimeAction timer) {
                timer.measure();
            }
            executor.getPlanet().getVariables().garbageCollector(getMainActionHandler());
            return;
        }
        if (doNotUseTryFlag) {
            action.prepareAndExecute(this);
        } else {
            try {
                action.prepareAndExecute(this);
            } catch (Exception error) {
                sendErrorMessage(action, error);
                removeAllActions();
                executor.getPlanet().getVariables().garbageCollector(getMainActionHandler());
                if (action.getPlanet().getLimits().isTooManyCodingErrors()) {
                    action.getPlanet().getTerritory().getScript().getExecutors().stopCode("errors limit");
                    sendPlanetCodeCriticalErrorMessage(action.getPlanet(), executor, getLocaleMessage("coding-error.errors-limit", false)
                            .replace("%limit%", String.valueOf(action.getPlanet().getLimits().getCodingErrorsLimit())));
                    return;
                }
            }
        }
        if (action instanceof WaitAction wait) {
            setWaitDelay(wait.getTime());
        } else {
            setWaitDelay(0);
        }
        if (!(action instanceof MultiAction || action instanceof Condition || action instanceof LaunchFunctionAction)) {
            executeNextAction();
        }
    }

    /**
     * Notifies planet about error, that has occurred
     * while executing action.
     *
     * @param action action, that caused error.
     * @param error  exception, that has occurred.
     */
    private void sendErrorMessage(Action action, Exception error) {
        String errorClass = error.getClass().getSimpleName();

        boolean unknown = !messageExists("coding-error." + errorClass.toLowerCase());
        String errorID = unknown ? "unknown" : errorClass.toLowerCase();
        String errorMessage = error.getMessage() == null ? errorClass : error.getMessage();
        errorMessage = errorMessage.replace("ua.mcchickenstudio.opencreative.coding.", "");
        String localizedMessage = getLocaleMessage("coding-error." + errorID);
        switch (error) {
            case PlayerException exception ->
                    localizedMessage = localizedMessage.replace("%player%", exception.getPlayerName());
            case NotTrustedLinkException exception ->
                    localizedMessage = localizedMessage.replace("%link%", exception.getLink());
            case UnknownMethodException exception ->
                    localizedMessage = localizedMessage.replace("%name%", exception.getName());
            case UnknownCycleException exception ->
                    localizedMessage = localizedMessage.replace("%name%", exception.getName());
            case UnknownFunctionException exception ->
                    localizedMessage = localizedMessage.replace("%name%", exception.getName());
            case UnsupportedEntityException exception -> {
                String localizedRequired = toKebabCase(exception.getRequired().getSimpleName());
                String localizedCurrent = toKebabCase(exception.getCurrent().getSimpleName()).replace("craft-", "");

                if (messageExists("entities." + localizedRequired)) {
                    localizedRequired = getLocaleMessage("entities." + localizedRequired);
                } else {
                    localizedRequired = localizedRequired.replace("-", "_");
                }

                if (messageExists("entities." + localizedCurrent)) {
                    localizedCurrent = getLocaleMessage("entities." + localizedCurrent);
                } else {
                    localizedCurrent = localizedCurrent.replace("-", "_");
                }

                localizedMessage = localizedMessage
                        .replace("%type%", localizedRequired)
                        .replace("%old%", localizedCurrent);
            }
            default -> {
            }
        }

        StringBuilder description = new StringBuilder();
        description.append(localizedMessage);

        if (unknown) {
            description.append(errorClass).append(": ").append(errorMessage);
        }

        sendPlanetCodeErrorMessage(executor, action, description.toString(), error);
    }

    /**
     * Clears all actions from queue.
     * <p>
     * Nothing will be executed.
     */
    public void removeAllActions() {
        actionsQueue.clear();
    }

    /**
     * Returns how many ticks should pass
     * before executing next action from queue.
     *
     * @return wait delay.
     */
    public long getWaitDelay() {
        return waitDelay;
    }

    /**
     * Sets how many ticks should pass before
     * executing next action.
     *
     * @param waitDelay wait delay.
     */
    public void setWaitDelay(long waitDelay) {
        this.waitDelay = waitDelay;
    }

    /**
     * Returns the main actions handler (executor thread).
     *
     * @return the main handler of actions.
     */
    public @NotNull ActionsHandler getMainActionHandler() {
        ActionsHandler handler = this;
        for (int depth = 0; depth < MAX_DEPTH; depth++) {
            ActionsHandler parent = handler.getParentActionHandler();
            if (parent == null) {
                return handler;
            }
            handler = parent;
        }
        return handler;
    }

    /**
     * Returns last spawned entity by code.
     *
     * @return last spawned entity.
     */
    public @Nullable Entity getLastSpawnedEntity() {
        return lastSpawnedEntity;
    }

    /**
     * Sets last spawned entity.
     *
     * @param lastSpawnedEntity last spawned entity.
     */
    public void setLastSpawnedEntity(Entity lastSpawnedEntity) {
        this.lastSpawnedEntity = lastSpawnedEntity;
    }

    /**
     * Checks whether action handler flagged to stop.
     *
     * @return true - stopped, false - not.
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Sets stopped flag to actions handler.
     *
     * @param stopped true - will stop code, false - not.
     */
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    /**
     * Returns parent actions handler, if exists.
     *
     * @return parent actions handler, or null.
     */
    public @Nullable ActionsHandler getParentActionHandler() {
        return parentActionsHandler;
    }

    /**
     * Returns world event, that launched
     * executor.
     *
     * @return world event.
     */
    public WorldEvent getEvent() {
        return event;
    }

    /**
     * Stops code in all parent handlers.
     */
    public void stopAllParentHandlers() {
        ActionsHandler handler = this;
        for (int depth = 0; depth < MAX_DEPTH; depth++) {
            ActionsHandler parent = handler.getParentActionHandler();
            if (parent == null) {
                return;
            }
            handler = parent;
            handler.setStopped(true);
            handler.removeAllActions();
            if (handler.getAction() instanceof RepeatAction repeatAction) {
                repeatAction.setMustStop(true);
            }
        }
    }

    /**
     * Returns first parent action handler with specified category of action.
     * <p>
     * If it doesn't find an action handler, it will return null.
     *
     * @param category category of action.
     * @return action handler with action category, or null - if not found.
     */
    public @Nullable ActionsHandler getFirstActionsHandler(@NotNull ActionCategory category) {
        ActionsHandler handler = this;
        for (int depth = 0; handler != null && depth < MAX_DEPTH; depth++) {
            Action action = handler.getAction();
            if (action != null && action.getActionCategory() == category) {
                return handler;
            }
            handler = handler.getParentActionHandler();
        }
        return null;
    }

    /**
     * Returns executor, that launched
     * actions handler.
     *
     * @return executor.
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * Returns multi action with brackets,
     * that launched actions handler, if exists.
     *
     * @return action, or null.
     */
    public @Nullable Action getAction() {
        return action;
    }

    /**
     * Returns selected targets, that can be modified
     * with selection action.
     *
     * @return selected targets.
     */
    public Set<Entity> getSelectedTargets() {
        return selectedTargets;
    }

    /**
     * Sets selected targets for actions with "selection" target.
     *
     * @param targets new targets.
     */
    public void setSelectedTargets(@NotNull Set<Entity> targets) {
        selectedTargets.clear();
        selectedTargets.addAll(targets);
    }

    @Override
    public String toString() {
        return "ActionsHandler" + hashCode() +  ". Planet: " + executor.getPlanet().getId()
                + " WaitDelay: " + waitDelay + " Stopped: " + stopped + " Queue Size: " + actionsQueue.size();
    }

    /**
     * Returns an unique ID of actions handler.
     * @return UUID of handler.
     */
    public final UUID getUniqueId() {
        return uuid;
    }
}
