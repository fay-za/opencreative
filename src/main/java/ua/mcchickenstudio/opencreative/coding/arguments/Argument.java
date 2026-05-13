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

package ua.mcchickenstudio.opencreative.coding.arguments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.KillerVictimEvent;
import ua.mcchickenstudio.opencreative.coding.placeholders.Placeholders;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.EventValueLink;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.substring;

/**
 * <h1>Argument</h1>
 * This class represents an argument, a field that
 * has id, name (path) and value. It's used to
 * get values, like text, numbers, locations etc.
 *
 * @see Arguments
 */
public class Argument {

    protected final @NotNull Planet planet;
    protected final @NotNull String path;
    protected final @NotNull ValueType type;
    protected final @NotNull Object value;

    /**
     * Creates instance of argument.
     *
     * @param planet associated planet.
     * @param type   type of value.
     * @param path   name of argument.
     * @param value  value.
     */
    public Argument(@NotNull Planet planet, @NotNull ValueType type, @NotNull String path, @NotNull Object value) {
        this.planet = planet;
        this.path = path;
        this.value = value;
        this.type = type;
    }

    /**
     * Replaces placeholders in text with values.
     *
     * @param text    text to parse.
     * @param handler handler to get some values.
     * @param action  action to get some values.
     * @return parsed text.
     */
    public static @NotNull String parseEntity(String text, ActionsHandler handler, Action action) {
        if (text.length() > 300) {
            return text;
        }
        return Placeholders.getInstance().parsePlaceholders(text, handler, action);
    }

    /**
     * Returns a name of argument.
     *
     * @return name of argument.
     */
    public @NotNull String getPath() {
        return path;
    }

    /**
     * Returns type of value.
     *
     * @return type of value.
     */
    public @NotNull ValueType getType() {
        return type;
    }

    /**
     * Returns value of argument.
     * <p>If value is a link to variable or event value
     * with null value, it will return link instead of
     * null. That prevents from null pointer problems.
     *
     * @param action action, that will be used, to parse text placeholders.
     * @return value of argument.
     */
    public @NotNull Object getValue(@NotNull Action action) {
        switch (value) {
            case VariableLink link -> {
                Object variableValue = planet.getVariables().getVariableValue(link, action);
                if (variableValue != null) {
                    return variableValue;
                }
            }
            case EventValueLink link -> {
                Entity target = switch (link.target()) {
                    case RANDOM_PLAYER -> {
                        List<Player> playerList = action.getExecutor().getPlanet().getTerritory().getWorld().getPlayers();
                        if (!playerList.isEmpty()) {
                            Random r = new Random();
                            int i = r.nextInt(playerList.size());
                            yield playerList.get(i);
                        } else {
                            yield null;
                        }
                    }
                    case KILLER -> {
                        if (action.getExecutor().getEvent() instanceof KillerVictimEvent mobEvent) {
                            yield mobEvent.getKiller();
                        }
                        yield null;
                    }
                    case VICTIM -> {
                        if (action.getExecutor().getEvent() instanceof KillerVictimEvent mobEvent) {
                            yield mobEvent.getVictim();
                        }
                        yield null;
                    }
                    case RANDOM_TARGET -> {
                        List<Entity> selectedTargets = new ArrayList<>(action.getHandler().getSelectedTargets());
                        if (selectedTargets.isEmpty()) {
                            yield null;
                        }
                        yield selectedTargets.get(new Random().nextInt(selectedTargets.size()));
                    }
                    case DEFAULT -> {
                        if (!action.getEvent().getSelection().isEmpty()) {
                            yield action.getEvent().getSelection().getFirst();
                        }
                        yield null;
                    }
                    case LAST_SPAWNED -> action.getHandler().getMainActionHandler().getLastSpawnedEntity();
                    default -> action.getEntity();
                };
                Object value = EventValues.getInstance().getValue(link.id().toLowerCase(), action.getHandler(), action, target);
                if (value != null) {
                    return value;
                }
            }
            case String string -> {
                return parseEntity(string, action.getHandler().getMainActionHandler(), action);
            }
            default -> {
                return value;
            }
        }
        return value;
    }

    /**
     * Checks whether variable is a list.
     *
     * @return true - is list, false - not a list.
     */
    public boolean isList() {
        return (this.type == ValueType.LIST);
    }

    @Override
    public @NotNull String toString() {
        return path + " - " + type.name() + ": " + substring(value.toString(), 30);
    }
}
