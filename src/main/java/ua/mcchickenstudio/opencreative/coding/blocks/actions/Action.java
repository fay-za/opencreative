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

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Argument;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlock;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.selectionactions.SelectionAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.KillerVictimEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Action</h1>
 * This class represents Action that will be executed in executor.
 *
 * @author McChicken Studio
 * @version 6.0
 * @since 5.0
 */
public abstract class Action implements CodingBlock {

    protected final Arguments arguments;
    private final Executor executor;
    private final Target target;
    private final int x;
    protected Entity entity;
    protected WorldEvent event;
    protected ActionsHandler handler;

    /**
     * Creates an Action with linked executor and specified arguments.
     *
     * @param executor Executor where this action will be added.
     * @param target   Target, that will execute this action.
     * @param x        X coordinate from Action's block location in developers planet.
     * @param args     List of arguments for action.
     */
    public Action(Executor executor, Target target, int x, Arguments args) {
        this.executor = executor;
        this.target = target;
        this.x = x;
        this.arguments = args;
    }

    /**
     * Prepares action for executing, sets handler and event, and executes action with target.
     *
     * @param handler ActionsHandler that stores event data and temporary variables.
     */
    public void prepareAndExecute(@NotNull ActionsHandler handler) {
        if (getActionType().isDisabled()) {
            sendCodingDebugLog(getPlanet(), "Action is disabled, cannot work: " + getActionType().getLocaleName());
            return;
        }
        this.handler = handler;
        this.event = handler.getEvent();
        sendCodingDebugAction(this);
        List<Entity> targets = getTargets();
        if (getPlanet().getLimits().isTooManyActionsAtOnce(targets.size())) {
            getPlanet().getTerritory().getScript().getExecutors().stopCode("actions limit");
            sendPlanetCodeCriticalErrorMessage(getPlanet(), executor, getLocaleMessage("coding-error.actions-limit", false)
                    .replace("%limit%", String.valueOf(getPlanet().getLimits().getCodingActionsCallsLimit())));
            return;
        }
        for (Entity entity : targets) {
            if (entity == null) continue;
            if (entity.getWorld().equals(getPlanet().getWorld()) || !OpenCreative.getSettings().getCodingSettings().isIgnoreActionsIfEntityNotInWorld()) {
                this.entity = entity;
                execute(entity);
            }
        }
    }

    /**
     * Executes action with specified entity.
     *
     * @param entity Entity to execute action.
     */
    protected abstract void execute(@Nullable Entity entity);

    /**
     * Returns type of action, that contains information
     * about action: icon material, arguments list.
     *
     * @return Type of action.
     */
    public abstract @NotNull ActionType getActionType();

    /**
     * Returns category of action, that contains information
     * about action: glass pane materials, block materials.
     *
     * @return Category of action.
     */
    public abstract @NotNull ActionCategory getActionCategory();

    /**
     * Returns arguments of action.
     *
     * @return Arguments of action.
     */
    protected final @NotNull Arguments getArguments() {
        return arguments;
    }

    /**
     * Returns executor, that stores this action.
     *
     * @return Executor with this action.
     */
    public final Executor getExecutor() {
        return executor;
    }

