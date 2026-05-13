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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.text;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.CollectionWithCollectionException;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;
import java.util.Map;

public final class ConvertToJSONAction extends VariableAction {
    public ConvertToJSONAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        Object value = getArguments().getValue("variable", this);
        boolean pretty = getArguments().getBoolean("pretty", false, this);
        Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
        String text;

        if (value instanceof List<?> list) {
            JsonArray array = new JsonArray();
            for (Object element : list) {
                if (element instanceof List<?> || element instanceof Map<?, ?>) {
                    throw new CollectionWithCollectionException(List.class, element instanceof List<?> ? List.class : Map.class);
                }
                array.add(getPrimitive(element));
            }
            text = gson.toJson(array);
        } else if (value instanceof Map<?, ?> map) {
            JsonObject object = new JsonObject();
            for (Object key : map.keySet()) {
                Object element = map.get(key);
                if (element instanceof List<?> || element instanceof Map<?, ?>) {
                    throw new CollectionWithCollectionException(List.class, element instanceof List<?> ? List.class : Map.class);
                }
                JsonPrimitive primitiveKey = getPrimitive(key);
                JsonPrimitive primitiveValue = getPrimitive(element);
                object.add(primitiveKey.toString(), primitiveValue);
            }
            text = gson.toJson(object);
        } else {
            text = gson.toJson(getPrimitive(value));
        }
        if (text.length() > 1024) {
            throw new TooLongTextException(1024);
        }
        if (!text.isEmpty()) setVarValue(link, text);
    }

    private @NotNull JsonPrimitive getPrimitive(@NotNull Object object) {
        switch (object) {
            case String string -> {
                return new JsonPrimitive(string);
            }
            case Number number -> {
                return new JsonPrimitive(number);
            }
            case Boolean bool -> {
                return new JsonPrimitive(bool);
            }
            default -> {
                return new JsonPrimitive(String.valueOf(object));
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_CONVERT_TO_JSON;
    }
}
