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

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.items.ItemsGroup;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.removePlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>BuildCommand</h1>
 * This command is responsible for changing current world's mode
 * to build mode. If it's already set, it can teleport player to
 * spawn location and give creative mode.
 * <p>
 * Available: For world builders.
 */
public class BuildCommand extends CommandHandler {

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


        if (args.length == 0) {
            removePlayerWithLocation(player);
            if (planet.getMode() != Planet.Mode.BUILD) {
                boolean canBuild = planet.getWorldPlayers().canBuild(player);
                if (canBuild || player.hasPermission("opencreative.world.build.switch")) {
                    Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                    if (!planet.getWorldPlayers().isTrustedBuilder(player) && !player.hasPermission("opencreative.world.build.switch")) {
                        if (planetOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                            return;
                        }
                        Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                        if (!(ownerPlanet == planet)) {
                            sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                            return;
                        }
                    }
                    PlanetModeChangeEvent event = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.BUILD, player);
                    event.callEvent();
                    if (event.isCancelled()) {
                        return;
                    }
                    planet.setMode(Planet.Mode.BUILD);
                    if (isEntityInDevPlanet(player)) {
                        clearPlayer(player);
                        player.teleport(planet.getTerritory().getSpawnLocation());
                        if (planet.isOwner(sender.getName())) {
                            player.getInventory().setItem(8, createItem(Material.COMPASS, 1, "items.developer.world-settings"));
                        }
                        if (canBuild) player.setGameMode(GameMode.CREATIVE);
                    }
                    planet.getTerritory().showBorders(player);
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                }
            } else {
                clearPlayer(player);
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.build-mode.title")), toComponent(getLocaleMessage("world.build-mode.subtitle")),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                player.teleport(planet.getTerritory().getSpawnLocation());
                Sounds.WORLD_MODE_BUILD.play(player);
                if (planet.getWorldPlayers().canBuild(player)) {
                    Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                    if (planet.getWorldPlayers().getBuildersNotTrusted().contains(sender.getName())) {
                        if (planetOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                            return;
                        }
                        Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                        if (!(ownerPlanet == planet)) {
                            sender.sendMessage(getLocaleMessage("world.build-mode.cant-build-when-offline"));
                            return;
                        }
                    }
                    if (planet.isOwner(sender.getName())) {
                        ItemsGroup.BUILD_OWNER.setItems(player);
                    }
                    player.setGameMode(GameMode.CREATIVE);
                    planet.getTerritory().showBorders(player);
                    giveBuildPermissions(player);
                    sender.sendMessage(getLocaleMessage("world.build-mode.message.owner"));
                    if (!planet.getTerritory().isAutoSave()) {
                        player.sendMessage(getLocaleMessage("settings.autosave.warning"));
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("world.build-mode.message.players"));
                }
            }
        } else {
            if (!planet.isOwner(sender.getName())) {
                sender.sendMessage(getLocaleMessage("not-owner"));
                return;
            }
            String nickname = args[0];
            Player onlinePlayer = Bukkit.getPlayer(nickname);
            if (!planet.getWorldPlayers().getAllBuilders().contains(nickname)) {
                if (onlinePlayer != null) {
                    nickname = onlinePlayer.getName();
                }
            }
            if (planet.isOwner(nickname)) {
                sender.sendMessage(getLocaleMessage("same-player"));
                return;
            }
            /*
             * Checks if player's name contains in not trusted
             * or trusted builders.
             */
            if (planet.getWorldPlayers().getBuildersNotTrusted().contains(nickname)) {
                planet.getWorldPlayers().addBuilder(nickname, true);
                sender.sendMessage(getLocaleMessage("world.players.builders.trusted").replace("%player%", nickname));
                return;
            }
            if (planet.getWorldPlayers().getBuildersTrusted().contains(nickname)) {
                planet.getWorldPlayers().removeBuilder(nickname);
                sender.sendMessage(getLocaleMessage("world.players.builders.removed").replace("%player%", nickname));
                return;
            }
            /*
             * Adds online player as not trusted builder, if he's not
             * listed in builders.
             */
            int limit = planet.getLimits().getBuildersLimit();
            if (planet.getWorldPlayers().getAllBuilders().size() > limit) {
                sender.sendMessage(getLocaleMessage("world.players.builders.limit").replace("%limit%", String.valueOf(limit)));
                return;
            }
            if (onlinePlayer != null) {
                Planet playerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(onlinePlayer);
                if (planet.equals(playerPlanet)) {
                    sender.sendMessage(getLocaleMessage("world.players.builders.added").replace("%player%", onlinePlayer.getName()));
                    planet.getWorldPlayers().addBuilder(onlinePlayer.getName(), false);
                } else {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                }
            } else {
                sender.sendMessage(getLocaleMessage("no-player-found"));
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (planet.isOwner(player)) {
            List<String> list = new ArrayList<>(planet.getWorldPlayers().getAllBuilders());
            for (Player planetPlayer : planet.getPlayers()) {
                if (planet.isOwner(planetPlayer) || list.contains(planetPlayer.getName())) continue;
                list.add(planetPlayer.getName());
            }
            return list.subList(0, Math.min(10, list.size()));
        }
        return null;
    }

}