    /**
     * Returns a set of entities whose name or UUID is equal to specified text.
     *
     * @param text Text to compare world's entities names and UUIDs.
     * @return Set of entities with same names or UUIDs, as text.
     */
    protected Set<Entity> getEntitiesByNameOrUUID(String text) {
        Set<Entity> entities = new HashSet<>();
        if (getWorld() == null) return entities;
        for (Entity entity : getWorld().getEntities()) {
            if (entity.getName().equalsIgnoreCase(text) || entity.getUniqueId().toString().equalsIgnoreCase(text)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Returns an entity whose name or UUID is equal to specified text.
     *
     * @param text Text to compare world's entities names and UUIDs.
     * @return Entity, or null.
     */
    protected @Nullable Entity getEntityByNameOrUUID(String text) {
        if (getWorld() == null) return null;
        for (Entity entity : getWorld().getEntities()) {
            if (entity.getName().equalsIgnoreCase(text) || entity.getUniqueId().toString().equalsIgnoreCase(text)) {
                return entity;
            }
        }
        return null;
    }

    protected Set<Player> getPlayersByNameOrUUID(String text) {
        Set<Player> players = new HashSet<>();
        if (getWorld() == null) return players;
        for (Player player : getWorld().getPlayers()) {
            if (player.getName().equalsIgnoreCase(text) || player.getUniqueId().toString().equalsIgnoreCase(text)) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Returns planet's world, where action will be executed.
     *
     * @return Planet's world.
     */
    protected World getWorld() {
        return getPlanet().getTerritory().getWorld();
    }

    protected Planet getPlanet() {
        return executor.getPlanet();
    }

    /**
     * Returns involved entity in action from ActionsHandler.
     *
     * @return Involved entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets entity involved in executor's event.
     *
     * @param entity New entity.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Returns last stored event in action.
     *
     * @return CreativeEvent, that called executor with this action.
     */
    public WorldEvent getEvent() {
        return event;
    }

    /**
     * Sets new event.
     *
     * @param event new event of action.
     */
    public void setEvent(WorldEvent event) {
        this.event = event;
    }

    /**
     * Returns current ActionsHandler in action.
     *
     * @return Handler of this action.
     */
    public ActionsHandler getHandler() {
        return handler;
    }

    /**
     * Sets new ActionsHandler.
     *
     * @param handler New handler of action.
     */
    public void setHandler(ActionsHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns enum of target.
     *
     * @return Enum of target.
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Returns list of entities that will execute this action.
     *
     * @return List of entities to execute action.
     */
    protected List<Entity> getTargets() {
        List<Entity> entities = new ArrayList<>();
        List<Entity> eventEntities = getHandler().getEvent().getSelection();
        switch (target) {
            case RANDOM_PLAYER -> {
                Player randomPlayer = null;
                List<Player> playerList = this.getExecutor().getPlanet().getTerritory().getWorld().getPlayers();
                if (!playerList.isEmpty()) {
                    Random r = new Random();
                    int i = r.nextInt(playerList.size());
                    randomPlayer = playerList.get(i);
                }
                if (randomPlayer != null) {
                    entities.add(randomPlayer);
                }
            }
            case ALL_PLAYERS -> {
                List<Player> playerList = this.getExecutor().getPlanet().getTerritory().getWorld().getPlayers();
                if (!playerList.isEmpty()) {
                    entities.addAll(playerList);
                }
            }
            case KILLER -> {
                Entity killer = getKiller();
                if (killer != null) {
                    entities.add(killer);
                }
            }
            case VICTIM -> {
                Entity victim = getVictim();
                if (victim != null) {
                    entities.add(victim);
                }
            }
            case SELECTED -> entities.addAll(getHandler().getSelectedTargets());
            case ALL_ENTITIES -> {
                int amount = 0;
                for (Entity entity : getWorld().getEntities()) {
                    if (amount > getPlanet().getLimits().getEntitiesLimit()) {
                        break;
                    }
                    if (!(entity instanceof Player)) {
                        entities.add(entity);
                        amount++;
                    }
                }
            }
            case RANDOM_TARGET -> {
                List<Entity> selectedTargets = new ArrayList<>(getHandler().getSelectedTargets());
                if (!selectedTargets.isEmpty()) {
                    entities.add(selectedTargets.get(new Random().nextInt(selectedTargets.size())));
                }
            }
            case LAST_SPAWNED -> {
                Entity spawned = getHandler().getMainActionHandler().getLastSpawnedEntity();
                if (spawned != null) {
                    entities.add(spawned);
                }
            }
            default -> entities.addAll(eventEntities);
        }
        entities.removeIf(entity -> entity instanceof Player player && ChangedWorld.isPlayerWithLocation(player));
        //entities.removeIf(entity -> !entity.getWorld().equals(getPlanet().getWorld()));
        int selectionLimit = getPlanet().getLimits().getEntitiesLimit() + getPlanet().getPlayers().size(); // adding players count if entities limit is set to 0
        if (entities.size() > selectionLimit) {
            entities = entities.subList(0, selectionLimit);
        }
        return entities;
    }

    private Entity getVictim() {
        if (executor.getEvent() instanceof KillerVictimEvent victimEvent) {
            return victimEvent.getVictim();
        }
        return null;
    }

    /**
     * Returns entity killer, that involved in damage event.
     *
     * @return Killer, or null if there's no involved entity in damage event.
     */
    private Entity getKiller() {
        if (executor.getEvent() instanceof KillerVictimEvent mobEvent) {
            return mobEvent.getKiller();
        }
        return null;
    }

    /**
     * Sets value in local, global or saved variable in world.
     *
     * @param link  Link of variable.
     * @param value New value.
     */
    protected void setVarValue(@Nullable VariableLink link, Object value) {
        if (link != null) {
            ValueType type = ValueType.getByObject(value);
            if (type == null) {
                type = ValueType.TEXT;
            }
            if (value instanceof String text) {
                if (text.length() > 1024) {
                    throw new TooLongTextException(1024);
                }
            }
            getPlanet().getVariables().setVariableValue(link, type, value, getHandler().getMainActionHandler(), this);
        }
    }

    /**
     * Returns a list of all arguments in this action.
     *
     * @return List of action's arguments.
     */
    public List<Argument> getArgumentsList() {
        return getArguments().getArgumentList();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return executor.getY();
    }

    @Override
    public int getZ() {
        return executor.getZ();
    }

}
