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

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LimitReachedVariablesEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static ua.mcchickenstudio.opencreative.coding.arguments.Argument.parseEntity;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getFileSize;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>WorldVariables</h1>
 * This class represents set of world variables. It includes
 * methods for finding and editing variables value.
 */
public final class WorldVariables {

    private final Planet planet;

    private final Map<LocalKey, WorldVariable> localVariables = new LinkedHashMap<>();
    private final Map<String, WorldVariable> globalVariables = new LinkedHashMap<>();
    private final Map<String, WorldVariable> savedVariables = new LinkedHashMap<>();

    public WorldVariables(Planet planet) {
        this.planet = planet;
    }

    /**
     * Returns instance of world variable by link.
     *
     * @param link link of variable, that contains name and type.
     * @param action action, that requested variable.
     * @return world variable, or null - if not found.
     */
    public @Nullable WorldVariable getVariable(@NotNull VariableLink link, @NotNull Action action) {
        return getVariable(parseEntity(link.getName(), action.getHandler(), action), link.getVariableType(), action.getHandler().getMainActionHandler());
    }

    /**
     * Returns instance of world variable by name, type and actions handler.
     *
     * @param name name of variable.
     * @param type type of variable (local, global, saved).
     * @param handler main actions handler, that stores local variable (local requires handler)
     * @return world variable, or null - if not found.
     */
    public @Nullable WorldVariable getVariable(@NotNull String name, @NotNull VariableLink.VariableType type, @Nullable ActionsHandler handler) {
        switch (type) {
            case SAVED -> {
                return savedVariables.get(name);
            }
            case LOCAL -> {
                if (handler == null) {
                    return null;
                }
                return localVariables.get(new LocalKey(name, handler.getUniqueId()));
            }
            default -> {
                return globalVariables.get(name);
            }
        }
    }

