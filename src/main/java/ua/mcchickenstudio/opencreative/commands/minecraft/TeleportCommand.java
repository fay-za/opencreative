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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.clearPlayer;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.removePassengers;

/**
 * <h1>TeleportCommand</h1>
 * This command is responsible for teleporting player to specified coordinates.
 * <p>
 * Using this command from console will redirect to Minecraft command.
 * <p>
 * Available: For world builders or developers.
 */
public class TeleportCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:tp command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:tp " + String.join(" ", args));
            return;
        }
        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

        if (!player.hasPermission("opencreative.teleport.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't teleport.
             */
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return;
            }
            if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
                player.sendMessage(getLocaleMessage("not-owner"));
                return;
            }
            /*
             * Players should not teleport in developer world,
             * because it's work depends on game mode.
             */
            if (OpenCreative.getPlanetsManager().getDevPlanet(player) != null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return;
            }
        }
        if (args.length == 1) {
            /*
             * Example: /tp PlayerName
             */
            Player teleportToPlayer = Bukkit.getPlayer(args[0]);
            if (teleportToPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return;
            }
            Planet teleportPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(teleportToPlayer);
            if (!player.hasPermission("opencreative.teleport.bypass")) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(teleportPlanet)) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                }
                if (OpenCreative.getPlanetsManager().getDevPlanet(teleportToPlayer) != null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
            }
            if (!player.hasPermission("opencreative.teleport.clear-bypass")) {
                if (!player.getWorld().equals(teleportToPlayer.getWorld())) {
                    clearPlayer(player);
                }
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(teleportPlanet)) {
                    if (teleportPlanet != null) {
                        teleportPlanet.connectPlayer(player);
                    } else {
                        removePassengers(player);
                        player.teleport(teleportToPlayer.getLocation());
                    }
                } else {
                    removePassengers(player);
                    player.teleport(teleportToPlayer.getLocation());
                }
            } else {
                player.teleport(teleportToPlayer.getLocation());
            }
            player.sendMessage(getLocaleMessage("commands.teleport.teleported")
                    .replace("%player%", teleportToPlayer.getName()));
            Sounds.PLAYER_TELEPORT.play(player);
            if (!player.getWorld().equals(teleportToPlayer.getWorld()) && !player.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(player);
            }
        } else if (args.length == 2) {
            /*
             * Example: /tp FirstPlayer SecondPlayer
             */
            Player firstPlayer = Bukkit.getPlayer(args[0]);
            if (firstPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return;
            }
            Player secondPlayer = Bukkit.getPlayer(args[1]);
            if (secondPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return;
            }
            Planet firstPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(firstPlayer);
            Planet secondPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(secondPlayer);
            if (!player.hasPermission("opencreative.teleport.others-bypass")) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(firstPlanet) || !planet.equals(secondPlanet) || !firstPlanet.equals(secondPlanet)) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                }
                if (OpenCreative.getPlanetsManager().getDevPlanet(firstPlayer) != null || OpenCreative.getPlanetsManager().getDevPlanet(secondPlayer) != null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
            }
            if (!firstPlayer.getWorld().equals(secondPlayer.getWorld()) && !player.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(firstPlayer);
            }
            removePassengers(firstPlayer);
            firstPlayer.teleport(secondPlayer.getLocation());
            player.sendMessage(getLocaleMessage("commands.teleport.teleported-player")
                    .replace("%first%", firstPlayer.getName())
                    .replace("%second%", secondPlayer.getName()));
            firstPlayer.sendMessage(getLocaleMessage("commands.teleport.teleported")
                    .replace("%player%", secondPlayer.getName()));
            Sounds.PLAYER_TELEPORT.play(firstPlayer);
            if (!firstPlayer.getWorld().equals(secondPlayer.getWorld()) && !firstPlayer.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(firstPlayer);
            }
        } else if (args.length >= 3) {
            /*
             * Example: /tp 30 4 30
             */
            double x, y, z;
            float yaw, pitch;
            Location location = player.getLocation();
            yaw = location.getYaw();
            pitch = location.getPitch();
            try {
                x = parseCoordinate(args[0], location.getX());
                y = parseCoordinate(args[1], location.getY());
                z = parseCoordinate(args[2], location.getZ());
                if (args.length >= 4) {
                    yaw = parseCoordinate(args[3], location.getYaw());
                }
                if (args.length >= 5) {
                    pitch = parseCoordinate(args[3], location.getPitch());
                }
                Location newLocation = new Location(location.getWorld(), x, y, z, yaw, pitch);
                if (!isOutOfBorders(newLocation)) {
                    removePassengers(player);
                    player.teleport(newLocation);
                    player.sendMessage(getLocaleMessage("commands.teleport.teleported-coords")
                            .replace("%x%", String.valueOf(Math.round(x)))
                            .replace("%y%", String.valueOf(Math.round(y)))
                            .replace("%z%", String.valueOf(Math.round(z)))
                            .replace("%yaw%", String.valueOf(Math.round(yaw)))
                            .replace("%pitch%", String.valueOf(Math.round(pitch)))
                    );
                    Sounds.PLAYER_TELEPORT.play(player);
                } else {
                    sender.sendMessage(getLocaleMessage("commands.teleport.out-of-borders"));
                }
            } catch (NumberFormatException exception) {
                sender.sendMessage(getLocaleMessage("commands.teleport.help"));
            }
        } else {
            sender.sendMessage(getLocaleMessage("commands.teleport.help"));
        }
    }

    private double parseCoordinate(String arg, double current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Double.parseDouble(arg.substring(1));
        } else {
            return Double.parseDouble(arg);
        }
    }

    private float parseCoordinate(String arg, float current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Float.parseFloat(arg.substring(1));
        } else {
            return Float.parseFloat(arg);
        }
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 3) return null;
        if (!(sender instanceof Player player))
            return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        return player.getWorld().getPlayers().stream().map(Player::getName).toList();
    }

}
