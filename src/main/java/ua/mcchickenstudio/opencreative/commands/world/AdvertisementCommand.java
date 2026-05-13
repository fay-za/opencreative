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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.AdvertisedEvent;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.events.planet.PlanetAdvertisementEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetInviteEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.commands.world.JoinCommand.findPlanet;
import static ua.mcchickenstudio.opencreative.commands.world.JoinCommand.handlePlayerConnection;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>AdvertisementCommand</h1>
 * This command is used to invite all players from server
 * to specific world. Or, it can be used as alias of
 * {@link JoinCommand}.
 * <p>
 * Available: For all players.
 */
public class AdvertisementCommand extends CommandHandler {

    public static void handlePlanetAdvertisement(Player player, Planet planet) {
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }

        if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
            return;
        }

        if (!(planet.getSharing() == Planet.Sharing.PUBLIC)) {
            player.sendMessage(getLocaleMessage("advertisement.closed-world"));
            return;
        }

        PlanetAdvertisementEvent event = new PlanetAdvertisementEvent(planet, player);
        event.callEvent();

        if (event.isCancelled()) return;

        if (OpenCreative.getEconomy().isEnabled()) {
            double playerBalance = OpenCreative.getEconomy().getBalance(player).doubleValue();
            double advertisementPrice = OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementPrice();

            if (playerBalance < advertisementPrice) {
                player.sendMessage(getPlayerLocaleMessage("advertisement.no-money", player)
                        .replace("%money%", String.valueOf(Math.round(advertisementPrice - playerBalance))));
                return;
            } else {
                OpenCreative.getEconomy().withdrawMoney(player, advertisementPrice);
            }
        }

        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);
        new AdvertisedEvent(player).callEvent();

        Component advertisementMessage = createAdvertisementMessage(player, planet);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(advertisementMessage);
        }
    }

    private static Component createAdvertisementMessage(Player player, Planet planet) {
        Component advertisement = getPlayerLocaleComponent("advertisement.message", player)
                .replaceText(TextReplacementConfig.builder()
                        .match("%world%")
                        .replacement(planet.getInformation().displayName()
                        ).build());
        Component hoverComponent = parsePlanetLines(planet, getLocaleComponent("advertisement.hover"));
        String clickCommand = "/ad " + planet.getId();

        advertisement = advertisement
                .hoverEvent(HoverEvent.showText(hoverComponent))
                .clickEvent(ClickEvent.runCommand(clickCommand));

        return advertisement;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }

        if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
            player.sendMessage(getLocaleMessage("maintenance"));
            return;
        }

        if (OpenCreative.getStability().isVeryBad() && !player.hasPermission("opencreative.stability.bypass")) {
            player.sendMessage(getLocaleMessage("creative.stability.cannot"));
            return;
        }

        switch (args.length) {
            case 0:  // /ad
                handlePlanetAdvertisement(player, OpenCreative.getPlanetsManager().getPlanetByPlayer(player));
                break;
            case 1:  // /ad [planet id]
                if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
                if (player.isDead()) {
                    player.sendMessage(getLocaleMessage("only-alive"));
                    return;
                }
                handlePlayerConnection(player, args[0]);
                break;
            case 2:  // /ad [planet id] [player]
                handlePlanetInvitation(player, args[0], args[1]);
                break;
        }

    }

    private void handlePlanetInvitation(Player player, String planetId, String inviteeName) {
        Player inviteReceiver = Bukkit.getPlayer(inviteeName);

        if (inviteReceiver == null) {
            player.sendMessage(getPlayerLocaleMessage("no-player-found", player));
            return;
        }

        if (player.equals(inviteReceiver)) {
            player.sendMessage(getPlayerLocaleMessage("same-world", player));
            return;
        }

        Planet foundPlanet = findPlanet(planetId);

        if (foundPlanet == null) {
            Sounds.PLAYER_FAIL.play(player);
            player.clearTitle();
            player.sendMessage(getPlayerLocaleMessage("no-planet-found", player));
            return;
        }

        if (foundPlanet.getSharing() != Planet.Sharing.PUBLIC) {
            player.sendMessage(getLocaleMessage("advertisement.closed-world"));
            return;
        }

        if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
            player.sendMessage(getLocaleMessage("advertisement.cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND))));
            return;
        }

        PlanetInviteEvent event = new PlanetInviteEvent(foundPlanet, player, inviteReceiver);
        event.callEvent();

        if (event.isCancelled()) return;

        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown(), CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND);

        Component advertisementMessage = createAdvertisementMessage(player, foundPlanet);
        inviteReceiver.sendMessage(advertisementMessage);
        player.sendMessage(advertisementMessage);
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return switch (args.length) {
            case 1 -> OpenCreative.getPlanetsManager().getPlanets().stream()
                    .map(planet -> planet.getInformation().getCustomID())
                    .filter(id -> id.startsWith(args[0]))
                    .toList();
            case 2 -> Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(args[1]))
                    .toList();
            default -> Collections.emptyList();
        };
    }
}

