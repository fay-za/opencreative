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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.removePassengers;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>SpawnCommand</h1>
 * This command is responsible for teleporting players to lobby.
 * <p>
 * Available: For all players.
 */
public class SpawnCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
            if (player.isDead()) {
                sender.sendMessage(getLocaleMessage("only-alive"));
                return;
            }
        }
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                // Console cannot be teleported to lobby
                sender.sendMessage(getLocaleMessage("only-players"));
                return;
            }
            new QuitEvent(player).callEvent();
            removePassengers(player);
            teleportToLobby(player);
            return;
        }
        if (!sender.hasPermission("opencreative.spawn.others")) {
            sender.sendMessage(getLocaleMessage("no-perms"));
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(getLocaleMessage("not-found-player"));
            return;
        }
        new QuitEvent(player).callEvent();
        removePassengers(player);
        teleportToLobby(player);
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("opencreative.spawn.others") || args.length > 1) {
            return null;
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
