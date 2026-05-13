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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlock;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.ChatEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugExecutor;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Executor</h1>
 * This class represents Executor that has actions to run.
 * Executor will be executed on events in planet.
 *
 * @author McChicken Studio
 * @version 6.0
 * @since 5.0
 */
public abstract class Executor implements CodingBlock, ExtensionContent {

    private final String id;
    private final ExecutorCategory category;

    private Planet planet;
    private int x;
    private int y;
    private int z;

    private WorldEvent event;
    private List<Action> actions;
    private ActionsHandler handler;
    private boolean debug;
    private int calls;

    /**
     * Constructor of empty Executor without block data.
     * <p>
     * Executing actions will be not able without initialization.
     * <p>
     * To initialize, use {@link #init(Planet, int, int, int)}
     * @param id          short id of executor that will be used in signs and translations.
     *                    <p>
     *                    It must be lower-snake-cased, for example: "player_join", "cycle".
     *                    If some of registered executors has same ID as new, it will be not added.
     * @param category category of executor.
     */
    public Executor(@NotNull String id, @NotNull ExecutorCategory category) {
        this.id = id;
        this.category = category;
    }

    /**
     * Initializes an executor and sets planet and coding block location.
     * Use this method after creating executor.
     *
     * @param planet Planet where executor will work.
     * @param x      X from Executor's block location in developers planet.
     * @param y      Y from Executor's block location in developers planet.
     * @param z      Z from Executor's block location in developers planet.
     * @throws IllegalStateException if executor is already initialized.
     */
    public void init(@NotNull Planet planet, int x, int y, int z) {
        if (this.planet != null) {
            throw new IllegalStateException("Executor is already initialized and associated with planet " + this.planet.getId());
        }
        this.planet = planet;
        this.x = x;
        this.y = y;
        this.z = z;
        this.actions = new ArrayList<>();
    }

    /**
     * Checks executor, executes actions and sends
     * information about executor.
     *
     * @param event Event that occurred in planet.
     * @throws IllegalStateException if executor is not initialized.
     */
    public void run(@NotNull WorldEvent event) {
        if (this.planet == null) {
            throw new IllegalStateException("Executor is not initialized and not associated with any planet.");
        }
        if (isDisabled()) {
            return;
        }
        sendCodingDebugExecutor(this);
        executeActions(event);
    }

    /**
     * Executes all actions with specified event.
     *
     * @param event Event that has happened in planet.
     */
    protected void executeActions(@NotNull WorldEvent event) {
        if (this.planet == null) {
            throw new IllegalStateException("Executor is not initialized and not associated with any planet.");
        }
        this.event = event;
        handler = new ActionsHandler(this);
        if (event instanceof ChatEvent chatEvent && !actions.isEmpty()) {
            chatEvent.setHandledByCode(true);
        }
        handler.executeActions(actions);
    }

    /**
     * Returns coding block category of this executor.
     *
     * @return category of executor.
     */
    public final @NotNull ExecutorCategory getBlockCategory() {
        return category;
    }

    /**
     * Returns planet, that was associated with executor.
     *
     * @return planet.
     */
    public final @NotNull Planet getPlanet() {
        return planet;
    }

    /**
     * Checks whether is executor block marked for debugging.
     *
     * @return true - for debug, false - not.
     */
    public final boolean isDebug() {
        return debug;
    }

    /**
     * Enables or disables sending debug logs about
     * executor and actions inside.
     *
     * @param debug true - enabled, false - disabled.
     */
    public final void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Returns last world event of executor.
     * <p>
     * When a new event happens, it will be replaced
     * with a new one.
     *
     * @return last world event.
     */
    public final WorldEvent getEvent() {
        return event;
    }

    public final ActionsHandler getHandler() {
        return handler;
    }

    public final @NotNull List<Action> getActions() {
        return actions;
    }

    /**
     * Sets actions list for executor.
     *
     * @param actions List of actions.
     */
    public final void setActions(@NotNull List<Action> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    /**
     * Increases calls amount by 1.
     */
    public final void increaseCall() {
        calls++;
    }

    /**
     * Decreases calls amount by 1.
     */
    public final void decreaseCall() {
        calls--;
    }

    /**
     * Returns how many times executor was called
     * to execute actions.
     *
     * @return amount of last executions.
     */
    public final int getLastCalls() {
        return calls;
    }

    /**
     * Checks whether executor is disabled
     * and cannot be called.
     *
     * @return true - disabled, false - not.
     */
    public final boolean isDisabled() {
        return OpenCreative.getSettings().getCodingSettings().isDisabledEvent(this);
    }

    /**
     * Returns id of executor, that will be used
     * to find it in registry.
     *
     * @return id of executor.
     */
    public final @NotNull String getID() {
        return id;
    }

    /**
     * Returns localized name of executor.
     *
     * @return localized name.
     */
    public final @NotNull String getLocaleName() {
        return getLocaleMessage("items.developer.events." + id.replace("_", "-") + ".name", false);
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final int getZ() {
        return z;
    }

    @Override
    public final int hashCode() {
        return (id.toLowerCase() + x + " " + y + " " + z).hashCode();
    }

    @Override
    public String toString() {
        return "Executor | Planet: " + getPlanet().getWorldName() + " Coords: " + x + " " + y + " " + z;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Executor executor)) return false;
        if (executor.x != this.x) return false;
        if (executor.y != this.y) return false;
        if (executor.z != this.z) return false;
        return Objects.equals(executor.id, this.id);
    }

}
