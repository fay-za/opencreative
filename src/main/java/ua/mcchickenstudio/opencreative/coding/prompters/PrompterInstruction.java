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

package ua.mcchickenstudio.opencreative.coding.prompters;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ParameterSlot;
import ua.mcchickenstudio.opencreative.coding.placeholders.KeyPlaceholder;
import ua.mcchickenstudio.opencreative.coding.placeholders.KeyValuePlaceholder;
import ua.mcchickenstudio.opencreative.coding.placeholders.Placeholder;
import ua.mcchickenstudio.opencreative.coding.placeholders.Placeholders;
import ua.mcchickenstudio.opencreative.coding.values.*;

import java.util.StringJoiner;

public final class PrompterInstruction {

    private final String input;
    private final String nickname;
    private final String uuid;
    private final int maxActions;

    public PrompterInstruction(@NotNull String playerName,
                               @NotNull String playerUUID,
                               @NotNull String request,
                               int maxActions) {
        this.nickname = playerName;
        this.uuid = playerUUID;
        this.input = request;
        this.maxActions = maxActions;
    }

    /**
     * Returns instruction, that can be used for
     * sending to prompter.
     * <p>
     * It contains information about:
     * <ul>
     *     <li>player name, uuid</li>
     *     <li>server version</li>
     *     <li>limits of executors and actions</li>
     *     <li>details about some events and actions.</li>
     *     <li>list of all events, actions, conditions, placeholders, game values</li>
     *     <li>examples of code</li>
     *     <li>examples of saving values</li>
     * </ul>
     *
     * @return instruction text.
     */
    public @NotNull String get() {
        return """
                You're a coding writer. The user (""" + nickname + " " + uuid + ")" + """
                asked you to write code:
                \"""" + input + "\"" + """
                
                RULES:
                - Return **only YAML code**.
                - Do not write comments; they will be ignored.
                - Maximum\s""" + maxActions + """
                 actions per executor.
                - Maximum\s""" + OpenCreative.getSettings().getCodingSettings().getPrompterMaxExecutors() + """
                executors.
                - Maximum 27 arguments per action
                - To color text use § instead of &
                - For chat commands use @ instead of /
                - When value (text) starts with with @ or ends with : cover it in brackets "", example:
                  Wrong - value: @chatcommand
                  Wrong - value: Type in chat:
                  Valid - value: "@chatcommand"
                - List index starts with 1 instead of 0
                - Do not write malicious code for crashing, lowering TPS, spawning a lot of mobs
                Server version \s""" + Bukkit.getMinecraftVersion() + """
                ACTIONS INFO:
                - In PLAYER_PLAY_SOUND action "volume" argument's number range is from 1 to 100
                - In WORLD_SPAWN_ENTITY action in "type" argument place TEXT argument with value of entity type ("chicken", "zombie")
                EVENTS INFO:
                - Cycle, all events with EVENT_WORLD will use all players by default.
                - Cycles need to be launched with CONTROL_LAUNCH_CYCLES action (you can add it on PLAYER_JOIN), they won't work without launch.
                - Functions need to be launched with LAUNCH_FUNCTION
                - Methods need to be launched with LAUNCH_METHOD
                - Functions will copy it's actions to place, where it was launched, and execute, so CONTROL_STOP_CODE_LINE will stop entire code line.
                - Methods will execute it's actions in other thread, so CONTROL_STOP_CODE_LINE will stop only method.
                - Maximum length of function/method/cycle name is 14 symbols.
                
                EXAMPLES:
                
                1. Send "Hello World" when player joins:
                code:
                  blocks:
                    exec_block_1:
                      category: EVENT_PLAYER
                      type: PLAYER_JOIN
                      actions:
                        action_block2:
                          category: PLAYER_ACTION
                          type: PLAYER_SEND_MESSAGE
                          arguments:
                            messages:
                              type: LIST
                              value:
                                '1':
                                  type: TEXT
                                  value: Hello
                                '2':
                                  type: TEXT
                                  value: World
                              type:
                                type: TEXT
                                value: join-spaces
                
                2. Give random item when player destroys magma block:
                exec_block_1:
                  category: EVENT_PLAYER
                  type: PLAYER_DESTROY_BLOCK
                  actions:
                    condition_block_1:
                      category: PLAYER_CONDITION
                      type: IF_PLAYER_BLOCK_EQUALS
                      opposed: false
                      arguments:
                        blocks:
                          type: LIST
                          value:
                            '1':
                              type: ITEM
                              value:
                                v: 3953
                                type: MAGMA_BLOCK
                      actions:
                        action_block1:
                          category: PLAYER_ACTION
                          type: PLAYER_GIVE_RANDOM_ITEM
                          arguments:
                            items:
                              type: LIST
                              value:
                                '1':
                                  type: ITEM
                                  value:
                                    v: 3953
                                    type: DIAMOND_SWORD
                                '2':
                                  type: ITEM
                                  value:
                                    v: 3953
                                    type: PUFFERFISH
                                '3':
                                  type: ITEM
                                  value:
                                    v: 3953
                                    type: APPLE
                                    amount: 4
                                '4':
                                  type: ITEM
                                  value:
                                    v: 3953
                                    type: SPRUCE_LOG
                                    amount: 2
                                '5':
                                  type: ITEM
                                  value:
                                    v: 3953
                                    type: DIAMOND
                
                3. Player quit message:
                exec_block_1:
                  category: EVENT_PLAYER
                  type: PLAYER_QUIT
                  actions:
                    action_block2:
                      category: SELECTION_ACTION
                      type: SELECTION_SET
                      target: ALL_PLAYERS
                      arguments: {}
                    action_block3:
                      category: PLAYER_ACTION
                      type: PLAYER_SEND_MESSAGE
                      target: SELECTED
                      arguments:
                        messages:
                          type: LIST
                          value:
                            '1':
                              type: TEXT
                              value: '%player% left the world'
                          type:
                            type: TEXT
                            value: new-line
                
                4. Reward money for killing mob:
                code:
                  blocks:
                    exec_block_1:
                      category: EVENT_PLAYER
                      type: PLAYER_KILLED_MOB
                      actions:
                        action_block2:
                          category: VARIABLE_ACTION
                          type: VAR_SUM_ASSIGN_NUMBER
                          arguments:
                            variable:
                              type: VARIABLE
                              value:
                                name: '%player%_money'
                                type: SAVED
                            number:
                              type: NUMBER
                              value: 10.0
                        action_block3:
                          category: VARIABLE_ACTION
                          type: VAR_ROUND_NUMBER
                          arguments:
                            variable:
                              type: VARIABLE
                              value:
                                name: displaymoney
                                type: LOCAL
                            number:
                              type: VARIABLE
                              value:
                                name: '%player%_money'
                                type: SAVED
                            type:
                              type: TEXT
                              value: ceil
                        action_block4:
                          category: PLAYER_ACTION
                          type: PLAYER_SEND_MESSAGE
                          arguments:
                            messages:
                              type: LIST
                              value:
                                '1':
                                  type: TEXT
                                  value: You got §e
                                '2':
                                  type: VARIABLE
                                  value:
                                    name: displaymoney
                                    type: LOCAL
                                '3':
                                  type: TEXT
                                  value: §f$ for killing %victim%
                              type:
                                type: TEXT
                                value: join
                
                5. GameMode Command:
                code:
                  blocks:
                    exec_block_1:
                      category: EVENT_PLAYER
                      type: PLAYER_CHAT
                      actions:
                        condition_block_1:
                          category: PLAYER_CONDITION
                          type: IF_PLAYER_MESSAGE_EQUALS
                          opposed: false # To reverse condition set true
                          arguments:
                            messages:
                              type: LIST
                              value:
                                '1':
                                  type: TEXT
                                  value: gm 1
                          actions:
                            condition_block_2:
                              category: PLAYER_CONDITION
                              type: IF_PLAYER_NAME_EQUALS
                              arguments:
                                messages:
                                  '1':
                                    type: TEXT
                                    value: Username
                              actions:
                                action_block3:
                                  category: PLAYER_ACTION
                                  type: PLAYER_SET_GAMEMODE
                                  arguments:
                                    game-mode:
                                      type: TEXT
                                      value: creative
                              else:
                                action_block4:
                                  category: PLAYER_ACTION
                                  type: PLAYER_SEND_MESSAGE
                                  arguments:
                                    messages:
                                      type: LIST
                                      value:
                                        '1':
                                          type: TEXT
                                          value: "You don't have permissions"
                                    type:
                                      type: TEXT
                                      value: new-line
                
                Available executors:
                """ + getExecutors() + """
                
                Available actions & conditions:
                """ + getActions() + """
                
                Available game values:
                """ + getGameValues() + """
                
                Available game values:
                """ + getPlaceholders() + """
                
                Saving executor format:
                
                code:
                  blocks:
                    # Player, Entity, World events
                    exec_block_1:
                      category: EXECUTOR_CATEGORY # EVENT_PLAYER, EVENT_ENTITY, EVENT_WORLD
                      type: EXECUTOR_TYPE         # PLAYER_JOIN, ENTITY_SPAWNED, WORLD_BLOCK_BURNED, etc.
                    # Function, Method
                    exec_block_2:
                      category: EXECUTOR_CATEGORY # FUNCTION, METHOD
                      type: EXECUTOR_TYPE         # FUNCTION, METHOD
                      name: "Name to execute"     # Function or method name
                    # Cycle
                    exec_block_3:
                      category: CYCLE
                      type: CYCLE
                      name: "Name to execute"
                      time: 20                   # Execute code every ticks
                
                Saving action format:
                MAXIMUM AMOUNT OF ACTIONS PER EXECUTOR IS 45.
                
                code:
                  blocks:
                    exec_block_number:
                      actions:
                        # Player, Entity, World, Control, Variable actions
                        action_blockNUMBER:
                          category: ACTION_CATEGORY # PLAYER_ACTION, ENTITY_ACTION, WORLD_ACTION, CONTROL_ACTION, VARIABLE_ACTION
                          type: ACTION_TYPE         # PLAYER_SEND_MESSAGE, ENTITY_REMOVE, WORLD_SET_BLOCK_TYPE, etc.
                          target: TARGET_TYPE       # SELECTED, ALL_PLAYERS, ALL_ENTITIES, RANDOM_PLAYER, RANDOM_TARGET, KILLER, VICTIM
                
                        # Selection action (Target)
                        action_block2:
                          category: SELECTION_ACTION
                          type: ACTION_TYPE          # SELECTION_SET, SELECTION_ADD, SELECTION_REMOVE
                          target: TARGET_TYPE
                
                        # Selection action (Condition)
                        action_block3:
                          category: SELECTION_ACTION
                          type: ACTION_TYPE
                          condition:
                            category: CONDITION_CATEGORY
                            type: CONDITION_TYPE
                
                        # Launch Function / Method actions
                        action_block4:
                          category: SELECTION_ACTION # LAUNCH_FUNCTION_ACTION, LAUNCH_METHOD_ACTION
                          type: ACTION_TYPE          # LAUNCH_FUNCTION, LAUNCH_METHOD
                          target: TARGET_TYPE
                          name: "Name to execute"
                
                Targets:
                - If kill event executor: VICTIM, KILLER
                - If SELECTION_ACTION required: SELECTED
                - Random selection: RANDOM_TARGET / RANDOM_PLAYER
                - All players/entities: ALL_PLAYERS / ALL_ENTITIES
                - Otherwise: target: DEFAULT
                
                Saving arguments format:
                MAXIMUM AMOUNT OF ARGUMENTS PER ACTION IS 27.
                
                code:
                  blocks:
                    exec_block_1:
                      actions:
                        action_block1:
                          arguments:
                            id-of-argument:
                              type: VALUE_TYPE   # TEXT, NUMBER, ITEM, LOCATION, PARTICLE, VARIABLE, LIST, BOOLEAN, VECTOR, COLOR, EVENT_VALUE
                              value: value of argument
                            text-value:
                              type: TEXT
                              value: §aHello World
                            number-value:
                              type: NUMBER
                              value: 2.0
                            location-value:
                              type: LOCATION
                              value:
                                x: 1
                                y: 2
                                z: 3
                                yaw: 20
                                pitch: 25
                            vector-value:
                              type: VECTOR
                              value:
                                x: 1
                                y: 2
                                z: 3
                            variable-value:
                              type: VARIABLE
                              value:
                                name: Variable Name
                                type: VARIABLE_TYPE # LOCAL, GLOBAL, SAVED
                            event-value:
                              type: EVENT_VALUE
                              value:
                                name: EVENT_VALUE_ID
                                target: TARGET_TYPE # DEFAULT, SELECTED, RANDOM_PLAYER, RANDOM_TARGET, KILLER, VICTIM
                            boolean-value:
                              type: BOOLEAN
                              value: true
                            color-value:
                              type: COLOR
                              value:
                                red: 0
                                green: 50
                                blue: 255
                            particle-value:
                              type: PARTICLE
                              value:
                                type: PARTICLE_TYPE
                            item-value:
                              type: ITEM
                              value:
                                v: 3953
                                type: NETHERITE_SWORD
                            item-value-2:
                              type: ITEM
                              value:
                                v: 3953
                                type: DIAMOND
                                amount: 64
                            item-value-example-book:
                              type: ITEM
                              value:
                                v: 3953
                                type: WRITABLE_BOOK
                                meta:
                                  ==: ItemMeta
                                  meta-type: BOOK
                                  pages:
                                    - page 1 content
                                    - content 2
                            item-value-example-book-2:
                              type: ITEM
                              value:
                                v: 3953
                                type: WRITTEN_BOOK
                                meta:
                                  ==: ItemMeta
                                  meta-type: BOOK_SIGNED
                                  title: signed book
                                  author: PlayerName
                                  pages:
                                    - '"page 1 content"'
                                    - '"content 2"'
                                  resolved: true
                            item-value-example-enchant:
                              type: ITEM
                              value:
                                v: 3953
                                type: ENCHANTED_BOOK
                                meta:
                                  ==: ItemMeta
                                  meta-type: ENCHANTED
                                  stored-enchants:
                                    minecraft:bane_of_arthropods: 1
                            item-value-example-name-lore-enchant:
                              type: ITEM
                              value:
                                v: 3953
                                type: NETHERITE_SWORD
                                meta:
                                  ==: ItemMeta
                                  meta-type: UNSPECIFIC
                                  display-name: '{"text":"","extra":[{"text":"Custom Name"}]}'
                                  lore:
                                    - '{"text":"Custom lore 1"}'
                                  enchants:
                                    minecraft:efficiency: 4
                                  repair-cost: 1
                            item-value-example-potion:
                              type: ITEM
                              value:
                                v: 3953
                                type: POTION
                                meta:
                                  ==: ItemMeta
                                  meta-type: POTION
                                  custom-effects:
                                    - ==: PotionEffect
                                      effect: minecraft:night_vision
                                      duration: 100
                                      amplifier: 1
                                      ambient: true
                                      has-particles: true
                                      has-icon: true
                """;
    }

