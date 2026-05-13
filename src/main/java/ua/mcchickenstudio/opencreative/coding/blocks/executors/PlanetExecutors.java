/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 */

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.events.InternalTriggerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.EntitySpawnedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.state.EntityAirChangedExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Structure;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.NameableExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction.PlayerDestroyBlockExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction.PlayerPlaceBlockExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.movement.PlayerWalkExecutor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.blocks.WorldBlockFluidChangedExecutor;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.events.planet.PlanetEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>PlanetExecutors</h1>
 * This class represents Executors in every planet code script.
 */
public class PlanetExecutors {

    protected final Planet planet;
    private final List<Executor> executorsList = new ArrayList<>();

    public PlanetExecutors(Planet planet) {
        this.planet = planet;
    }

    /**
     * Initializes all structures by executing their internal actions.
     * This ensures fields are populated before events try to read them.
     */
    /**
     * Initializes all structures by executing their internal actions.
     * This ensures fields are populated before events try to read them.
     */
    public void initializeStructures() {
        if (planet.getMode() != Planet.Mode.PLAYING) return;

        InternalTriggerEvent initEvent = new InternalTriggerEvent(planet);

        List<Structure> structures = getStructuresList();
        for (Structure structure : structures) {
            structure.run(initEvent);
        }

        if (!structures.isEmpty()) {
            sendCodingDebugLog(planet, "§8Initialized §b" + structures.size() + " §8structures.");
        }
    }

    /**
     * Finds executor for world event and activates it, if found.
     */
    public static void activate(@NotNull WorldEvent event) {
        Planet planet = event.getPlanet();
        if (!OpenCreative.getSettings().getCodingSettings().isEnabled()) return;
        if (planet == null) return;
        if (planet.getMode() != Planet.Mode.PLAYING) return;
        PlanetExecutors executors = planet.getTerritory().getScript().getExecutors();
        for (Executor executor : executors.executorsList) {
            if (executor instanceof EventAwaiter awaiter) {
                if (awaiter.getEventClass() == event.getClass()) {
                    activate(executor, event);
                }
            }
        }
    }

    public static void activate(@NotNull Executor executor, @NotNull WorldEvent event) {
        Planet planet = executor.getPlanet();
        if (planet.getMode() != Planet.Mode.PLAYING) return;
        if (canRunExecutor(planet, executor)) {
            executor.run(event);
        }
    }

    public static boolean canRunExecutor(@NotNull Planet planet, @NotNull Executor executor) {
        if (executor instanceof PlayerWalkExecutor || executor instanceof EntityAirChangedExecutor
                || executor instanceof WorldBlockFluidChangedExecutor || executor instanceof PlayerDestroyBlockExecutor
                || executor instanceof PlayerPlaceBlockExecutor || executor instanceof EntitySpawnedExecutor) {
            if (executor.getLastCalls() >= planet.getLimits().getCodeOperationsLimit()) {
                planet.getTerritory().getScript().getExecutors().stopCode("operations limit");
                sendPlanetCodeCriticalErrorMessage(planet, executor, getLocaleMessage("coding-error.operations-limit", false)
                        .replace("%limit%", String.valueOf(planet.getLimits().getCodeOperationsLimit())));
                return false;
            }
            executor.increaseCall();
            new BukkitRunnable() {
                @Override
                public void run() {
                    executor.decreaseCall();
                }
            }.runTaskLater(OpenCreative.getPlugin(), 35L);
            return true;
        }
        int limit = planet.getLimits().getCodeOperationsLimit();
        int depth = StackWalker.getInstance().walk(stream ->
                (int) stream.limit(limit + 1).count()
        );
        if (depth > limit) {
            planet.getTerritory().getScript().getExecutors().stopCode("operations limit");
            sendPlanetCodeCriticalErrorMessage(planet, executor, getLocaleMessage("coding-error.operations-limit", false)
                    .replace("%limit%", String.valueOf(limit)));
            return false;
        }
        return true;
    }

    public void clear() {
        executorsList.clear();
    }

