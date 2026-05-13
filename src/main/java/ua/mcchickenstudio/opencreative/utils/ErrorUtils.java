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

package ua.mcchickenstudio.opencreative.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Argument;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.indev.messages.PlaceholderReplacer;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>ErrorUtils</h1>
 * This class contains utilities for handling exceptions
 * and logging them in console with friendly look.
 */
public final class ErrorUtils {

    private static final Random random = new Random();

    /**
     * Cuts common packages paths from stack trace text.
     *
     * @param text stack trace to cut.
     * @return stack trace without package paths,
     * or Error message is not available, if text is null.
     */
    public static String cutClassesName(@Nullable String text) {
        if (text == null) {
            return "Error message is not available";
        }
        String newText = text;
        newText = newText.replace("ua.mcchickenstudio.opencreative.coding.", "");
        newText = newText.replace("ua.mcchickenstudio.opencreative.", "");
        newText = newText.replace("org.bukkit.", "");
        newText = newText.replace("io.papermc.", "");
        newText = newText.replace("com.destroystokyo.", "");
        newText = newText.replace("java.lang.", "java.");
        newText = newText.replace("blocks.", "");
        return newText;
    }

    /**
     * Parses exception into user-friendly message, that can be
     * printed into console or sent to player.
     *
     * @param error   exception to parse.
     * @param colored true - for player, false - for console.
     * @return user-friendly exception.
     */
    public static @NotNull String parseException(@NotNull Exception error, boolean colored) {
        Set<String> lastStacks = new HashSet<>();
        byte i = 0;
        for (StackTraceElement stackTraceElement : error.getStackTrace()) {
            String stack = stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
            stack = cutClassesName(stack);
            lastStacks.add((colored ? "§c" : "") + stack);
            i++;
            if (i == 15) {
                break;
            }
        }
        return "\n" +
                (colored ? "§c" : "") +
                "☹ Exception has occurred..." +
                "\n" +
                (!colored ?
                        """
                                \\|/ _____ \\|/
                                "@'/ . . \\`@"\s""" + OpenCreative.getVersion() + """
                                \n/_| \\___/ |_\\\s""" + getRandomPhrase() + """
                                \n   \\___U_/
                                """ : "") +
                (colored ? "§4 " : " ") +
                error.getClass().getSimpleName() +
                ": " +
                cutClassesName(error.getMessage()) +
                "\n \n" +
                String.join("\n", lastStacks);
    }

    /**
     * Returns random phrase for displaying error log.
     *
     * @return random phrase.
     */
    private static @NotNull String getRandomPhrase() {
        String[] phrases = new String[]{
                "Things aren't so different..", "Seems like we messed up..",
                "We never gonna give console up..", "We'll meet again, some sunny day..",
                "We're fine, We won't lose our mind..", "Oops!", "Kernel Panic.. are we kernel?",
                "We'll see.. if you will report it.", "It is fine..", "I don't like errors, okay?",
                "At least not memory leak, yes?", "Don't be mad, be happy that something works.",
                "Totally OpenCreative+.", "My final message - goodbye!", "And here's we hanging out.",
                "This plugin ate a sparc! Gah!", "I blame PEOPLE BELOW for this.", "Well, it's possible. <-- (our catchphrase)",
                "Bug after bug, after bug, it never ends.", "I'll get out of this problem tonight.",
                "Try not to think about how long bug was here.", "You'll get one bug instead of zero.",
                "Totally powered by Java.", "Totally Minecraft plugin.", "Can someone just show me the fix?",
                "Errors in the plugin that never ends", "It might just work if we can try not to go insane",
                "Try not to panic when you see this", "This is not supposed to be here",
                ":( Your PLUGIN ran into a problem.", "Why is this happening?", "Dum dum, this sucks..",
                "Error the Troublemaker..", "Bug toy, bug toy, bug toy...", "Get your bug toy, get a little opposite of joy",
                "It's an error, munch munch munch!", "And then an error meets me, because I'm there.",
                "Sometimes I pretend it's fixed, I might be liar.", "I don't see a fix on the normal route it walks.",
                "I could say I'm sorry, but it's not that kind of party", "The right error in the wrong place",
                "Raise and shine, Mr. Error!"
        };
        return phrases[random.nextInt(phrases.length)];
    }

