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

package ua.mcchickenstudio.opencreative.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.PatternReplacementResult;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.player.CreativeChatEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>ChatCommand</h1>
 * This command is used to chat with all players on server.
 * <p>
 * Available: For all players.
 */
public class ChatCommand extends CommandHandler {

    public static final Set<Player> creativeChatOff = new HashSet<>();

    private static @NotNull Component parseAdvertisementInMessage(@NotNull Component component) {
        Component result = parseAdvertisementCommand(component, "join");
        if (!component.equals(result)) {
            // If message already has /join invite then return it.
            return result;
        }
        return parseAdvertisementCommand(component, "ad");
    }

    private static @NotNull Component parseAdvertisementCommand(@NotNull Component component, @NotNull String commandLabel) {
        AtomicBoolean alreadyReplaced = new AtomicBoolean(false);
        return component.replaceText(TextReplacementConfig.builder()
                .match("(/" + commandLabel + ")\\s+(\\S+)")
                .once()
                .condition((match, matchCount, replaced) -> {
                    String id = match.group(2);
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByAnyID(id);
                    if (planet == null) {
                        return PatternReplacementResult.CONTINUE;
                    }
                    if (alreadyReplaced.get()) return PatternReplacementResult.STOP;
                    alreadyReplaced.set(true);
                    return PatternReplacementResult.REPLACE;
                })
                .replacement((match, builder) -> {
                    String command = match.group(1) + " " + match.group(2);
                    String id = match.group(2);
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByAnyID(id);
                    if (planet == null) {
                        return Component.text(command);
                    }
                    Component hover = parsePlanetLines(planet, getLocaleComponent("advertisement.hover"));
                    return Component.text(command)
                            .color(NamedTextColor.YELLOW)
                            .decorate(TextDecoration.UNDERLINED)
                            .hoverEvent(HoverEvent.showText(hover))
                            .clickEvent(ClickEvent.suggestCommand(command));
                })
                .build());
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (OpenCreative.getSettings().isMaintenance() && !sender.hasPermission("opencreative.maintenance.bypass")) {
            sender.sendMessage(getLocaleMessage("maintenance"));
            return;
        }
        if (OpenCreative.getStability().isVeryBad() && !sender.hasPermission("opencreative.stability.bypass")) {
            sender.sendMessage(getLocaleMessage("creative.stability.cannot"));
            return;
        }
        if (!OpenCreative.getSettings().isCreativeChatEnabled() && !sender.hasPermission("opencreative.creative-chat.bypass")) {
            sender.sendMessage(getLocaleMessage("creative.creative-chat.off"));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("creative-chat.cc-usage"));
            return;
        }
        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.CREATIVE_CHAT)) return;
        }
        if (args.length == 1 && (args[0].equalsIgnoreCase("off")
                || args[0].equalsIgnoreCase("on")
                || args[0].equalsIgnoreCase("enable")
                || args[0].equalsIgnoreCase("disable"))
                && sender instanceof Player player) {
            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable")) {
                creativeChatOff.add(player);
                sender.sendMessage(getLocaleMessage("creative-chat.turned-off"));
            } else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable")) {
                creativeChatOff.remove(player);
                sender.sendMessage(getLocaleMessage("creative-chat.turned-on"));
            }
            return;
        }
        if (sender instanceof Player player) {
            if (creativeChatOff.contains(player)) {
                sender.sendMessage(getLocaleMessage("creative-chat.on-usage"));
                return;
            }
        }
        OpenCreative.getPlugin().getLogger().info("[CREATIVE-CHAT] " + sender.getName()
                + ": " + String.join(" ", args));

        String text = String.join(" ", args);
        String prefix = OpenCreative.getPlugin().getConfig().getString("messages.cc-prefix", "&6 Chat &8| &7");
        if (!(sender instanceof Player player)) {
            // If sender is console
            Component formatted = toComponent(prefix + sender.getName() + text);

            CreativeChatEvent event = new CreativeChatEvent(sender, text, formatted);
            event.callEvent();
            if (event.isCancelled()) return;

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!(creativeChatOff.contains(onlinePlayer))) {
                    onlinePlayer.sendMessage(formatted);
                }
            }
            return;
        }
        String format = OpenCreative.getPlugin().getConfig().getString("messages.cc-chat", "&6%cc-prefix% &7%player%: %message%")
                .replace("%player%", sender.getName())
                .replace("%cc-prefix%", prefix);
        format = parsePAPI(player, format);
        Component formatted = toComponent(format
                .replace("%message%", MiniMessage.miniMessage().escapeTags(text)));
        if (formatted.clickEvent() == null) formatted = formatted.clickEvent(ClickEvent.suggestCommand(text));
        formatted = parseAdvertisementInMessage(formatted);

        CreativeChatEvent event = new CreativeChatEvent(sender, text, formatted);
        event.callEvent();
        if (event.isCancelled()) return;

        formatted = event.getFormattedMessage();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!(creativeChatOff.contains(onlinePlayer))) {
                onlinePlayer.sendMessage(formatted);
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
