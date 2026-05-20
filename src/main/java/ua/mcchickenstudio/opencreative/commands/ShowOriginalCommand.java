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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.translation.OriginalMessageStore;

import java.util.List;
import java.util.UUID;

/**
 * <h1>ShowOriginalCommand</h1>
 * Displays the original (untranslated) chat message that produced a
 * given translation. Invoked by clicking the {@code [Show Original]}
 * button beside a translated chat line.
 * <p>
 * Usage: {@code /showoriginal <id>}
 */
public class ShowOriginalCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command,
                          @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return;
        if (args.length < 1) {
            sendUsage(player);
            return;
        }
        UUID id;
        try {
            id = UUID.fromString(args[0]);
        } catch (IllegalArgumentException ex) {
            sendUsage(player);
            return;
        }
        OriginalMessageStore.Entry entry = OriginalMessageStore.get(id);
        if (entry == null) {
            String missing = OpenCreative.getPlugin().getConfig().getString(
                    "messages.translation.original-not-found",
                    "<dark_gray>[<gray>Original message is no longer available<dark_gray>]"
            );
            player.sendMessage(MiniMessage.miniMessage().deserialize(legacyToMini(missing)));
            return;
        }
        String template = OpenCreative.getPlugin().getConfig().getString(
                "messages.translation.original-format",
                " <dark_gray>[<gray>%lang%<dark_gray>] <gray>%player%<dark_gray>: <white>%message%"
        );
        String rendered = legacyToMini(template
                .replace("%player%", entry.sender())
                .replace("%lang%", entry.sourceLanguage().toUpperCase())
                .replace("%message%", MiniMessage.miniMessage().escapeTags(entry.original())));
        Component out = MiniMessage.miniMessage().deserialize(rendered);
        player.sendMessage(out);
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command,
                                        @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private static void sendUsage(@NotNull Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gray>Usage: <white>/showoriginal <id>"
        ));
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
