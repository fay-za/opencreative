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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.coding.modules.ModulesBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parseModuleLines;

public class ModuleCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = null;
        DevPlanet devPlanet = null;
        if (sender instanceof Player senderPlayer) {
            player = senderPlayer;

            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        }
        if (args.length == 0) {
            if (player != null) {
                if (devPlanet == null) {
                    sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                    return;
                }
                new ModulesBrowserMenu(player).open(player);
            } else {
                sender.sendMessage(getLocaleMessage("only-players"));
            }
            return;
        }
        switch (args[0].toLowerCase()) {
            case "load", "install" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (!canUseCommand(sender, player, devPlanet)) {
                    return;
                }
                if (devPlanet == null) return;
                Module module = OpenCreative.getModuleManager().getModuleById(args[1]);
                if (module == null) {
                    sender.sendMessage(getLocaleMessage("modules.not-found")
                            .replace("%id%", args[1]));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                module.place(devPlanet, player);
            }
            case "info" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                Module module = OpenCreative.getModuleManager().getModuleById(args[1]);
                if (module == null) {
                    sender.sendMessage(getLocaleMessage("modules.not-found")
                            .replace("%id%", args[1]));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                sender.sendMessage(parseModuleLines(module, getLocaleMessage("modules.info")));
            }
            case "delete" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (!sender.hasPermission("opencreative.modules.delete")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return;
                }
                Module module = OpenCreative.getModuleManager().getModuleById(args[1]);
                if (module == null) {
                    sender.sendMessage(getLocaleMessage("modules.not-found")
                            .replace("%id%", args[1]));
                    return;
                }
                sender.sendMessage(getLocaleMessage("modules.deleted")
                        .replace("%moduleID%", String.valueOf(module.getId())));
                OpenCreative.getPlugin().getLogger().info("Module " + module.getId() + " is being deleted by " + sender.getName());
                OpenCreative.getModuleManager().deleteModule(module);
            }
            case "list" -> {
                if (!sender.hasPermission("opencreative.modules.list")) {
                    sender.sendMessage(getLocaleMessage("no-perms"));
                    return;
                }
                Set<Module> modules = OpenCreative.getModuleManager().getModules();
                sender.sendMessage(getLocaleMessage("modules.list.amount")
                        .replace("%amount%", String.valueOf(modules.size())));
                for (Module module : modules) {
                    sender.sendMessage(parseModuleLines(module, getLocaleMessage("modules.list.module")));
                }
            }
            case "like" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (!canUseCommand(sender, player, devPlanet)) {
                    return;
                }
                if (devPlanet == null) return;
                Module module = OpenCreative.getModuleManager().getModuleById(args[1]);
                if (module == null) {
                    sender.sendMessage(getLocaleMessage("modules.not-found")
                            .replace("%id%", args[1]));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                if (!module.getInformation().wasDownloadedBefore(devPlanet.getPlanet())) {
                    sender.sendMessage(getLocaleMessage("modules.rating.not-installed"));
                    return;
                }
                if (module.getInformation().addLike(player)) {
                    sender.sendMessage(getLocaleMessage("modules.rating.liked"));
                    Sounds.DEV_MODULE_LIKED.play(player);
                } else {
                    sender.sendMessage(getLocaleMessage("modules.rating.already-rated"));
                    Sounds.PLAYER_FAIL.play(player);
                }
            }
            case "dislike" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                if (!canUseCommand(sender, player, devPlanet)) {
                    return;
                }
                if (devPlanet == null) return;
                Module module = OpenCreative.getModuleManager().getModuleById(args[1]);
                if (module == null) {
                    sender.sendMessage(getLocaleMessage("modules.not-found")
                            .replace("%id%", args[1]));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                if (!module.getInformation().wasDownloadedBefore(devPlanet.getPlanet())) {
                    sender.sendMessage(getLocaleMessage("modules.rating.not-installed"));
                    return;
                }
                if (module.getInformation().addDislike(player)) {
                    sender.sendMessage(getLocaleMessage("modules.rating.disliked"));
                    Sounds.DEV_MODULE_LIKED.play(player);
                } else {
                    sender.sendMessage(getLocaleMessage("modules.rating.already-rated"));
                    Sounds.PLAYER_FAIL.play(player);
                }
            }
            default -> sender.sendMessage(getLocaleMessage("modules.help"));
        }
    }

    private boolean canUseCommand(@NotNull CommandSender sender, @Nullable Player player, @Nullable DevPlanet devPlanet) {
        if (player == null) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return false;
        }
        if (devPlanet == null) {
            player.sendMessage(getLocaleMessage("only-in-dev-world"));
            return false;
        }
        if (!devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
            player.sendMessage(getLocaleMessage("not-developer"));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1) {
                return List.of("delete", "list", "info");
            } else if (args.length == 2 && List.of("delete", "info", "load", "like", "dislike").contains(args[0].toLowerCase())) {
                return OpenCreative.getModuleManager().getModules().stream()
                        .map(module -> String.valueOf(module.getId()))
                        .toList();
            }
            return null;
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("info");
            suggestions.add("like");
            suggestions.add("dislike");
            if (devPlanet != null) {
                suggestions.add("load");
            }
            if (player.hasPermission("opencreative.modules.delete")) {
                suggestions.add("delete");
            }
            if (player.hasPermission("opencreative.modules.list")) {
                suggestions.add("list");
            }
        } else if (args.length == 2) {
            if (player.hasPermission("opencreative.modules.delete")
                    && args[0].equalsIgnoreCase("delete") || (devPlanet != null
                    && List.of("like", "dislike", "load").contains(args[0].toLowerCase()))) {
                return OpenCreative.getModuleManager().getModules().stream()
                        .map(module -> String.valueOf(module.getId()))
                        .toList();
            }
        }
        return suggestions;
    }
}
