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
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.CollectionWithCollectionException;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParseJSONAction extends VariableAction {
    public ParseJSONAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        String text = getArguments().getText("text", " ", this);
        if (text.length() > 1024) {
            throw new TooLongTextException(1024);
        }
        JsonElement json = JsonParser.parseString(text);
        if (json.isJsonArray()) {
            // ["test", 1, 2, true]
            JsonArray array = json.getAsJsonArray();
            List<Object> list = new ArrayList<>();
            for (JsonElement element : array.asList()) {
                if (element.isJsonArray() || element.isJsonObject()) {
                    throw new CollectionWithCollectionException(List.class, element.isJsonArray() ? List.class : Map.class);
                }
                Object object = getPrimitive(element);
                if (object != null) list.add(element);
            }
            setVarValue(link, list);
        } else if (json.isJsonObject()) {
            // {"test": 1.0}
            JsonObject object = json.getAsJsonObject();
            Map<String, Object> map = new LinkedHashMap<>();
            for (String key : object.keySet()) {
                JsonElement element = object.get(key);
                if (element.isJsonArray() || element.isJsonObject()) {
                    throw new CollectionWithCollectionException(List.class, element.isJsonArray() ? List.class : Map.class);
                }
                Object primitive = getPrimitive(element);
                if (primitive != null) map.put(key, primitive);
            }
            setVarValue(link, map);
        } else if (json.isJsonNull()) {
            setVarValue(link, "null");
        } else if (json.isJsonPrimitive()) {
            Object primitive = getPrimitive(json);
            if (primitive != null) setVarValue(link, primitive);
        }
    }

    private @Nullable Object getPrimitive(@NotNull JsonElement element) {
        if (element.isJsonNull()) {
            return "null";
        }
        if (!element.isJsonPrimitive()) {
            return null;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isString()) {
            return primitive.getAsString();
        }
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }
        if (primitive.isNumber()) {
            String numberString = element.getAsString();
            if (numberString.contains(".") || numberString.toLowerCase().contains("e")) {
                return primitive.getAsNumber().doubleValue();
            }
            return primitive.getAsNumber().intValue();
        }
        return null;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_PARSE_JSON;
    }
}
