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
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>JoinCommand</h1>
 * This command is used to connect player to specified planet
 * by its numeric or text ID.
 * <p>
 * Available: For all players.
 */
public class JoinCommand extends CommandHandler {

    public static boolean handlePlayerConnection(@NotNull Player player, @NotNull String planetId) {
        Planet foundPlanet = findPlanet(planetId);

        if (foundPlanet == null) {
            Sounds.PLAYER_FAIL.play(player);
            player.clearTitle();
            player.sendMessage(MessageUtils.getPlayerLocaleMessage("no-planet-found", player));
            return false;
        }

        return handlePlayerConnection(player, foundPlanet);
    }

    public static boolean handlePlayerConnection(@NotNull Player player, @NotNull Planet planet) {
        if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
            player.sendMessage(MessageUtils.getPlayerLocaleMessage("same-world", player));
            return false;
        }

        planet.connectPlayer(player);
        return true;
    }

    public static Planet findPlanet(String planetId) {
        Set<Planet> planets = OpenCreative.getPlanetsManager().getPlanets();
        if (planets.isEmpty()) return null;

        return planets.stream()
                .filter(planet -> String.valueOf(planet.getId()).equals(planetId) ||
                        planet.getInformation().getCustomID().equalsIgnoreCase(planetId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
        }

        if (OpenCreative.getSettings().isMaintenance() && !sender.hasPermission("opencreative.maintenance.bypass")) {
            sender.sendMessage(getLocaleMessage("maintenance"));
            return;
        }

        if (OpenCreative.getStability().isVeryBad() && !sender.hasPermission("opencreative.stability.bypass")) {
            sender.sendMessage(getLocaleMessage("creative.stability.cannot"));
            return;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("opencreative.join.others")) {
                sender.sendMessage(getLocaleMessage("no-perms"));
                return;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(getLocaleMessage("not-found-player"));
                return;
            }
            sender.sendMessage(MessageUtils.getPlayerLocaleMessage("commands.join.connecting", player).replace("%id%", args[0]));
            if (!handlePlayerConnection(player, args[0])) {
                sender.sendMessage(MessageUtils.getPlayerLocaleMessage("commands.join.failed", player));
            }
        } else if (args.length != 1) {
            sender.sendMessage(getLocaleMessage("commands.join.help"));
        } else if (sender instanceof Player player) {
            if (player.isDead()) {
                player.sendMessage(getLocaleMessage("only-alive"));
                return;
            }
            handlePlayerConnection(player, args[0]);
        } else {
            sender.sendMessage(getLocaleMessage("only-players"));
        }
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return switch (args.length) {
            case 1 -> OpenCreative.getPlanetsManager().getPlanets().stream()
                    .map(planet -> planet.getInformation().getCustomID())
                    .toList();
            case 2 -> sender.hasPermission("opencreative.join.others")
                    ? Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
                    : Collections.emptyList();
            default -> Collections.emptyList();
        };
    }
}