    /**
     * Notifies player about error by sending message with error
     * description in chat and sends warning log in console.
     *
     * @param player       player to send error message.
     * @param errorMessage message of exception.
     */
    public static void sendPlayerErrorMessage(Player player, String errorMessage) {
        OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage);
        player.sendMessage(getLocaleMessage("player-error").replace("%error%", errorMessage));
        Sounds.PLAYER_ERROR.play(player);
    }

    /**
     * Notifies player about error by sending message with error
     * description in chat and sends warning log in console
     * with stack traces to find line, where error has occurred.
     *
     * @param player       player to send error message.
     * @param errorMessage description of error.
     * @param error        exception, that has occurred.
     */
    public static void sendPlayerErrorMessage(Player player, String errorMessage, Exception error) {
        if (OpenCreative.getSettings().shouldLogWarnings())
            OpenCreative.getPlugin().getLogger().warning("An player error has occurred for " + player.getName() + ": " + errorMessage + " " + parseException(error, false));
        Component message = Component
                .text(getLocaleMessage("player-error").replace("%error%", errorMessage))
                .hoverEvent(HoverEvent.showText(Component.text(parseException(error, true))));
        player.sendMessage(message);
        Sounds.PLAYER_ERROR.play(player);
    }

    /**
     * Notifies planet players about general error by sending message
     * with error description in chat and sends warning log in console.
     * <p>
     * Not related to coding errors.
     *
     * @param planet planet to send error message.
     * @param error  description of error.
     */
    public static void sendPlanetErrorMessage(@NotNull Planet planet, @NotNull String error) {
        if (OpenCreative.getSettings().shouldLogWarnings())
            OpenCreative.getPlugin().getLogger().warning("An error has occurred in planet " + planet.getWorldName() + ": " + error);
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("planet-error").replace("%error%", error));
            Sounds.PLAYER_ERROR.play(player);
        }
    }

    /**
     * Notifies planet players about general error by sending message
     * with error description in chat and sends warning log in console
     * with stack traces to find line, where error has occurred.
     * <p>
     * Not related to coding errors.
     *
     * @param planet       planet to send error message.
     * @param errorMessage description of error.
     * @param error        exception, that has occurred.
     */
    public static void sendPlanetErrorMessage(Planet planet, String errorMessage, Exception error) {
        if (OpenCreative.getSettings().shouldLogWarnings())
            OpenCreative.getPlugin().getLogger().warning("An error has occurred in planet " + planet.getWorldName() + ": " + errorMessage + " " + parseException(error, false));
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("planet-error").replace("%error%", errorMessage))
                    .hoverEvent(HoverEvent.showText(Component.text(parseException(error, true))));
            player.sendMessage(message);
            Sounds.PLAYER_ERROR.play(player);
        }
    }

    /**
     * Notifies planet players about reaching limit,
     * so some operations will be cancelled.
     *
     * @param action   action, that produced warning.
     * @param limitID limit name.
     * @param count count of operations.
     * @param limit maximum amount of operations.
     */
    public static void sendPlanetLimitWarningMessage(@NotNull Action action,
                                                     @NotNull String limitID,
                                                     int count, int limit) {
        Planet planet = action.getExecutor().getPlanet();
        if (planet == null) return;
        if (cantSendOnceMessage(planet, 5)) return;
        for (Player player : planet.getPlayers()) {
            Component text = getPlayerLocaleComponent("coding-warning.message", player);
            text = new PlaceholderReplacer("warning",
                    getPlayerLocaleComponent("coding-warning." + limitID + "-limit.text", player))
                    .apply(text);
            text = new PlaceholderReplacer(
                    "count", count, "limit",  limit
            ).apply(text);
            text = text.hoverEvent(HoverEvent.showText(getPlayerLocaleComponent("coding-warning." + limitID + "-limit.hover", player)));
            text = text.clickEvent(ClickEvent.runCommand(
                    "/dev " + action.getX() + " " + action.getY() + " " + action.getZ()
            ));
            player.sendMessage(text);
            Sounds.WORLD_CODE_WARNING.play(player);
        }
    }

    /**
     * Notifies planet players about reaching limit,
     * so some operations will be canceled.
     *
     * @param planet planet, where limit was reached.
     * @param limitID limit name.
     * @param command command to execute on click.
     * @param count count of operations.
     * @param limit maximum amount of operations.
     */
    public static void sendPlanetLimitWarningMessage(@NotNull Planet planet,
                                                     @NotNull String limitID,
                                                     @Nullable String command,
                                                     int count, int limit) {
        if (cantSendOnceMessage(planet, 5)) return;
        for (Player player : planet.getPlayers()) {
            Component text = getPlayerLocaleComponent("coding-warning.message", player);
            text = new PlaceholderReplacer("warning",
                    getPlayerLocaleComponent("coding-warning." + limitID + "-limit.text", player))
                    .apply(text);
            text = new PlaceholderReplacer(
                    "count", count, "limit",  limit
            ).apply(text);
            text = text.hoverEvent(HoverEvent.showText(getPlayerLocaleComponent("coding-warning." + limitID + "-limit.hover", player)));
            if (command != null) {
                text = text.clickEvent(ClickEvent.runCommand(command));
            }
            player.sendMessage(text);
            Sounds.WORLD_CODE_WARNING.play(player);
        }
    }

    /**
     * Notifies planet players about coding warning, that has
     * happened while executing action in executor.
     * <p>
     * Warnings don't stop executing the code, they just
     * send notification, that something can be improved.
     *
     * @param executor executor, that executed action.
     * @param action   action, that produced warning.
     * @param warningID path of warning message.
     * @param placeholder placeholders to replace.
     */
    public static void sendPlanetCodeWarningMessage(@NotNull Executor executor, @NotNull Action action,
                                                    @NotNull String warningID, @NotNull PlaceholderReplacer placeholder) {
        Planet planet = executor.getPlanet();
        if (planet == null) return;
        String command = "/dev " + action.getX() + " " + executor.getY() + " " + executor.getZ();
        sendMessageOnce(planet, "coding-warning." + warningID + ".text", placeholder,
                command, "coding-warning." + warningID + "hover", 5);
        for (Player player : planet.getPlayers()) {
            Sounds.WORLD_CODE_WARNING.play(player);
        }
    }

    /**
     * Notifies planet players about coding exception, that has
     * happened while executing action in executor.
     * <p>
     * Errors stop executing next actions to prevent
     * happening new errors in same coding line. They
     * tell that something went wrong, and it needs
     * to be fixed.
     *
     * @param executor     executor, that executed action.
     * @param action       action, that produced error.
     * @param errorMessage description of error.
     * @param error        exception, that has occurred.
     */
    public static void sendPlanetCodeErrorMessage(Executor executor, Action action, String errorMessage, Exception error) {
        Planet planet = executor.getPlanet();
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message")
                            .replace("%event%", executor.getLocaleName())
                            .replace("%action%", action.getActionType().getLocaleName())
                            .replace("%error%", errorMessage)
                            .replace("%x%", String.valueOf(action.getX()))
                            .replace("%y%", String.valueOf(action.getExecutor().getY()))
                            .replace("%z%", String.valueOf(action.getExecutor().getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message") + "\n" + parseException(error, true))))
                    .clickEvent(ClickEvent.runCommand("/dev " + (action.getX() - 0.5) + " " + action.getExecutor().getY() + " " + (action.getExecutor().getZ() - 0.5)));
            player.sendMessage(message);
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    public static void notifyBuildModeByCode(Executor executor, Action action) {
        Planet planet = executor.getPlanet();
        for (Player player : planet.getPlayers()) {
            Component message = getComponentWithPlaceholders("world.build-mode.changed-because-of-code",
                    player,
                    "event", executor.getLocaleName(),
                    "action", action.getActionType().getLocaleName(),
                    "x", action.getX(),
                    "y", action.getExecutor().getY(),
                    "z", action.getExecutor().getZ())
                    .hoverEvent(HoverEvent.showText(getLocaleComponent("coding-error.hover-message")))
                    .clickEvent(ClickEvent.runCommand("/dev " + (action.getX() - 0.5) + " " + action.getExecutor().getY() + " " + (action.getExecutor().getZ() - 0.5)));
            player.sendMessage(message);
        }
    }

    /**
     * Notifies planet players about coding exception, that has
     * happened while executing action in executor.
     * <p>
     * Errors stop executing next actions to prevent
     * happening new errors in same coding line. They
     * tell that something went wrong, and it needs
     * to be fixed.
     *
     * @param executor     executor, that executed action.
     * @param action       action, that produced error.
     * @param entity       target of action.
     * @param errorMessage description of error.
     */
    public static void sendPlanetCodeErrorMessage(Executor executor, Action action, Entity entity, String errorMessage) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld());
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(
                    getLocaleMessage("coding-error.message")
                            .replace("%event%", executor.getLocaleName())
                            .replace("%action%", action.getActionType().toString())
                            .replace("%error%", errorMessage)
                            .replace("%x%", String.valueOf(action.getX()))
                            .replace("%y%", String.valueOf(executor.getY()))
                            .replace("%z%", String.valueOf(executor.getZ())));
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     * Notifies planet players about coding fatal error,
     * that has happened while executing code.
     * <p>
     * Critical errors stop entire code in planet,
     * and change its mode to Build.
     *
     * @param planet       planet to send error message.
     * @param executor     executor, that executed action.
     * @param errorMessage description of error.
     */
    public static void sendPlanetCodeCriticalErrorMessage(Planet planet, Executor executor, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Sounds.WORLD_CODE_CRITICAL_ERROR.play(player);
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-event-critical")
                            .replace("%event%", executor.getLocaleName())
                            .replace("%error%", errorMessage)
                            .replace("%x%", String.valueOf(executor.getX()))
                            .replace("%y%", String.valueOf(executor.getY()))
                            .replace("%z%", String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
        }
    }

    /**
     * Notifies planet players about coding exception, that has
     * happened while executing action in executor.
     * <p>
     * Errors stop executing next actions to prevent
     * happening new errors in same coding line. They
     * tell that something went wrong, and it needs
     * to be fixed.
     *
     * @param planet       planet to send error message.
     * @param executor     executor, that executed action.
     * @param errorMessage description of error.
     */
    public static void sendPlanetCodeErrorMessage(Planet planet, Executor executor, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-event")
                            .replace("%event%", executor.getLocaleName())
                            .replace("%error%", errorMessage)
                            .replace("%x%", String.valueOf(executor.getX()))
                            .replace("%y%", String.valueOf(executor.getY()))
                            .replace("%z%", String.valueOf(executor.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + executor.getX() + " " + executor.getY() + " " + executor.getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_ERROR.play(player);
        }
    }

    /**
     * Notifies planet players about coding issue, that has
     * happened while compiling new code from dev planet.
     *
     * @param planet       planet to send error message.
     * @param block        block, that caused issue.
     * @param errorMessage description of error.
     */
    public static void sendPlanetCompileErrorMessage(Planet planet, Block block, String errorMessage) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            Component message = Component
                    .text(getLocaleMessage("coding-error.message-compile")
                            .replace("%error%", errorMessage)
                            .replace("%x%", String.valueOf(block.getX()))
                            .replace("%y%", String.valueOf(block.getY()))
                            .replace("%z%", String.valueOf(block.getZ())))
                    .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                    .clickEvent(ClickEvent.runCommand("/dev " + block.getX() + " " + block.getY() + " " + block.getZ()));
            player.sendMessage(message);
            Sounds.WORLD_CODE_COMPILE_ERROR.play(player);
        }
    }

    /**
     * Notifies planet players about unknown coding blocks,
     * that were found while compiling a new code from
     * dev planet.
     *
     * @param planet        planet to send error message.
     * @param unknownBlocks list of unknown blocks.
     */
    public static void sendPlanetCompileErrorMessage(Planet planet, List<Block> unknownBlocks) {
        if (planet == null) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-error.unknown-block-detected").replace("%error%", getLocaleMessage("coding-error.unknown-blocks", false)));
            for (Block block : unknownBlocks) {
                NamedTextColor color = NamedTextColor.GRAY;
                String category = "???";
                String type = getSignLine(block.getLocation(), (byte) 3);
                if (type == null || type.isEmpty()) type = "???";
                ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());

                if (executorCategory != null) {
                    color = executorCategory.getColor();
                    category = executorCategory.getLocaleName();
                } else if (actionCategory != null) {
                    color = actionCategory.getColor();
                    category = actionCategory.getLocaleName();
                }

                Component blockCoordinatesMessage = Component
                        .text(getLocaleMessage("coding-error.unknown-block-coords")
                                .replace("%x%", String.valueOf(block.getLocation().getX()))
                                .replace("%y%", String.valueOf(block.getLocation().getY()))
                                .replace("%z%", String.valueOf(block.getLocation().getZ()))
                                .replace("%category%", category)
                                .replace("%type%", type))
                        .color(color)
                        .hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("coding-error.hover-message"))))
                        .clickEvent(ClickEvent.runCommand("/dev " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()));
                player.sendMessage(blockCoordinatesMessage);
            }
            Sounds.WORLD_CODE_COMPILE_ERROR.play(player);
            player.sendMessage(" ");
        }
    }

    /**
     * Sends warning log in console about issue with plugin.
     *
     * @param warning description of warning.
     */
    public static void sendWarningErrorMessage(String warning) {
        if (OpenCreative.getSettings().shouldLogWarnings()) {
            OpenCreative.getPlugin().getLogger().warning("Warning! " + warning);
        }
    }

    /**
     * Sends warning log in console with stack traces
     * to find line, that produced not too serious error.
     *
     * @param errorMessage description of warning.
     * @param error        exception, that has occurred.
     */
    public static void sendWarningMessage(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().shouldLogWarnings()) {
            OpenCreative.getPlugin().getLogger().warning("Warning! " + errorMessage + " " + parseException(error, false));
        }
    }

    /**
     * Sends error log in console about problem with plugin.
     *
     * @param errorMessage description of error.
     */
    public static void sendCriticalErrorMessage(String errorMessage) {
        if (OpenCreative.getSettings().shouldLogCriticalErrors()) {
            OpenCreative.getPlugin().getLogger().severe(":( Oops! An error has occurred: " + errorMessage);
        }
    }

    /**
     * Sends error log in console with stack traces
     * to find line, that produced critical error.
     *
     * @param errorMessage description of critical error.
     * @param error        exception, that has occurred.
     */
    public static void sendCriticalErrorMessage(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().shouldLogCriticalErrors()) {
            OpenCreative.getPlugin().getLogger().severe(":( Oops! An error has occurred: " + errorMessage + " " + parseException(error, false));
        }
    }

    /**
     * Sends debug log in console, only if debug mode
     * is enabled in plugin's settings.
     *
     * @param message debug log.
     */
    public static void sendDebug(String message) {
        if (OpenCreative.getSettings().isDebug()) {
            OpenCreative.getPlugin().getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * Sends error log in console with stack traces,
     * only if debug mode is enabled in plugin's
     * settings.
     * <p>
     * Used for not serious errors for server owners.
     *
     * @param errorMessage description of critical error.
     * @param error        exception, that has occurred.
     */
    public static void sendDebugError(String errorMessage, Exception error) {
        if (OpenCreative.getSettings().isDebug()) {
            OpenCreative.getPlugin().getLogger().severe("CRITICAL ERROR has occurred: " + errorMessage + " " + parseException(error, false));
        }
    }

    /**
     * Sends notification to planet players about not
     * found value while filling arguments in action,
     * only if planet's debug mode is enabled.
     * <p>
     * Happens when player forgets to fill items
     * in coding container.
     *
     * @param planet planet to send error message.
     * @param name   name of value.
     */
    public static void sendCodingDebugNotFoundVariable(Planet planet, String name) {
        if (true) {
            return;
        }
        if (!planet.isDebug()) return;
        Object value = null;
        if (value == null) value = "null";
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.variable-not-found", false).replace("%name%", name).replace("%value%", value.toString()));
        }
    }

    /**
     * Sends notification to planet players about not
     * found event value while executing actions or
     * conditions, that require event value.
     * <p>
     * Happens when player uses wrong coding block
     * in event.
     *
     * @param planet   planet to send message.
     * @param executor executor, that stores event.
     * @param clazz    class of event value, that was not found.
     */
    public static void sendCodingNotFoundEventValue(Planet planet, Executor executor, Class<? extends EventValue> clazz) {
        if (planet == null) return;
        EventValue eventValue = EventValues.getInstance().getByClass(clazz);
        sendPlanetCodeErrorMessage(planet, executor, getLocaleMessage("coding-error.temp-var-not-exists", false)
                .replace("%variable%", eventValue != null ? eventValue.getLocaleName() : clazz.getSimpleName()));
    }

    /**
     * Sends debug log message to planet players,
     * if planet's debug mode is enabled.
     * <p>
     * Used to describe some reasons of
     * events cancellations, actions fails
     * or processes.
     *
     * @param planet planet to send log.
     * @param log    debug log.
     */
    public static void sendCodingDebugLog(Planet planet, String log) {
        if (!planet.isDebug()) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.log", false).replace("%log%", log));
        }
    }

    /**
     * Sends notification to planet players about
     * found value while filling arguments in action,
     * only if planet's debug mode is enabled.
     *
     * @param planet planet to send message.
     * @param name   executor, that stores event.
     * @param value  value.
     */
    public static void sendCodingDebugVariable(Planet planet, String name, Object value) {
        if (true) {
            return;
        }
        if (!planet.isDebug()) return;
        if (value == null) value = "null";
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.variable-found", false).replace("%name%", name).replace("%value%", value.toString()));
        }
    }

    /**
     * Sends notification to planet players about
     * calling executor.
     *
     * @param executor executor, that has activated.
     */
    public static void sendCodingDebugExecutor(Executor executor) {
        Planet planet = executor.getPlanet();
        if (!executor.isDebug()) return;
        if (!planet.isDebug()) return;
        for (Player player : planet.getPlayers()) {
            player.sendMessage(getLocaleMessage("coding-debug.executor-message", false)
                    .replace("%type%", executor.getLocaleName()).replace("%x%", String.valueOf(executor.getX())).replace("%y%", String.valueOf(executor.getY())).replace("%z%", String.valueOf(executor.getZ())));
        }
    }

    /**
     * Sends notification to planet players about
     * action information, that will be executed.
     * Only if planet's debug mode is enabled.
     *
     * @param action action, that will be executed.
     */
    public static void sendCodingDebugAction(Action action) {
        if (action.getExecutor() == null) return;
        if (!action.getExecutor().isDebug()) return;
        Planet planet = action.getExecutor().getPlanet();
        if (!planet.isDebug()) return;
        List<Argument> arguments = action.getArgumentsList();
        String message = getLocaleMessage("coding-debug.hover." + (action.getActionType().isCondition() ? "condition" : "action"));
        message = message.replace("%category%", action.getActionCategory().getLocaleName());
        message = message.replace("%type%", action.getActionType().getLocaleName());
        if (action instanceof Condition condition) {
            message = message.replace("%opposed%", getLocaleMessage("coding-debug.condition.opposed." + condition.isOpposed()));
        }
        List<String> argumentsString = new ArrayList<>();
        for (Argument arg : arguments) {
            argumentsString.add(getLocaleMessage("coding-debug.hover.argument")
                    .replace("%name%", arg.getPath())
                    .replace("%type%", arg.getType().getLocaleName())
                    .replace("%value%", ValueType.getDisplayShortString(arg.getValue(action))));
        }
        message = message.replace("%arguments%", String.join(" \n", argumentsString));
        String actionMessage = getLocaleMessage("coding-debug.action-message", false).replace("%type%", action.getActionType().getLocaleName()).replace("%x%", String.valueOf(action.getX())).replace("%y%", String.valueOf(action.getExecutor().getY())).replace("%z%", String.valueOf(action.getExecutor().getZ()));
        for (Player player : planet.getPlayers()) {
            player.sendMessage(Component.text(actionMessage)
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(message))));
        }
    }

}
