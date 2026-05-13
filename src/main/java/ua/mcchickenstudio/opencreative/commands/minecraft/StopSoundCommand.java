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
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>StopSoundCommand</h1>
 * This command is responsible for stopping sounds for player.
 * <p>
 * Using this command from console will redirect to Minecraft command.
 * <p>
 * Available: For world builders or developers.
 */
public class StopSoundCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender, "minecraft:stopsound " + String.join(" ", args));
        } else {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            if (!player.hasPermission("opencreative.stop-sound.bypass")) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
                if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player))) {
                    player.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
            }
            if (args.length == 0) {
                player.stopAllSounds();
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-all").replace("%player%", player.getName()));
                return;
            }
            Player target = player;
            String soundOrCategory;
            if (args.length == 1) {
                soundOrCategory = args[0].toUpperCase();
            } else if (args.length == 2) {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                } else if (!sender.hasPermission("opencreative.stop-sound.bypass")) {
                    Planet targetPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(target);
                    if (!player.hasPermission("opencreative.stop-sound.bypass")) {
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.equals(targetPlanet)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return;
                        }
                    }
                }
                soundOrCategory = args[1].toUpperCase();
            } else {
                sender.sendMessage(getLocaleMessage("commands.stop-sound.help"));
                return;
            }
            Sound sound = null;
            SoundCategory category = null;
            try {
                sound = Sound.valueOf(soundOrCategory);
            } catch (IllegalArgumentException ignored) {
            }
            try {
                category = SoundCategory.valueOf(soundOrCategory);
            } catch (IllegalArgumentException ignored) {
            }
            if (sound != null) {
                target.stopSound(sound);
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-sound").replace("%sound%", soundOrCategory).replace("%player%", target.getName()));
            } else if (category != null) {
                target.stopSound(category);
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-category").replace("%category%", soundOrCategory).replace("%player%", target.getName()));
            } else {
                target.stopAllSounds();
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-all").replace("%player%", target.getName()));
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Arrays.stream(SoundCategory.values()).map(c -> c.name().toLowerCase()).toList();
    }
}
