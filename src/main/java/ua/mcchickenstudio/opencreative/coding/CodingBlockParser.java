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

package ua.mcchickenstudio.opencreative.coding;

import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetCompileErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CodingBlockParser</h1>
 * This class represents parser of coding blocks. It has methods to
 * read coding block information and save it into planet's code script.getConfig().
 *
 * @see CodeScript
 */
public class CodingBlockParser {

    private final long maxScriptSize;
    private final boolean saveEmptyArguments;

    public CodingBlockParser(@NotNull DevPlanet devPlanet) {
        this(devPlanet.getPlanet().getGroup().getScriptSizeLimit());
    }

    public CodingBlockParser(@NotNull DevPlanet devPlanet, boolean saveEmptyArguments) {
        this(devPlanet.getPlanet().getGroup().getScriptSizeLimit(), saveEmptyArguments);
    }

    public CodingBlockParser(int scriptSizeLimit, boolean saveEmptyArguments) {
        this.maxScriptSize = scriptSizeLimit * 1024L * 1024L;
        this.saveEmptyArguments = saveEmptyArguments;
    }

    public CodingBlockParser(int scriptSizeLimit) {
        this(scriptSizeLimit, false);
    }

    private static ValueType parseItemType(ItemStack item) {
        ValueType valueType = ValueType.ITEM;
        if (item == null) return ValueType.ITEM;
        if (item.getItemMeta() != null) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            String dataType = container.get(getCodingValueKey(), PersistentDataType.STRING);
            if (dataType != null) {
                try {
                    valueType = ValueType.valueOf(dataType);
                } catch (Exception ignored) {
                }
            }
            if (valueType == ValueType.VARIABLE || valueType == ValueType.EVENT_VALUE) {
                String variableType = container.get(getCodingVariableTypeKey(), PersistentDataType.STRING);
                if (variableType == null || variableType.isEmpty()) {
                    return ValueType.ITEM;
                }
            }
        }
        return valueType;
    }

    public static Object parseItemValue(ItemStack item) {
        if (item == null) return "EMPTY";
        ValueType valueType = parseItemType(item);
        if (valueType == ValueType.ITEM) {
            if (item.isEmpty()) {
                return "EMPTY";
            }
            return item.serialize();
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item.serialize();
        }
        Component itemDisplayName = itemMeta.displayName();
        if (itemDisplayName == null) {
            return item.serialize();
        }
        String name = itemMeta.getDisplayName();
        switch (valueType) {
            case TEXT -> {
                return name;
            }
            case BOOLEAN -> {
                boolean bool;
                try {
                    bool = Boolean.parseBoolean(ChatColor.stripColor(name));
                    return bool;
                } catch (Exception ignored) {
                }
            }
            case NUMBER -> {
                double number;
                try {
                    number = Double.parseDouble(ChatColor.stripColor(name));
                    return number;
                } catch (Exception ignored) {
                }
            }
            case LOCATION -> {
                Map<String, Object> locationMap = new HashMap<>();
                String locationString = ChatColor.stripColor(name);
                String[] locCoords = locationString.split(" ");
                if (locCoords.length == 5) {
                    try {
                        locationMap.put("x", Double.parseDouble(locCoords[0]));
                        locationMap.put("y", Double.parseDouble(locCoords[1]));
                        locationMap.put("z", Double.parseDouble(locCoords[2]));
                        locationMap.put("yaw", Float.parseFloat(locCoords[3]));
                        locationMap.put("pitch", Float.parseFloat(locCoords[4]));
                        return locationMap;
                    } catch (Exception ignored) {
                    }
                }
            }
            case VECTOR -> {
                Map<String, Object> vectorMap = new HashMap<>();
                String vectorString = ChatColor.stripColor(name);
                String[] coords = vectorString.split(" ");
                if (coords.length == 3) {
                    try {
                        vectorMap.put("x", Double.parseDouble(coords[0]));
                        vectorMap.put("y", Double.parseDouble(coords[1]));
                        vectorMap.put("z", Double.parseDouble(coords[2]));
                        return vectorMap;
                    } catch (Exception ignored) {
                    }
                }
            }
            case COLOR -> {
                Map<String, Object> colorMap = new HashMap<>();
                String colorString = ChatColor.stripColor(name);
                String[] colors = colorString.split(" ");
                if (colors.length == 3) {
                    try {
                        colorMap.put("red", Integer.parseInt(colors[0]));
                        colorMap.put("green", Integer.parseInt(colors[1]));
                        colorMap.put("blue", Integer.parseInt(colors[2]));
                        return colorMap;
                    } catch (Exception ignored) {
                    }
                }
            }
            case VARIABLE -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String variableType = container.get(getCodingVariableTypeKey(), PersistentDataType.STRING);
                if (variableType == null) break;
                VariableLink.VariableType type;
                Map<String, String> variableMap = new HashMap<>();
                try {
                    type = VariableLink.VariableType.valueOf(variableType);
                    variableMap.put("name", ChatColor.stripColor(name));
                    variableMap.put("type", type.name());
                    return variableMap;
                } catch (Exception ignored) {
                }
            }
            case EVENT_VALUE -> {
                String variableType = getPersistentData(item, getCodingVariableTypeKey());
                if (variableType.startsWith("PLOT")) {
                    variableType = variableType.replace("PLOT", "PLANET");
                    ItemUtils.setPersistentData(item, getCodingVariableTypeKey(), variableType);
                }
                if (!EventValues.getInstance().exists(variableType.toLowerCase())) {
                    break;
                }
                String targetType = getPersistentData(item, getCodingTargetTypeKey());
                Map<String, String> valueMap = new HashMap<>();
                valueMap.put("name", variableType);
                if (!targetType.isEmpty()) {
                    valueMap.put("target", targetType);
                }
                return valueMap;
            }
            case PARTICLE -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String particleType = container.get(getCodingParticleTypeKey(), PersistentDataType.STRING);
                if (particleType == null) break;
                Particle type;
                Map<String, String> valueMap = new HashMap<>();
                try {
                    type = Particle.valueOf(particleType);
                    valueMap.put("type", type.name());
                    return valueMap;
                } catch (Exception ignored) {
                }
            }
        }
        return name;
    }

    /**
     * Saves all code lines in developer planet and
     * launches new code.
     *
     * @param devPlanet developer planet to parse code.
     */
    public CompletableFuture<Boolean> parseCode(@NotNull DevPlanet devPlanet) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (!devPlanet.isCodeChanged()) {
            sendCodingDebugLog(devPlanet.getPlanet(), "Not parsing code, nothing was changed.");
            future.complete(true);
            return future;
        }
        devPlanet.setCurrentlySavingCode(true);
        long time = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Parsing code in planet " + devPlanet.getPlanet().getId() + "...");
        sendCodingDebugLog(devPlanet.getPlanet(), getLocaleMessage("coding-debug.parsing-code", false));
        CodeScript script = devPlanet.getPlanet().getTerritory().getScript();
        script.clear(false);
        parseExecutors(devPlanet, script.getConfig(), devPlanet.getChangedColumns());
        devPlanet.clearColumnsChanges();
        OpenCreative.getPlugin().getLogger().info("Parsed code in planet " + devPlanet.getPlanet().getId() + " in " + (System.currentTimeMillis() - time) + " ms.");
        sendCodingDebugLog(devPlanet.getPlanet(), getLocaleMessage("coding-debug.parsed-code", false)
                .replace("%time%", String.valueOf(Math.floor((System.currentTimeMillis() - time) / 10.0) / 100.0)));
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            if (script.saveCode()) {
                sendCodingDebugLog(devPlanet.getPlanet(), "Shutting down executors...");
                devPlanet.getPlanet().getTerritory().stopBukkitRunnables();
                devPlanet.setCurrentlySavingCode(false);
                if (!devPlanet.getPlanet().isLoaded()) return;
                devPlanet.getPlanet().getTerritory().getScript().loadCode().thenAccept((result) -> {
                   future.complete(true);
                });
            }
        });
        return future;
    }

    /**
     * Saves all code lines in developer planet and
     * launches new code.
     *
     * @param devPlanet developer planet to parse code.
     */
    public CompletableFuture<Boolean> recompileCode(@NotNull DevPlanet devPlanet) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (!devPlanet.isCodeChanged()) {
            sendCodingDebugLog(devPlanet.getPlanet(), "Not recompiling code, nothing was changed.");
            future.complete(true);
            return future;
        }
        devPlanet.setCurrentlySavingCode(true);
        long time = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Recompiling code in planet " + devPlanet.getPlanet().getId() + "...");
        sendCodingDebugLog(devPlanet.getPlanet(), getLocaleMessage("coding-debug.parsing-code", false));
        CodeScript script = devPlanet.getPlanet().getTerritory().getScript();
        script.clear(true);
        parseAllExecutors(devPlanet, script.getConfig());
        devPlanet.clearColumnsChanges();
        OpenCreative.getPlugin().getLogger().info("Recompiled code in planet " + devPlanet.getPlanet().getId() + " in " + (System.currentTimeMillis() - time) + " ms.");
        sendCodingDebugLog(devPlanet.getPlanet(), getLocaleMessage("coding-debug.parsed-code", false)
                .replace("%time%", String.valueOf(Math.floor((System.currentTimeMillis() - time) / 10.0) / 100.0)));
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            if (script.saveCode()) {
                sendCodingDebugLog(devPlanet.getPlanet(), "Shutting down executors...");
                devPlanet.getPlanet().getTerritory().stopBukkitRunnables();
                devPlanet.setCurrentlySavingCode(false);
                if (!devPlanet.getPlanet().isLoaded()) return;
                devPlanet.getPlanet().getTerritory().getScript().loadCode().thenAccept((result) -> {
                    future.complete(true);
                });
            }
        });
        return future;
    }

    /**
     * Adds all code lines from developer planet to configuration.
     * Don't forget to save it.
     *
     * @param devPlanet developer planet to parse code.
     * @param config    config to add executors.
     */
    public void parseAllExecutors(DevPlanet devPlanet, CodeStorage config) {

        Set<Location> locations = new HashSet<>();
        List<DevPlatform> platforms = devPlanet.getPlatforms();

        // For platforms
        for (DevPlatform platform : platforms) {
            // For coding executors
            Location begin = devPlanet.getDevPlatformer().getPlatformBeginLocation(platform);
            Location end = devPlanet.getDevPlatformer().getPlatformEndLocation(platform);
            int y = begin.getBlockY() + 1;
            int x = begin.getBlockX() + 4;
            for (int z = begin.getBlockZ() + 4; z <= end.getBlockZ() - 4; z = z + 4) {
                Block executorBlock = devPlanet.getWorld().getBlockAt(x, y, z);
                locations.add(executorBlock.getLocation());
            }
        }
        parseExecutors(devPlanet, config, locations);

    }

    /**
     * Adds specified code lines from developer planet to configuration.
     * Locations represent locations of executors cells (blue glass).
     * Don't forget to save it.
     *
     * @param devPlanet developer planet to parse code.
     * @param config    config to add executors.
     * @return true - code is fine, false - troubles while parsing.
     */
    public boolean parseExecutors(DevPlanet devPlanet, CodeStorage config, Set<Location> executorsLocations) {
        boolean isCodeFine = true;
        World world = devPlanet.getWorld();

        List<Block> unknownBlocks = new ArrayList<>();
        boolean notDependsOnHeight = devPlanet.getDevPlatformer().notDependsOnHeight();
        long argumentsSize = 0;

        // For coding executors
        for (Location executorLocation : executorsLocations) {

            int executorX = executorLocation.getBlockX();
            int y = executorLocation.getBlockY();
            int z = executorLocation.getBlockZ();

            Block executorBlock = world.getBlockAt(executorX, y, z);
            config.removeExecutorBlock(executorBlock, notDependsOnHeight);
            if (executorBlock.isEmpty()) {
                continue;
            }

            ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(executorBlock.getType());
            Executor executor = Executors.getInstance().getByBlock(executorBlock);
            boolean debug = executorBlock.getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WALL_TORCH;

            /*
             * Checking executor. If executor is not detected,
             * then we don't need to save actions inside,
             * because we can't execute them without executor.
             */
            if (executorCategory == null || executor == null) {
                if ((executorCategory == null && isSignEmpty(executorBlock, (byte) 2)) || (executor == null && isSignEmpty(executorBlock, (byte) 3))) {
                    unknownBlocks.add(executorBlock);
                }
                continue;
            }
            config.saveExecutorBlock(executorBlock, notDependsOnHeight, executorCategory, executor, debug);

            // For coding actions
            List<String> multiActions = new ArrayList<>();
            for (int x = executorX + 2; x <= executorX + 92; x = x + 2) {

                Block actionBlock = world.getBlockAt(x, y, z);
                ActionCategory actionCategory = ActionCategory.getByMaterial(actionBlock.getType());
                ActionType actionType = ActionType.getType(actionBlock);
                Target actionTarget = Target.getBySign(actionBlock.getLocation());
                Block containerBlock = actionBlock.getRelative(BlockFace.UP);

                if (actionCategory != null && actionCategory != ActionCategory.ELSE_CONDITION && actionCategory.isMultiAction()) {
                    multiActions.add((actionCategory.isCondition() ? "condition_block_" : "multi_action_") + getBlockNumber(actionBlock));
                    if (actionType == null) {
                        continue;
                    }
                }

                if (actionCategory == null || actionType == null) {
                    if (actionBlock.getType() != Material.END_STONE && ((actionCategory == null && isSignEmpty(actionBlock, (byte) 2)) || (actionType == null && isSignEmpty(actionBlock, (byte) 3)))) {
                        unknownBlocks.add(actionBlock);
                    }
                    /*
                     * Checking condition's piston. If it is beginning piston,
                     * we already added condition in conditions list.
                     */
                    if (world.getBlockAt(x + 1, y, z).getType() == Material.PISTON) {
                        if (!multiActions.isEmpty()) {
                            String last = multiActions.getLast();
                            multiActions.remove(last);
                            if (world.getBlockAt(x + 2, y, z).getType() == Material.END_STONE) {
                                multiActions.add(last + ".else");
                                x = x + 2;
                            }
                        } else {
                            sendPlanetCompileErrorMessage(devPlanet.getPlanet(), world.getBlockAt(x + 1, y, z), getLocaleMessage("coding-error.bad-piston"));
                            isCodeFine = false;
                            continue;
                        }
                    }
                    continue;
                }
                config.saveActionBlock(executorBlock, notDependsOnHeight, multiActions, actionBlock, actionCategory, actionType, actionTarget);
                /*
                 * Checking items in container and saving
                 * them as arguments for action.
                 */
                if (!(containerBlock.getState() instanceof InventoryHolder container)) continue;
                byte slot = 0;
                ItemStack[] content = container.getInventory().getContents();
                if (actionType.getCategory() == ActionCategory.SELECTION_ACTION ||
                        actionType == ActionType.REPEAT_WHILE || actionType == ActionType.REPEAT_WHILE_NOT) {
                    actionType = ActionType.getTypeFromSelectionAction(actionBlock);
                    if (actionType == null) continue;
                }
                if (actionType.getArgumentsSlots() == null) continue;
                for (ArgumentSlot argSlot : actionType.getArgumentsSlots()) {
                    ItemStack item = content[slot];
                    /*
                     * If argument slot is list, then we need
                     * handle and save every item into list.
                     */
                    if (argSlot.isList()) {
                        config.saveArguments(executorBlock, notDependsOnHeight, multiActions, actionBlock, argSlot.getPath(), null, ValueType.LIST);
                        for (byte i = 1; i < argSlot.getListSize() + 1; i++) {
                            if (slot < content.length) {
                                item = content[slot];
                                if (item == null) {
                                    if (argSlot.acceptEmptyItems() || saveEmptyArguments) {
                                        item = new ItemStack(Material.AIR);
                                        Object value = parseItemValue(item);
                                        argumentsSize += value.toString().getBytes(StandardCharsets.UTF_8).length;
                                        if (argumentsSize > maxScriptSize) {
                                            onArgumentsTooBig(devPlanet, actionBlock, argumentsSize);
                                            return false;
                                        }
                                        config.saveArguments(executorBlock, notDependsOnHeight, multiActions, actionBlock,
                                                argSlot.getPath() + ".value." + i,
                                                value, parseItemType(item));
                                    }
                                } else {
                                    Object value = parseItemValue(item);
                                    argumentsSize += value.toString().getBytes(StandardCharsets.UTF_8).length;
                                    if (argumentsSize > maxScriptSize) {
                                        onArgumentsTooBig(devPlanet, actionBlock, argumentsSize);
                                        return false;
                                    }
                                    config.saveArguments(executorBlock, notDependsOnHeight, multiActions, actionBlock,
                                            argSlot.getPath() + ".value." + i,
                                            value, parseItemType(item));
                                }
                            }
                            slot++;
                        }
                    } else {
                        if (item != null || saveEmptyArguments) {
                            Object value = parseItemValue(item);
                            argumentsSize += value.toString().getBytes(StandardCharsets.UTF_8).length;
                            if (argumentsSize > maxScriptSize) {
                                onArgumentsTooBig(devPlanet, actionBlock, argumentsSize);
                                return false;
                            }
                            config.saveArguments(executorBlock, notDependsOnHeight, multiActions, actionBlock,
                                    argSlot.getPath(), value, parseItemType(item));
                        }
                        slot++;
                    }
                }
            }
        }

        if (!unknownBlocks.isEmpty()) {
            /*
             * Warns world developer about old or unknown
             * coding blocks that were found while parsing.
             */
            sendPlanetCompileErrorMessage(devPlanet.getPlanet(), unknownBlocks);
            return false;
        }
        return isCodeFine;
    }

    private boolean isSignEmpty(Block block, byte line) {
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().name().contains("SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (line > 0 && line < sign.lines().size()) {
                return (!sign.getLine(line - 1).isEmpty());
            }
            return false;
        }
        return false;
    }

    private void onArgumentsTooBig(DevPlanet devPlanet, Block block, long argsSize) {
        sendPlanetCompileErrorMessage(devPlanet.getPlanet(), block, getLocaleMessage("world.script-size-limit")
                .replace("%amount%", FileUtils.byteCountToDisplaySize(argsSize))
                .replace("%limit%", String.valueOf(maxScriptSize / 1024 / 1024)));
    }

    public int getBlockNumber(Block block) {
        return (block.getX() - 2) / 2;
    }

}
