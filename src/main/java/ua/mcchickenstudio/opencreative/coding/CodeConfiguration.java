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

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;

/**
 * <h1>CodeConfiguration</h1>
 * This class represents a code configuration, that
 * has methods to save executor and action blocks.
 */
public class CodeConfiguration extends YamlConfiguration implements CodeStorage {

    @Override
    public void loadCode(@NotNull File file) {
        try {
            load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException ex) {
            OpenCreative.getPlugin().getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
    }

    @Override
    public void removeExecutorBlock(@NotNull Block block, boolean notDependsOnHeight) {
        String path = "code.blocks." + getExecutorKey(block);
        set(path, null);
        set("code.blocks.exec_block_" + (notDependsOnHeight ? block.getZ() : block.getY()) + "_" + block.getX(), null);
    }

    @Override
    public void saveExecutorBlock(@NotNull Block block, boolean notDependsOnHeight,
                                  @NotNull ExecutorCategory category,
                                  @NotNull Executor executor, boolean debug) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String path = "code.blocks." + getExecutorKey(block);
        set(path + ".category", category.name());
        set(path + ".type", executor.getID().toUpperCase());

        if (debug) {
            set(path + ".debug", true);
        }
        String thirdSignLine = getSignLine(block.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
        if (category == ExecutorCategory.FUNCTION ||
                category == ExecutorCategory.METHOD ||
                category == ExecutorCategory.STRUCTURE) {

            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".name", thirdSignLine);
            }
        }

        set(path + ".location.x", x);
        set(path + ".location.y", y);
        set(path + ".location.z", z);
    }

    @Override
    public void saveExecutorBlock(@NotNull Block block, boolean notDependsOnHeight, @NotNull ExecutorCategory category, @NotNull Executor executor) {
        saveExecutorBlock(block, notDependsOnHeight, category, executor, false);
    }

    @Override
    public void saveActionBlock(@NotNull Block executorBlock, boolean notDependsOnHeight,
                                @NotNull List<String> multiActions, @NotNull Block actionBlock,
                                @NotNull ActionCategory category, @NotNull ActionType type,
                                @NotNull Target target) {
        String path = getActionBlockPath(executorBlock, notDependsOnHeight, actionBlock, multiActions);

        set(path + ".category", category.name());
        set(path + ".type", type.name());

        if (type == ActionType.LAUNCH_FUNCTION || type == ActionType.LAUNCH_METHOD) {
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
            if (thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".name", thirdSignLine);
            }
        }

        if (category == ActionCategory.SELECTION_ACTION) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 1);
            String secondSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 2);
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
            if (secondSignLine != null && !secondSignLine.isEmpty() && thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".condition.category", secondSignLine.toUpperCase());
                set(path + ".condition.type", thirdSignLine.toUpperCase());
                if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                    set(path + ".condition.opposed", true);
                }
            } else if (secondSignLine != null && !secondSignLine.isEmpty()) {
                set(path + ".target", secondSignLine.toUpperCase());
            }
        } else if (type == ActionType.REPEAT_WHILE || type == ActionType.REPEAT_WHILE_NOT) {
            String secondSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 2);
            String thirdSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
            if (secondSignLine != null && !secondSignLine.isEmpty() && thirdSignLine != null && !thirdSignLine.isEmpty()) {
                set(path + ".condition.category", secondSignLine.toUpperCase());
                set(path + ".condition.type", thirdSignLine.toUpperCase());
                if (target != Target.DEFAULT) {
                    set(path + ".target", target.name());
                }
            }
        } else if (target != Target.DEFAULT) {
            set(path + ".target", target.name());
        }

        if (category.isCondition()) {
            String firstSignLine = getSignLine(actionBlock.getRelative(BlockFace.SOUTH).getLocation(), (byte) 1);
            if (firstSignLine != null && firstSignLine.equalsIgnoreCase("not")) {
                set(path + ".opposed", true);
            }
        }

        set(path + ".location.x", actionBlock.getX());
        createSection(path + ".arguments");
    }

    @Override
    public void saveArguments(@NotNull Block executorBlock, boolean notDependsOnHeight,
                              @NotNull List<String> multiActions, @NotNull Block actionBlock,
                              @NotNull String argument, @Nullable Object value, @NotNull ValueType type) {
        String path = getActionBlockPath(executorBlock, notDependsOnHeight, actionBlock, multiActions);
        set(path + ".arguments." + argument + ".type", type.name());
        set(path + ".arguments." + argument + ".value", value);
    }

    @Override
    public void saveToFile(@NotNull File file) throws IOException {
        save(file);
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return getConfigurationSection(path);
    }

    private String getActionBlockPath(Block executorBlock, boolean notDependsOnHeight, Block actionBlock, List<String> multiActions) {
        StringBuilder conditionsPath = new StringBuilder();
        for (String condition : multiActions) {
            conditionsPath
                    .append(condition.endsWith(".else") ? condition.replace(".else", "") : condition)
                    .append(condition.endsWith(".else") ? ".else." : ".actions.");
        }
        StringBuilder builder = new StringBuilder("code.blocks.");
        builder.append(getExecutorKey(executorBlock));
        builder.append(".actions.");
        builder.append(conditionsPath);
        ActionCategory category = ActionCategory.getByMaterial(actionBlock.getType());
        if (category != null && category.isMultiAction()) {
            builder.delete(builder.length() - 9, builder.length());
        } else {
            builder.append("action_block").append(getBlockNumber(actionBlock));
        }
        return builder.toString();
    }

    public int getBlockNumber(Block block) {
        return (block.getX() - 2) / 2;
    }

    /**
     * Builds stable unique key for executor block.
     * We include all 3 coordinates to avoid key collisions on vertical/legacy platforms
     * where many executors share same X and Y and differ only by Z.
     */
    private @NotNull String getExecutorKey(@NotNull Block block) {
        return "exec_block_" + block.getY() + "_" + block.getX() + "_" + block.getZ();
    }

}
