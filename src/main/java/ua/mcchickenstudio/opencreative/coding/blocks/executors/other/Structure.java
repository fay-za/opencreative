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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.other;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;

import java.util.HashMap;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugExecutor;

/**
 * <h1>Structure</h1>
 * This class represents a data structure, acting similarly to a C struct.
 * It stores key-value pairs (fields) that belong to a single named instance.
 */
public final class Structure extends NameableExecutor {

    private final Map<String, Object> fields;

    public Structure() {
        super("structure", ExecutorCategory.STRUCTURE);
        this.fields = new HashMap<>();
    }

    @Override
    public void run(@NotNull WorldEvent event) {
        sendCodingDebugExecutor(this);
        executeActions(event);
    }

    /**
     * Sets or updates a field within the structure.
     *
     * @param fieldName The name of the field.
     * @param value     The value to store.
     */
    public void setField(@NotNull String fieldName, @Nullable Object value) {
        this.fields.put(fieldName, value);
    }

    /**
     * Retrieves a field's value from the structure.
     *
     * @param fieldName The name of the field.
     * @return The stored value, or null if it doesn't exist.
     */
    @Nullable
    public Object getField(@NotNull String fieldName) {
        return this.fields.get(fieldName);
    }

    /**
     * Checks if the structure contains a specific field.
     *
     * @param fieldName The name of the field.
     * @return True if the field exists, false otherwise.
     */
    public boolean hasField(@NotNull String fieldName) {
        return this.fields.containsKey(fieldName);
    }

    /**
     * Removes a field from the structure.
     *
     * @param fieldName The name of the field to remove.
     */
    public void removeField(@NotNull String fieldName) {
        this.fields.remove(fieldName);
    }

    /**
     * Clears all fields from the structure.
     */
    public void clearFields() {
        this.fields.clear();
    }

    /**
     * Gets a copy of all fields currently stored in the structure.
     *
     * @return A map of all fields.
     */
    @NotNull
    public Map<String, Object> getFields() {
        return new HashMap<>(this.fields);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Structure";
    }

    @Override
    public @NotNull String getDescription() {
        return "Stores multiple values as fields under a single named structure, similar to a struct in C.";
    }
}