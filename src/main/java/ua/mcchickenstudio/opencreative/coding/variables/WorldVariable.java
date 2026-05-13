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

package ua.mcchickenstudio.opencreative.coding.variables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

import java.util.List;
import java.util.Map;

/**
 * <h1>WorldVariable</h1>
 * This class represents a variable in world, that
 * has type and stores value.
 *
 * <p>Variables can be <b>local, global, saved</b>.
 * <p>Local - stores only in ActionsHandler
 * <p>Global - stores in world while it's loaded
 * <p>Saved - stores in world forever
 */
public final class WorldVariable {

    private final @NotNull String name;
    private final @NotNull VariableLink.VariableType varType;
    private final @Nullable ActionsHandler handler;

    private @Nullable Object value;
    private @NotNull ValueType valueType;

    /**
     * Creates instance of variable, that has name, value, type,
     * value type and actions handler.
     *
     * @param name    name of variable.
     * @param varType type of variable (local, global, saved).
     * @param type    type of value.
     * @param value   value.
     * @param handler actions handler.
     */
    public WorldVariable(@NotNull String name, @NotNull VariableLink.VariableType varType, @NotNull ValueType type, @Nullable Object value, @Nullable ActionsHandler handler) {
        this.name = name;
        this.valueType = type;
        this.varType = varType;
        this.value = value;
        this.handler = handler;
    }

    /**
     * Returns actions handler associated with
     * variable. Can be null when variable
     * is loaded from storage, or when
     * player creates variable with
     * command.
     *
     * @return actions handler, or null.
     */
    public @Nullable ActionsHandler getHandler() {
        return handler;
    }

    /**
     * Returns value of variable.
     *
     * @return value of variable.
     */
    public @Nullable Object getValue() {
        return value;
    }

    /**
     * Sets a new value to variable.
     *
     * @param value new value.
     */
    public void setValue(@Nullable Object value) {
        this.value = value;
    }

    /**
     * Returns type of value.
     *
     * @return value type.
     */
    public @NotNull ValueType getType() {
        return valueType;
    }

    /**
     * Sets a new value type to variable.
     *
     * @param type new type.
     */
    public void setType(@NotNull ValueType type) {
        this.valueType = type;
    }

    /**
     * Returns type of variable (local, global, saved).
     *
     * @return type of variable.
     */
    public @NotNull VariableLink.VariableType getVarType() {
        return varType;
    }

    /**
     * Returns name of variable.
     *
     * @return name of variable.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns a size of variable.
     * <p>If value is map, then keys amount will be added to size.
     * <p>If value is list, then elements amount will be added to size.
     * <p>Otherwise, it will return 1.
     *
     * @return size of variable.
     */
    public int getSize() {
        int size = 1;
        if (value instanceof List<?> list) {
            size += list.size();
        } else if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getValue() instanceof List<?> list) {
                    size += list.size() + 1;
                } else if (entry.getKey() instanceof List<?> list) {
                    size += list.size() + 1;
                } else {
                    size += 2 * map.size();
                }
            }
        }
        return size;
    }

    @Override
    public String toString() {
        return name + " - " + varType.name() + ": " + valueType.name() + " " + value;
    }
}
