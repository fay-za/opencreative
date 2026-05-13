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
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>GamemodeCommand</h1>
 * This command is responsible for changing player's gamemode.
 * <p>
 * Using this command from console will redirect to Minecraft command.
 * <p>
 * Available: For world builders or developers.
 */
public class GamemodeCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:gamemode command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:gamemode " + String.join(" ", args));
            return;
        }

        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

        if (!player.hasPermission("opencreative.game-mode.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't change his game mode.
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
             * Players should not change game mode in developer world,
             * because it's work depends on game mode.
             */
            if (OpenCreative.getPlanetsManager().getDevPlanet(player) != null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return;
            }
        }
        if (args.length == 1) {
            /*
             * Example: /gamemode survival
             */
            GameMode mode = null;
            switch (args[0]) {
                case "0" -> mode = GameMode.SURVIVAL;
                case "1" -> mode = GameMode.CREATIVE;
                case "2" -> mode = GameMode.ADVENTURE;
                case "3" -> mode = GameMode.SPECTATOR;
            }
            try {
                if (mode == null) {
                    mode = GameMode.valueOf(args[0].toUpperCase());
                }
                player.setGameMode(mode);
                player.sendMessage(getLocaleMessage("commands.game-mode.changed." + mode.name().toLowerCase()));
            } catch (IllegalArgumentException error) {
                player.sendMessage(getLocaleMessage("commands.game-mode.wrong"));
            }

        } else if (args.length == 2) {
            /*
             * Example: /gamemode survival PlayerName
             */
            GameMode mode = null;
            switch (args[0]) {
                case "0" -> mode = GameMode.SURVIVAL;
                case "1" -> mode = GameMode.CREATIVE;
                case "2" -> mode = GameMode.ADVENTURE;
                case "3" -> mode = GameMode.SPECTATOR;
            }
            try {
                if (mode == null) {
                    mode = GameMode.valueOf(args[0].toUpperCase());
                }
                Player modePlayer = Bukkit.getPlayer(args[1]);
                if (modePlayer == null) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                } else {
                    /*
                     * Check player's, that will receive new game mode, world.
                     * If players' world is not same as sender's world game mode
                     * will be not changed.
                     */
                    Planet modePlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(modePlayer);
                    if (!player.hasPermission("opencreative.game-mode.bypass")) {
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.equals(modePlanet)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return;
                        }
                        if (OpenCreative.getPlanetsManager().getDevPlanet(modePlayer) != null) {
                            player.sendMessage(getLocaleMessage("only-in-world"));
                            return;
                        }
                    }
                }
                modePlayer.setGameMode(mode);
                player.sendMessage(getLocaleMessage("commands.game-mode.changed-player." + mode.name().toLowerCase())
                        .replace("%player%", modePlayer.getName()));
                modePlayer.sendMessage(getLocaleMessage("commands.game-mode.changed." + mode.name().toLowerCase()));
            } catch (IllegalArgumentException e) {
                player.sendMessage(getLocaleMessage("commands.game-mode.wrong"));
            }
        } else {
            sender.sendMessage(getLocaleMessage("commands.game-mode.help"));
        }
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 3) return null;
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(Arrays.stream(GameMode.values()).map(gameMode -> gameMode.name().toLowerCase()).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            }
        }
        return tabCompleter;
    }

}
