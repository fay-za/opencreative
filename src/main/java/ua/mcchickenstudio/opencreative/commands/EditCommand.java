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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>EditCommand</h1>
 * This command is responsible for editing items data,
 * like display name and lore.
 * <p>
 * Available: For world builders or developers.
 */
public class EditCommand extends CommandHandler {

    private static final int TEXT_LIMIT = 100;
    private static final int LINES_LIMIT = 20;

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            OpenCreative.getPlugin().getLogger().info(getLocaleMessage("only-in-world"));
            return;
        }

        if (!checkPermissions(player)) return;

        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("commands.edit.help"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();
        if (item.getType().isAir() || meta == null) {
            sender.sendMessage(getLocaleMessage("commands.edit.item"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "name":
                handleSetName(player, item, args);
                break;
            case "lore", "setlore":
                handleSetLore(player, item, args);
                break;
            case "addlore":
                handleAddLore(player, item, args);
                break;
            case "removelore", "deletelore", "dellore", "remlore":
                handleRemoveLore(player, item, args);
                break;
            case "clear":
                handleClear(player, item);
                break;
        }

    }

    private boolean checkPermissions(Player player) {
        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return false;

        if (player.hasPermission("opencreative.edit.bypass")) return true;

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return false;
        }
        if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
            player.sendMessage(getLocaleMessage("not-owner"));
            return false;
        }

        return true;
    }

    private String joinArgs(String[] args, int fromIndex) {
        return String.join(" ", Arrays.copyOfRange(args, fromIndex, args.length));
    }

    private void handleSetName(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            Component displayName = meta.displayName();
            if (displayName == null) {
                player.sendMessage(getLocaleMessage("commands.edit.item"));
                return;
            }
            player.sendMessage(displayName.clickEvent(
                    ClickEvent.suggestCommand(LegacyComponentSerializer.legacyAmpersand().serialize(displayName))));
        } else {
            String message = joinArgs(args, 1);
            Component newName = fromInputToComponent(message);
            if (getComponentLength(newName) > TEXT_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%", String.valueOf(TEXT_LIMIT))));
                return;
            }
            if (!newName.hasDecoration(TextDecoration.ITALIC)) {
                newName = newName.decoration(TextDecoration.ITALIC, false);
            }
            meta.displayName(newName);
            item.setItemMeta(meta);
            player.sendMessage(getComponentWithPlaceholders("commands.edit.renamed",
                    player, "name", newName)
                    .clickEvent(ClickEvent.suggestCommand(message)));
        }
    }

    private int getComponentLength(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).length();
    }

    private void handleSetLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            player.sendMessage(getLocaleMessage("commands.edit.help"));
            return;
        }
        int lineNumber;
        try {
            lineNumber = Integer.parseInt(args[1]);
            if (lineNumber < 1) {
                lineNumber = 1;
            } else if (lineNumber > LINES_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit")
                        .replace("%limit%", String.valueOf(LINES_LIMIT))));
                return;
            }
        } catch (NumberFormatException ignored) {
            getComponentWithPlaceholders("commands.edit.not-number", player,
                    "argument", args[1]);
            return;
        }
        String message = joinArgs(args, 2);
        Component newLoreLine = fromInputToComponent(message);
        if (getComponentLength(newLoreLine) > TEXT_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%", String.valueOf(TEXT_LIMIT))));
            return;
        }
        if (!newLoreLine.hasDecoration(TextDecoration.ITALIC)) {
            newLoreLine = newLoreLine.decoration(TextDecoration.ITALIC, false);
        }
        List<Component> newLore = meta.lore();
        if (newLore == null) newLore = new ArrayList<>();
        if (newLore.size() < lineNumber) {
            while (newLore.size() < lineNumber) {
                newLore.add(Component.text(" "));
            }
        }
        if (args.length == 2) {
            player.sendMessage(newLore.get(lineNumber - 1)
                    .clickEvent(ClickEvent.suggestCommand(
                            LegacyComponentSerializer.legacyAmpersand().serialize(newLore.get(lineNumber - 1)))));
            return;
        }
        newLore.set(lineNumber - 1, newLoreLine);
        meta.lore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(getComponentWithPlaceholders("commands.edit.set-lore",
                player, "number", lineNumber, "lore", newLoreLine)
                .clickEvent(ClickEvent.suggestCommand(message)));
    }

    private void handleAddLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        String message = joinArgs(args, 1);
        Component newLoreLine = fromInputToComponent(message);
        if (getComponentLength(newLoreLine) > TEXT_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.text-limit").replace("%limit%", String.valueOf(TEXT_LIMIT))));
            return;
        }
        List<Component> newLore = meta.lore();
        if (newLore == null) newLore = new ArrayList<>();
        if (newLore.size() > LINES_LIMIT) {
            player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit")
                    .replace("%limit%", String.valueOf(LINES_LIMIT))));
            return;
        }
        if (!newLoreLine.hasDecoration(TextDecoration.ITALIC)) {
            newLoreLine = newLoreLine.decoration(TextDecoration.ITALIC, false);
        }
        newLore.add(newLoreLine);
        meta.lore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(getComponentWithPlaceholders("commands.edit.set-lore",
                player, "number", newLore.size(), "lore", message));
    }

    private void handleRemoveLore(Player player, ItemStack item, String[] args) {
        ItemMeta meta = item.getItemMeta();
        if (args.length == 1) {
            player.sendMessage(getLocaleMessage("commands.edit.item"));
            return;
        }
        int lineNumber;
        try {
            lineNumber = Integer.parseInt(args[1]);
            if (lineNumber < 1) {
                lineNumber = 1;
            } else if (lineNumber > LINES_LIMIT) {
                player.sendMessage(toComponent(getLocaleMessage("commands.edit.lines-limit").replace("%limit%", String.valueOf(LINES_LIMIT))));
                return;
            }
        } catch (NumberFormatException ignored) {
            getComponentWithPlaceholders("commands.edit.not-number", player,
                    "argument", args[1]);
            return;
        }
        List<Component> newLore = meta.lore();
        if (newLore == null) newLore = new ArrayList<>();
        if (newLore.size() >= lineNumber) {
            newLore.remove(lineNumber - 1);
        }
        meta.lore(newLore);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.removed-lore").replace("%number%", String.valueOf(lineNumber))));

    }

    private void handleClear(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(null);
        meta.lore(null);
        item.setItemMeta(meta);
        player.sendMessage(toComponent(getLocaleMessage("commands.edit.cleared")));
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length <= 1) {
            tabCompleter.add("name");
            tabCompleter.add("lore");
            tabCompleter.add("addlore");
            tabCompleter.add("removelore");
            tabCompleter.add("clear");
        }
        return tabCompleter;
    }
}