    public void load(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section != null) {
            OpenCreative.getPlugin().getLogger().info("Loading code in planet " + planet.getId() + "...");
            long time = System.currentTimeMillis();
            List<Executor> executors = new ArrayList<>();
            Set<String> keys = section.getKeys(false);
            String path;
            for (String key : keys) {
                path = "code.blocks." + key;
                if (config.getString(path + ".type") != null) {
                    Executor executor = createExecutor(config, path);
                    if (executor != null) {
                        executors.add(executor);
                    }
                }
            }
            clear();
            executorsList.addAll(executors);

            // --- AUTO-INITIALIZE STRUCTURES HERE ---
            if (planet.getMode() == Planet.Mode.PLAYING) {
                initializeStructures();
            }
            // ----------------------------------------

            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loaded-code", false)
                    .replace("%time%", String.valueOf(Math.floor((System.currentTimeMillis() - time) / 10.0) / 100.0)));
            OpenCreative.getPlugin().getLogger().info("Loaded code in planet " + planet.getId() + " in " + (System.currentTimeMillis() - time) + " ms with " + executors.size() + " executors!");
        } else {
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loaded-code", false)
                    .replace("%time%", "0"));
            OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " has no code to load.");
        }
    }

    public @NotNull List<Executor> getExecutorsList() {
        return executorsList;
    }

    public @NotNull List<Action> getInsideActionsList(@NotNull Action action) {
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        if (action instanceof Condition condition) {
            for (Action inside : condition.getActions()) {
                actions.addAll(getInsideActionsList(inside));
            }
            for (Action inside : condition.getElseActions()) {
                actions.addAll(getInsideActionsList(inside));
            }
        } else if (action instanceof MultiAction multiAction) {
            for (Action inside : multiAction.getActions()) {
                actions.addAll(getInsideActionsList(inside));
            }
        }
        return actions;
    }

    public @NotNull List<Structure> getStructuresList() {
        List<Structure> structures = new ArrayList<>();
        for (Executor executor : executorsList) {
            if (executor instanceof Structure structure) {
                structures.add(structure);
            }
        }
        return structures;
    }

    public @NotNull List<Function> getFunctionsList() {
        List<Function> functions = new ArrayList<>();
        for (Executor executor : executorsList) {
            if (executor instanceof Function function) {
                functions.add(function);
            }
        }
        return functions;
    }

    public @NotNull List<Method> getMethodsList() {
        List<Method> methods = new ArrayList<>();
        for (Executor executor : executorsList) {
            if (executor instanceof Method method) {
                methods.add(method);
            }
        }
        return methods;
    }

    private int[] getCoords(YamlConfiguration config, String path) {
        int[] coords = new int[3];
        coords[0] = config.getInt(path + ".location.x");
        coords[1] = config.getInt(path + ".location.y");
        coords[2] = config.getInt(path + ".location.z");
        return coords;
    }

    private Executor createExecutor(@NotNull YamlConfiguration config, @NotNull String path) {
        try {
            int[] coords = getCoords(config, path);
            Executor executor = Executors.getInstance().getById(config.getString(path + ".type", "").toLowerCase());
            if (executor == null) return null;

            executor = executor.getClass().getDeclaredConstructor().newInstance();
            if (executor instanceof NameableExecutor nameable) {
                String name = config.getString(path + ".name");

                if (name != null && !name.isEmpty()) {
                    nameable.setCallName(name);
                    nameable.init(planet, coords[0], coords[1], coords[2]);
                } else {
                    return null;
                }
            } else {
                executor.init(planet, coords[0], coords[1], coords[2]);
            }

            List<Action> allActionsList = createActionList(executor, path + ".actions", config);
            if (!allActionsList.isEmpty()) {
                executor.setActions(allActionsList);
            }

            boolean debug = config.getBoolean(path + ".debug", false);
            if (debug) {
                executor.setDebug(true);
            }

            return executor;
        } catch (Exception ignored) {
            return null;
        }
    }

    private @NotNull List<Action> createActionList(@NotNull Executor executor,
                                                   @NotNull String path,
                                                   @NotNull YamlConfiguration config) {
        List<Action> actionList = new ArrayList<>();
        ConfigurationSection actions = config.getConfigurationSection(path);
        if (actions != null) {
            Set<String> actionsBlocks = actions.getKeys(false);
            for (String actionBlock : actionsBlocks) {
                String actionPath = path + "." + actionBlock;
                Action action = createAction(executor, actionPath, config);
                if (action != null) {
                    actionList.add(action);
                }
            }
        }
        return actionList;
    }

    private @Nullable Action createAction(@NotNull Executor executor,
                                          @NotNull String path,
                                          @NotNull YamlConfiguration config) {

        String type = config.getString(path + ".type");
        if (type == null) return null;

        try {
            ActionType actionType = ActionType.valueOf(type);
            Arguments args = new Arguments(executor.getPlanet());
            Target target = Target.DEFAULT;
            String targetString = config.getString(path + ".target");
            if (targetString != null && !targetString.isEmpty()) {
                target = Target.valueOf(targetString);
            }
            ConfigurationSection section = config.getConfigurationSection(path + ".arguments");
            if (section != null) {
                args.load(section);
            }
            if (actionType == ActionType.LAUNCH_FUNCTION || actionType == ActionType.LAUNCH_METHOD) {
                if (config.getString(path + ".name") != null) {
                    args.setArgumentValue("name", ValueType.TEXT, config.getString(path + ".name", " "));
                }
            } else if (actionType == ActionType.SELECTION_SET || actionType == ActionType.SELECTION_ADD || actionType == ActionType.SELECTION_REMOVE) {
                if (config.getConfigurationSection(path + ".condition") != null) {
                    boolean isOpposed = config.getBoolean(path + ".condition.opposed", false);
                    ActionCategory conditionCategory = ActionCategory.valueOf(config.getString(path + ".condition.category"));
                    ActionType conditionType = ActionType.valueOf(config.getString(path + ".condition.type"));
                    return actionType.getActionClass().getConstructor(Executor.class, int.class, Arguments.class, ActionCategory.class, ActionType.class, boolean.class).newInstance(executor, config.getInt(path + ".location.x"), args, conditionCategory, conditionType, isOpposed);
                } else if (config.getString(path + ".target") != null) {
                    if (targetString != null && !targetString.isEmpty()) {
                        target = Target.valueOf(targetString);
                    }
                    return actionType.getActionClass().getConstructor(Executor.class, int.class, Arguments.class, Target.class).newInstance(executor, config.getInt(path + ".location.x"), args, target);
                }
                if (config.getString(path + ".condition.type") != null) {
                    args.setArgumentValue("name", ValueType.TEXT, config.getString(path + ".name", ""));
                }
            }
            if (actionType.getCategory().isMultiAction()) {
                if (actionType.getCategory().isCondition()) {
                    boolean isOpposed = config.getBoolean(path + ".opposed", false);
                    return actionType.getActionClass()
                            .getConstructor(Executor.class, Target.class, int.class, Arguments.class, List.class, List.class, boolean.class)
                            .newInstance(executor, target, config.getInt(path + ".location.x"),
                                    args, createActionList(executor, path + ".actions", config),
                                    createActionList(executor, path + ".else", config), isOpposed);
                } else if (actionType == ActionType.REPEAT_WHILE || actionType == ActionType.REPEAT_WHILE_NOT) {
                    if (config.getConfigurationSection(path + ".condition") != null) {
                        ActionType conditionType = ActionType.valueOf(config.getString(path + ".condition.type"));
                        return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,
                                Arguments.class, List.class, ActionType.class).newInstance(executor,
                                target, config.getInt(path + ".location.x"),
                                args, createActionList(executor, path + ".actions", config), conditionType);
                    }
                } else {
                    return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class, Arguments.class, List.class)
                            .newInstance(executor, target, config.getInt(path + ".location.x"),
                                    args, createActionList(executor, path + ".actions", config));
                }
            }
            return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class, Arguments.class).newInstance(executor, target, config.getInt(path + ".location.x"), args);
        } catch (Exception error) {
            sendDebugError("Can't create an action", error);
            return null;
        }
    }

    public void stopCode(@NotNull String reason) {
        if (planet.getMode() == Planet.Mode.BUILD) return;
        OpenCreative.getPlugin().getLogger().info("Planet code has been stopped in " + planet.getId() + " because of " + reason + ".");
        planet.setMode(Planet.Mode.BUILD, true);
    }
}