    private boolean handleVariableValue(@NotNull VariableLink link,
                                        @NotNull ValueType type,
                                        @NotNull Object value,
                                        @Nullable ActionsHandler handler,
                                        @Nullable Action action) {
        WorldVariable variable = (action != null) ? getVariable(link, action) : getVariable(link.getName(), link.getVariableType(), null);

        int total = getTotalVariablesAmount();
        if (total > planet.getLimits().getVariablesAmountLimit()) {
            if (action == null) {
                sendPlanetLimitWarningMessage(planet, "variables", "/env var list", total, planet.getLimits().getVariablesAmountLimit());
            } else {
                sendPlanetLimitWarningMessage(action, "variables", total, planet.getLimits().getVariablesAmountLimit());
            }
            new LimitReachedVariablesEvent(planet).callEvent();
            return false;
        }
        if (variable != null) {
            variable.setType(type);
            variable.setValue(value);
            removeVariable(link, action);
            addVariable(variable, link.getVariableType());
        } else {
            WorldVariable newVariable = (action != null)
                    ? new WorldVariable(parseEntity(link.getName(), action.getHandler(), action), link.getVariableType(), type, value, handler)
                    : new WorldVariable(link.getName(), link.getVariableType(), type, value, null);

            if (newVariable.getSize() + getTotalVariablesAmount() > planet.getLimits().getVariablesAmountLimit()) {
                if (action == null) {
                    sendPlanetLimitWarningMessage(planet, "variables", "/env var list", total, planet.getLimits().getVariablesAmountLimit());
                } else {
                    sendPlanetLimitWarningMessage(action, "variables", total, planet.getLimits().getVariablesAmountLimit());
                }
                new LimitReachedVariablesEvent(planet).callEvent();
                return false;
            }
            removeVariable(link, action);
            addVariable(newVariable, link.getVariableType());
        }

        if (action == null || action.getExecutor().isDebug()) {
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.variable." + (variable == null ? "created" : "set"), false)
                    .replace("%variable%", action != null ? parseEntity(link.getName(), action.getHandler(), action) : link.getName())
                    .replace("%value%", ValueType.getDisplayShortString(value)));
        }
        return true;
    }

    /**
     * Adds variable by instance and type.
     *
     * @param variable variable instance.
     * @param type type of variable.
     */
    private void addVariable(@NotNull WorldVariable variable, @NotNull VariableLink.VariableType type) {
        switch (type) {
            case SAVED -> savedVariables.put(variable.getName(), variable);
            case LOCAL -> {
                if (variable.getHandler() == null) return;
                localVariables.put(new LocalKey(variable.getName(),
                        variable.getHandler().getMainActionHandler().getUniqueId()), variable);
            }
            default -> globalVariables.put(variable.getName(), variable);
        }
    }

    /**
     * Sets variable value to new specified one. Used in actions.
     *
     * @param link    variable link to set.
     * @param type    new type of value.
     * @param value   new value.
     * @param handler handler of setting.
     * @param action  action of setting.
     */
    public void setVariableValue(VariableLink link, ValueType type, Object value, ActionsHandler handler, Action action) {
        handleVariableValue(link, type, value, handler, action);
    }

    /**
     * Sets variable value to new specified one.
     *
     * @param link  variable link to set.
     * @param type  new type of value.
     * @param value new value.
     * @return true - if successfully set, false - failed.
     */
    public boolean setVariableValue(VariableLink link, ValueType type, Object value) {
        return handleVariableValue(link, type, value, null, null);
    }

    /**
     * Gets variable value by variable link.
     *
     * @param link   link for variable.
     * @param action action.
     * @return variable - if found, null - variable not exists.
     */
    public Object getVariableValue(VariableLink link, Action action) {
        WorldVariable variable = getVariable(link, action);
        return variable != null ? variable.getValue() : null;
    }

    /**
     * Removes variable by variable link.
     *
     * @param link   link for variable.
     * @param action action.
     */
    public void removeVariable(VariableLink link, Action action) {
        String name = link.getName();
        if (action != null) name = parseEntity(link.getName(), action.getHandler(), action);
        switch (link.getVariableType()) {
            case SAVED -> savedVariables.remove(name);
            case LOCAL -> {
                if (action == null) return;
                localVariables.remove(new LocalKey(name,
                        action.getHandler().getMainActionHandler().getUniqueId()));
            }
            default -> globalVariables.remove(name);
        }
    }

    /**
     * Returns set of all current variables in world.
     *
     * @return set of all variables.
     */
    public @NotNull Set<WorldVariable> getSet() {
        Set<WorldVariable> variables = new LinkedHashSet<>();
        variables.addAll(localVariables.values());
        variables.addAll(globalVariables.values());
        variables.addAll(savedVariables.values());
        return variables;
    }

    /**
     * Clears all current variables in world.
     */
    public void clearVariables() {
        localVariables.clear();
        globalVariables.clear();
        savedVariables.clear();
    }

    /**
     * Loads variables from /planet/variables.json file.
     */
    public void load() {
        long startTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Loading variables for planet " + planet.getId());

        clearVariables();
        File variablesJson = FileUtils.getPlanetVariablesJson(planet);
        if (variablesJson == null || variablesJson.length() <= 2) {
            return;
        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray a = (JSONArray) jsonParser.parse(new FileReader(variablesJson));
            for (Object object : a) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                ValueType type = ValueType.valueOf((String) jsonObject.get("type"));
                Object value = jsonObject.get("value");
                value = deserializeObject(value, type);
                if (getTotalVariablesAmount() < planet.getLimits().getVariablesAmountLimit()) {
                    WorldVariable newVariable = new WorldVariable(name, VariableLink.VariableType.SAVED, type, value, null);
                    addVariable(newVariable, VariableLink.VariableType.SAVED);
                }
            }
        } catch (Exception e) {
            sendCriticalErrorMessage("Failed to parse JSON file " + variablesJson.getPath(), e);
        }

        long endTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Loaded " + getTotalVariablesAmount() + " variables for planet " + planet.getId() + " in " + (endTime - startTime) + " ms");
    }

    /**
     * Saves variables with type saved into /planet/variables.json file.
     */
    @SuppressWarnings("unchecked")
    public void save() {
        long startTime = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Saving variables for planet " + planet.getId());

        File variablesJson = FileUtils.getPlanetVariablesJson(planet);
        if (variablesJson == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        try (FileWriter file = new FileWriter(variablesJson.getPath())) {
            for (WorldVariable worldVariable : new HashSet<>(savedVariables.values())) {
                JSONObject objItem = new JSONObject();
                objItem.put("name", worldVariable.getName());
                objItem.put("type", worldVariable.getType().name());
                Object value = worldVariable.getValue();
                value = serializeObject(value);
                objItem.put("value", value);
                jsonArray.add(objItem);
            }
            file.write(jsonArray.toString());
        } catch (Exception e) {
            sendCriticalErrorMessage("Failed to save variables", e);
        }

        long endTime = System.currentTimeMillis();
        long fileSize = getFileSize(variablesJson);

        OpenCreative.getPlugin().getLogger().info(
                "Saved " + jsonArray.size() + " variables for planet " + planet.getId()
                        + " in " + (endTime - startTime) + " ms"
                        + " (" + byteCountToDisplaySize(fileSize) + ")"
        );
    }

    /**
     * Returns saved object value, that can be text, link or map.
     *
     * @param value value, that will be saved.
     * @return value for saving.
     */
    private @NotNull Object serializeObject(@Nullable Object value) {
        try {
            switch (value) {
                case ItemStack item -> {
                    return ItemUtils.saveItemAsByteArray(item);
                }
                case Location location -> {
                    Map<String, Number> locationMap = new HashMap<>();
                    locationMap.put("x", location.getX());
                    locationMap.put("y", location.getY());
                    locationMap.put("z", location.getZ());
                    locationMap.put("yaw", location.getYaw());
                    locationMap.put("pitch", location.getPitch());
                    return locationMap;
                }
                case Vector vector -> {
                    Map<String, Number> vectorMap = new HashMap<>();
                    vectorMap.put("x", vector.getX());
                    vectorMap.put("y", vector.getY());
                    vectorMap.put("z", vector.getZ());
                    return vectorMap;
                }
                case Color color -> {
                    Map<String, Integer> colorMap = new HashMap<>();
                    colorMap.put("red", color.getRed());
                    colorMap.put("green", color.getGreen());
                    colorMap.put("blue", color.getBlue());
                    return colorMap;
                }
                case Particle particle -> {
                    Map<String, String> particleMap = new HashMap<>();
                    particleMap.put("type", particle.name());
                    return particleMap;
                }
                case EventValueLink link -> {
                    Map<String, String> valueMap = new HashMap<>();
                    valueMap.put("name", link.id());
                    return valueMap;
                }
                case VariableLink link -> {
                    Map<String, String> variableMap = new HashMap<>();
                    variableMap.put("name", link.getName());
                    variableMap.put("type", link.getVariableType().name());
                    return variableMap;
                }
                case String text -> {
                    return text;
                }
                case Number number -> {
                    return String.valueOf(number);
                }
                case Boolean bool -> {
                    return String.valueOf(bool);
                }
                case List<?> list -> {
                    List<Object> newList = new ArrayList<>();
                    for (Object element : list) {
                        Map<String, Object> parsedElement = new HashMap<>();
                        ValueType insideType = ValueType.getByObject(element);
                        if (insideType == null || insideType == ValueType.LIST || insideType == ValueType.MAP) {
                            insideType = ValueType.TEXT;
                        }
                        parsedElement.put("type", insideType.name());
                        parsedElement.put("value", serializeObject(element));
                        newList.add(parsedElement);
                    }
                    return newList;
                }
                case Map<?, ?> map -> {
                    Map<Object, Object> newMap = new HashMap<>();
                    for (Object key : map.keySet()) {
                        Map<String, Object> newKey = new HashMap<>();
                        ValueType insideKeyType = ValueType.getByObject(key);
                        if (insideKeyType == null) insideKeyType = ValueType.TEXT;
                        newKey.put("type", insideKeyType.name());
                        newKey.put("value", serializeObject(key));

                        Map<String, Object> newValue = new HashMap<>();
                        Object mapValue = map.get(key);
                        ValueType insideValueType = ValueType.getByObject(mapValue);
                        if (insideValueType == null || insideValueType == ValueType.MAP) {
                            insideValueType = ValueType.TEXT;
                        }
                        newValue.put("type", insideValueType.name());
                        newValue.put("value", serializeObject(mapValue));
                        String serializedKey = new JSONObject(newKey).toString();
                        newMap.put(serializedKey, newValue);
                    }
                    return newMap;
                }
                case null, default -> {
                    return "null";
                }
            }
        } catch (Exception e) {
            return "null";
        }
    }

    /**
     * Returns loaded object value, that will be used to set saved variable's value.
     *
     * @param value value of saved instance (text, list, map).
     * @param type type of value.
     * @return loaded object for registering.
     */
    @SuppressWarnings("unchecked")
    private @NotNull Object deserializeObject(Object value, ValueType type) {
        try {
            switch (type) {
                case ITEM -> {
                    String itemString = (String) value;
                    if (itemString.contains("{")) {
                        return new ItemStack(Material.AIR);
                    } else {
                        return ItemUtils.loadItemFromByteArray(itemString);
                    }
                }
                case LOCATION -> {
                    double x, y, z;
                    float yaw, pitch;
                    Map<?, ?> locationMap = (Map<?, ?>) value;
                    x = (Double) locationMap.get("x");
                    y = (Double) locationMap.get("y");
                    z = (Double) locationMap.get("z");
                    yaw = ((Double) locationMap.get("yaw")).floatValue();
                    pitch = ((Double) locationMap.get("pitch")).floatValue();
                    return new Location(planet.getTerritory().getWorld(), x, y, z, yaw, pitch);
                }
                case VECTOR -> {
                    double x, y, z;
                    Map<?, ?> vectorMap = (Map<?, ?>) value;
                    x = (Double) vectorMap.get("x");
                    y = (Double) vectorMap.get("y");
                    z = (Double) vectorMap.get("z");
                    return new Vector(x, y, z);
                }
                case LIST -> {
                    List<Object> newList = new ArrayList<>();
                    List<?> oldList = (List<?>) value;
                    for (Object element : oldList) {
                        Object newElement = element;
                        if (newElement instanceof Map<?, ?> insideMap && insideMap.containsKey("type") && insideMap.containsKey("value")) {
                            ValueType keyType = ValueType.parseString(insideMap.get("type").toString());
                            newElement = deserializeObject(insideMap.get("value"), keyType);
                        } else {
                            newElement = deserializeObject(newElement, ValueType.getByObject(newElement));
                        }
                        newList.add(newElement);
                    }
                    return newList;
                }
                case MAP -> {
                    Map<Object, Object> newMap = new LinkedHashMap<>();
                    Map<?, ?> oldMap = (Map<?, ?>) value;
                    for (Object key : oldMap.keySet()) {
                        Object newKey = key;
                        Object newValue = oldMap.get(key);
                        Map<String, Object> deserializedKey = (Map<String, Object>) new JSONParser().parse((String) newKey);
                        newKey = deserializeObject(deserializedKey.get("value"), ValueType.parseString(deserializedKey.get("type").toString()));
                        if (newValue instanceof Map<?, ?> insideMap && insideMap.containsKey("type") && insideMap.containsKey("value")) {
                            ValueType keyValueType = ValueType.parseString(insideMap.get("type").toString());
                            newValue = deserializeObject(insideMap.get("value"), keyValueType);
                        } else {
                            newValue = deserializeObject(key, ValueType.getByObject(key));
                        }
                        newMap.put(newKey, newValue);
                    }
                    return newMap;
                }
                case COLOR -> {
                    int red, green, blue;
                    Map<?, ?> colorMap = (Map<?, ?>) value;
                    red = (int) colorMap.get("red");
                    green = (int) colorMap.get("green");
                    blue = (int) colorMap.get("blue");
                    return Color.fromRGB(red, green, blue);
                }
                case PARTICLE -> {
                    Map<?, ?> particleMap = (Map<?, ?>) value;
                    String particleType = (String) particleMap.get("type");
                    return Particle.valueOf(particleType);
                }
                case EVENT_VALUE -> {
                    Map<?, ?> eventValueMap = (Map<?, ?>) value;
                    String eventValueType = (String) eventValueMap.get("name");
                    Target target = Target.SELECTED;
                    if (eventValueMap.containsKey("target")) {
                        String targetType = (String) eventValueMap.get("target");
                        target = Target.getByText(targetType);
                    }
                    return new EventValueLink(eventValueType, target);
                }
                case VARIABLE -> {
                    Map<?, ?> varMap = (Map<?, ?>) value;
                    String varName = (String) varMap.get("name");
                    return new VariableLink(varName, VariableLink.VariableType.SAVED);
                }
                case NUMBER -> {
                    String text = String.valueOf(value);
                    if (text.contains(".")) {
                        return Double.parseDouble(text);
                    }
                    return Long.parseLong(text);
                }
                case BOOLEAN -> {
                    return Boolean.parseBoolean(String.valueOf(value));
                }
                case TEXT -> {
                    return String.valueOf(value);
                }
                default -> {
                    return "null";
                }
            }
        } catch (Exception ignored) {
            return "null";
        }
    }

    /**
     * Returns total size of variables. It includes list and maps elements.
     *
     * @return total size of variables.
     */
    public int getTotalVariablesAmount() {
        int size = 0;
        for (WorldVariable var : localVariables.values()) {
            size += var.getSize();
        }
        for (WorldVariable var : globalVariables.values()) {
            size += var.getSize();
        }
        for (WorldVariable var : savedVariables.values()) {
            size += var.getSize();
        }
        return size;
    }

    /**
     * Clears local variables with action handler type.
     *
     * @param actionsHandler handler.
     */
    public void garbageCollector(ActionsHandler actionsHandler) {
        for (LocalKey localKey : new HashSet<>(localVariables.keySet())) {
            if (actionsHandler.getUniqueId().equals(localKey.handlerId)) {
                localVariables.remove(localKey);
            }
        }
    }

    /**
     * Clears all global variables in world.
     */
    public void clearGlobalVariables() {
        globalVariables.clear();
    }

    /**
     * <h1>LocalKey</h1>
     * This class represents a key, that stores
     * name and handler ID of local variable.
     */
    static class LocalKey {

        private final String name;
        private final UUID handlerId;

        public LocalKey(String name, UUID handlerUUID) {
            this.name = name;
            this.handlerId = handlerUUID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LocalKey key)) return false;
            return name.equals(key.name) && handlerId.equals(key.handlerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, handlerId);
        }

        @Override
        public String toString() {
            return "LocalKey{name='" + name + "', handlerId=" + handlerId + '}';
        }
    }

}
