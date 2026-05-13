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

package ua.mcchickenstudio.opencreative.commands.world.modes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.JoinEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.PlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.items.ItemsGroup;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>PlayCommand</h1>
 * This command is responsible for changing current world's mode
 * to play mode. If it's already set, it can teleport player to
 * spawn location and load code.
 * <p>
 * Available: For world developers.
 */
public class PlayCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }

        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

        if (player.isDead()) {
            sender.sendMessage(getLocaleMessage("only-alive"));
            return;
        }

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }

        if (!OpenCreative.getSettings().getCodingSettings().isEnabled()) {
            player.sendMessage(getLocaleMessage("world.dev-mode.disabled"));
            return;
        }

        DevPlanet playerDevPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (playerDevPlanet != null) {
            playerDevPlanet.getLastLocations().put(player.getUniqueId(), player.getLocation());
        }

        removePlayerWithLocation(player);
        if (planet.getMode() != Planet.Mode.PLAYING) {
            // Build mode
            if (planet.getWorldPlayers().canDevelop(player) || player.hasPermission("opencreative.world.play.switch")) {
                PlanetModeChangeEvent event = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.PLAYING, player);
                event.callEvent();
                if (event.isCancelled()) {
                    return;
                }
                if (!OpenCreative.getStability().isFine()) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                planet.setMode(Planet.Mode.PLAYING);
                if (isEntityInDevPlanet(player)) {
                    afterCompilation(player, playerDevPlanet, false);
                }
            } else {
                sender.sendMessage(getPlayerLocaleMessage("not-owner", player));
            }
            return;
        }
        // Play mode
        if (!(new PlayEvent(player).callEvent() || planet.getWorldPlayers().canDevelop(player))) {
            return;
        }
        if (planet.getWorldPlayers().canDevelop(player)) {
            if (!OpenCreative.getStability().isFine()) {
                player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                Sounds.PLAYER_FAIL.play(player);
            } else {
                player.sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                if (!Arrays.asList(args).contains("--no-compile")) {
                    if (planet.getDevPlanet().isLoaded()) {
                        if (planet.getDevPlanet().isCodeChanged() && planet.getDevPlanet().isCurrentlySavingCode()) {
                            player.sendMessage(getLocaleMessage("world.dev-mode.already-saving-code"));
                        } else {
                            CompletableFuture<Boolean> parserResult;
                            if (Arrays.asList(args).contains("recompile")) {
                                if (sender.hasPermission("opencreative.world.recompile")) {
                                    parserResult = new CodingBlockParser(planet.getDevPlanet())
                                            .recompileCode(planet.getDevPlanet());
                                } else {
                                    parserResult = new CodingBlockParser(planet.getDevPlanet())
                                            .parseCode(planet.getDevPlanet());
                                    player.sendMessage(getLocaleMessage("no-perms"));
                                }
                            } else {
                                parserResult = new CodingBlockParser(planet.getDevPlanet())
                                        .parseCode(planet.getDevPlanet());
                            }
                            parserResult.thenAccept(success -> {
                                if (success) {
                                    afterCompilation(player, playerDevPlanet, true);
                                }
                            });
                            return;
                        }
                    } else {
                        planet.getTerritory().getScript().loadCode();
                    }
                }
            }
        } else {
            player.sendMessage(getLocaleMessage("world.play-mode.message.players"));
        }
        afterCompilation(player, playerDevPlanet, true);
    }

    private void afterCompilation(@NotNull Player player, @Nullable DevPlanet devPlanet, boolean executeEvents) {
        Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
            if (!player.isOnline()) return;
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) return;
            DevPlanet current = OpenCreative.getPlanetsManager().getDevPlanet(player);
            if (devPlanet != null && !devPlanet.equals(current)) {
                return;
            }
            if (devPlanet == null) {
                givePlayPermissions(player);
                new QuitEvent(player).callEvent();
            }
            clearPlayer(player, false);
            player.teleportAsync(planet.getTerritory().getSpawnLocation()).thenAccept(success -> {
                if (success) {
                    planet.getTerritory().showBorders(player);
                    if (planet.isOwner(player)) {
                        ItemsGroup.PLAY_OWNER.setItems(player);
                    }
                    if (executeEvents) new JoinEvent(player).callEvent();
                }
            });
        });
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (planet.getMode() == Planet.Mode.PLAYING && planet.getWorldPlayers().canDevelop(player) && args.length <= 1) {
            return List.of("--no-compile");
        }
        return null;
    }
}