    private @NotNull String getPlaceholders() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Placeholder type : Placeholders.getInstance().getPlaceholders()) {
            joiner.add(getPlaceholder(type));
        }
        return joiner.toString();
    }

    private @NotNull String getPlaceholder(@NotNull Placeholder placeholder) {
        StringJoiner joiner = new StringJoiner(", ");
        switch (placeholder) {
            case KeyPlaceholder keyPlaceholder -> {
                for (String key : keyPlaceholder.getKeys()) {
                    joiner.add(key);
                }
                return joiner + " - " + placeholder.getDescription();
            }
            case KeyValuePlaceholder keyValuePlaceholder -> {
                for (String key : keyValuePlaceholder.getKeys()) {
                    joiner.add(key);
                }
                return joiner + " - " + placeholder.getDescription();
            }
            default -> {
                return placeholder.getName() + " - " + placeholder.getDescription();
            }
        }
    }

    private @NotNull String getExecutors() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Executor executor : Executors.getInstance().getExecutors()) {
            if (executor.isDisabled()) continue;
            joiner.add(executor.getID().toUpperCase());
        }
        return joiner.toString();
    }

    private @NotNull String getGameValues() {
        StringJoiner joiner = new StringJoiner(", ");
        for (EventValue value : EventValues.getInstance().getEventValues()) {
            joiner.add(value.getID().toUpperCase() + "(" + getGameValueType(value) + ")");
        }
        return joiner.toString();
    }

    private @NotNull String getGameValueType(@NotNull EventValue eventValue) {
        return switch (eventValue) {
            case NumberEventValue ignored -> "NUMBER";
            case VectorEventValue ignored -> "VECTOR";
            case BooleanEventValue ignored -> "BOOLEAN";
            case ListEventValue ignored -> "LIST";
            case ItemEventValue ignored -> "ITEM";
            case LocationEventValue ignored -> "LOCATION";
            default -> "TEXT";
        };
    }

    private @NotNull String getActions() {
        StringJoiner joiner = new StringJoiner("\n");
        for (ActionType type : ActionType.values()) {
            if (type.isDisabled()) continue;
            joiner.add(type.name() + " " + getActionArguments(type));
        }
        return joiner.toString();
    }

    private @NotNull String getActionArguments(@NotNull ActionType type) {
        ArgumentSlot[] args = type.getArgumentsSlots();
        if (args == null) return "";
        if (args.length == 0) return "";
        StringJoiner joiner = new StringJoiner(", ");
        for (ArgumentSlot arg : args) {
            joiner.add(getArgument(arg));
        }
        return joiner.toString();
    }

    private @NotNull String getArgument(@NotNull ArgumentSlot argument) {
        // SLOT (NUMBER)
        // SAVE (BOOLEAN)
        // HAND (PARAMETER: main, off)
        // ITEMS (ITEM LIST OF 27)
        StringBuilder builder = new StringBuilder();
        builder.append(argument.getPath());
        builder.append(" (");
        if (argument instanceof ParameterSlot parameter) {
            builder.append("PARAMETER: ");
            builder.append(getParameterChoices(parameter));
            builder.append(")");
        } else if (argument.isList()) {
            builder.append(argument.getVarType().name());
            builder.append(" LIST OF ");
            builder.append(argument.getListSize());
            builder.append(")");
        } else {
            builder.append(argument.getVarType().name());
            builder.append(")");
        }
        return builder.toString();
    }

    private @NotNull String getParameterChoices(@NotNull ParameterSlot parameter) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Object value : parameter.getValues()) {
            joiner.add(String.valueOf(value));
        }
        return joiner.toString();
    }

}
