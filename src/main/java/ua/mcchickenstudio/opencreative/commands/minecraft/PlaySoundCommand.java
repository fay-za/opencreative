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

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.settings.Sounds.SOUND_REGEX;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>PlaySoundCommand</h1>
 * This command is responsible for playing sounds for player.
 * <p>
 * Using this command from console will redirect to Minecraft command.
 * <p>
 * Available: For world builders or developers.
 */
public class PlaySoundCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender, "minecraft:playsound " + String.join(" ", args));
        } else {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            if (!player.hasPermission("opencreative.play-sound.bypass")) {
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
                sender.sendMessage(getLocaleMessage("commands.play-sound.help"));
                return;
            }

            Player target = player;
            String soundString;
            Sound sound = null;
            float volume = 100f;
            float pitch = 1f;
            Long seed = null;
            boolean isCustomTarget = false;

            if (args.length >= 2) {
                try {
                    // playsound entity.villager.yes 100 -> false, 0
                    // playsound playername entity.villager.yes -> true, 1
                    volume = Float.parseFloat(args[1]);
                } catch (NumberFormatException ignored) {
                    isCustomTarget = true;
                }
                if (isCustomTarget && args.length >= 3) {
                    try {
                        volume = Float.parseFloat(args[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                soundString = args[isCustomTarget ? 1 : 0];
            } else {
                soundString = args[0];
            }

            if (args.length >= 3) {
                // playsound entity.villager.yes 100 2
                try {
                    if (!(args.length == 3 && isCustomTarget)) {
                        pitch = Float.parseFloat(args[isCustomTarget ? 3 : 2]);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            if (args.length >= 4) {
                // playsound entity.villager.yes 100 2 seed
                try {
                    if (!(args.length == 4 && isCustomTarget)) {
                        seed = Long.parseLong(args[isCustomTarget ? 4 : 3]);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            if (isCustomTarget) target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(getLocaleMessage("no-player-found"));
                return;
            } else if (!sender.hasPermission("opencreative.play-sound.bypass")) {
                Planet targetPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(target);
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(targetPlanet)) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                }
            }

            if (!soundString.matches(SOUND_REGEX)) {
                player.sendMessage(getLocaleMessage("commands.play-sound.bad-sound")
                        .replace("%sound%", soundString));
                return;
            }

            try {
                sound = Sound.valueOf(soundString.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
            volume = Math.clamp(volume, 1, 100);
            pitch = Math.clamp(pitch, 0.1f, 2.0f);
            Location location = target.getLocation().clone();
            if (volume < 100) {
                location = location.add(0, -50, 0);
            }
            if (sound == null) {
                if (seed != null) {
                    target.playSound(location, soundString, SoundCategory.AMBIENT, volume, pitch, seed);
                } else {
                    target.playSound(location, soundString, volume, pitch);
                }
            } else {
                if (seed != null) {
                    target.playSound(location, sound, SoundCategory.AMBIENT, volume, pitch, seed);
                } else {
                    target.playSound(location, sound, volume, pitch);
                }
            }
            sender.sendMessage(getLocaleMessage("commands.play-sound.played").replace("%sound%", soundString).replace("%volume%", String.valueOf(volume)).replace("%pitch%", String.valueOf(pitch)).replace("%player%", target.getName()));
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(Registry.SOUNDS.stream().map(sound -> sound.getKey().asMinimalString()).toList());
            } else if (args.length == 3) {
                tabCompleter.add("100");
                tabCompleter.add("50");
                tabCompleter.add("20");
                tabCompleter.add("10");
            } else if (args.length == 4) {
                tabCompleter.add("1");
                tabCompleter.add("2");
                tabCompleter.add("0.1");
                tabCompleter.add("0.5");
            }
        }
        return tabCompleter;
    }
}
