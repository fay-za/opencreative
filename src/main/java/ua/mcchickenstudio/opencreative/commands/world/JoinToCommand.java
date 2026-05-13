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

package ua.mcchickenstudio.opencreative.commands.world;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.List;

import static ua.mcchickenstudio.opencreative.commands.world.JoinCommand.handlePlayerConnection;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * <h1>JoinToCommand</h1>
 * This command is used to connect player to the specified player.
 * <p>
 * Available: For all players.
 */
public class JoinToCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
            if (player.isDead()) {
                player.sendMessage(getLocaleMessage("only-alive"));
                return;
            }
        } else if (args.length == 1) {
            sender.sendMessage(getLocaleMessage("only-players"));
        }

        if (OpenCreative.getSettings().isMaintenance() && !sender.hasPermission("opencreative.maintenance.bypass")) {
            sender.sendMessage(getLocaleMessage("maintenance"));
            return;
        }

        if (OpenCreative.getStability().isVeryBad() && !sender.hasPermission("opencreative.stability.bypass")) {
            sender.sendMessage(getLocaleMessage("creative.stability.cannot"));
            return;
        }

        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(getLocaleMessage("commands.join-to.help"));
            return;
        }

        if (args.length == 2 && !sender.hasPermission("opencreative.join-to.others")) {
            sender.sendMessage(getLocaleMessage("no-perms"));
            return;
        }

        String nickname = args[0];
        if (args.length == 2) nickname = args[1];
        Player foundPlayer = Bukkit.getPlayer(nickname);
        if (foundPlayer == null) {
            sender.sendMessage(getLocaleMessage("not-found-player"));
            return;
        }

        Player playerToConnect = null;
        if (args.length == 2) {
            playerToConnect = Bukkit.getPlayer(args[0]);
        } else if (sender instanceof Player player) {
            playerToConnect = player;
        }
        if (playerToConnect == null) {
            sender.sendMessage(getLocaleMessage("not-found-player"));
            return;
        }

        Planet foundPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(foundPlayer);
        if (foundPlanet == null) {
            // If world is lobby
            if (OpenCreative.getPlanetsManager().getPlanetByPlayer(playerToConnect) == null) {
                // If player is in lobby already
                playerToConnect.sendMessage(getPlayerLocaleMessage("same-world", playerToConnect));
                return;
            }
            new QuitEvent(playerToConnect).callEvent();
            teleportToLobby(playerToConnect);
            return;
        }
        // If world is planet
        handlePlayerConnection(playerToConnect, foundPlanet);
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getWorld().equals(player.getWorld()))
                .map(Player::getName).toList();
    }
}
