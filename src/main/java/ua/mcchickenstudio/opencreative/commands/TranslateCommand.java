/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package ua.mcchickenstudio.opencreative.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.translation.TranslationCache;
import ua.mcchickenstudio.opencreative.utils.translation.TranslationPreferences;

import java.util.List;

/**
 * <h1>TranslateCommand</h1>
 * Manages per-player chat translation preferences.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>{@code /translate toggle} — toggles translation on/off for the calling player.</li>
 *   <li>{@code /translate status}  — prints the current opt-in state.</li>
 *   <li>{@code /translate cache}   — (operators) shows cache hits/misses/size.</li>
 *   <li>{@code /translate clearcache} — (operators) clears the translation cache.</li>
 * </ul>
 */
public class TranslateCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command,
                          @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "toggle" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gray>Only players can toggle their translation preference."));
                    return;
                }
                boolean nowOptedOut = TranslationPreferences.toggle(player);
                String key = nowOptedOut
                        ? "messages.translation.toggle-off"
                        : "messages.translation.toggle-on";
                String fallback = nowOptedOut
                        ? "<dark_gray>[<gray>Translation <red>disabled<gray> — you will see messages in their original language<dark_gray>]"
                        : "<dark_gray>[<gray>Translation <green>enabled<gray> — non-native messages will be auto-translated for you<dark_gray>]";
                String text = OpenCreative.getPlugin().getConfig().getString(key, fallback);
                player.sendMessage(MiniMessage.miniMessage().deserialize(legacyToMini(text)));
            }
            case "status" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gray>Only players have a translation preference."));
                    return;
                }
                boolean optedOut = TranslationPreferences.isOptedOut(player);
                player.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gray>Translation is currently <" + (optedOut ? "red>disabled" : "green>enabled") + "<gray>."));
            }
            case "cache" -> {
                if (!sender.isOp()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<red>You need operator permission to inspect the translation cache."));
                    return;
                }
                long hits = TranslationCache.hits();
                long misses = TranslationCache.misses();
                long total = hits + misses;
                double ratio = total == 0 ? 0.0 : (100.0 * hits / total);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gray>Translation cache: <white>" + TranslationCache.size()
                                + "<gray> entries · hits=<white>" + hits
                                + "<gray> misses=<white>" + misses
                                + "<gray> hit-rate=<white>" + String.format("%.1f%%", ratio)));
            }
            case "clearcache" -> {
                if (!sender.isOp()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<red>You need operator permission to clear the translation cache."));
                    return;
                }
                TranslationCache.clear();
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gray>Translation cache cleared."));
            }
            default -> sendUsage(sender);
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command,
                                        @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return List.of("toggle", "status", "cache", "clearcache");
            }
            return List.of("toggle", "status");
        }
        return null;
    }

    private static void sendUsage(@NotNull CommandSender sender) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gray>Usage: <white>/translate <toggle|status>"));
    }

    private static @NotNull String legacyToMini(@NotNull String input) {
        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(i + 1));
                String tag = switch (code) {
                    case '0' -> "<black>";
                    case '1' -> "<dark_blue>";
                    case '2' -> "<dark_green>";
                    case '3' -> "<dark_aqua>";
                    case '4' -> "<dark_red>";
                    case '5' -> "<dark_purple>";
                    case '6' -> "<gold>";
                    case '7' -> "<gray>";
                    case '8' -> "<dark_gray>";
                    case '9' -> "<blue>";
                    case 'a' -> "<green>";
                    case 'b' -> "<aqua>";
                    case 'c' -> "<red>";
                    case 'd' -> "<light_purple>";
                    case 'e' -> "<yellow>";
                    case 'f' -> "<white>";
                    case 'k' -> "<obfuscated>";
                    case 'l' -> "<bold>";
                    case 'm' -> "<strikethrough>";
                    case 'n' -> "<underlined>";
                    case 'o' -> "<italic>";
                    case 'r' -> "<reset>";
                    default -> null;
                };
                if (tag != null) {
                    out.append(tag);
                    i++;
                    continue;
                }
            }
            out.append(c);
        }
        return out.toString();
    }
}
