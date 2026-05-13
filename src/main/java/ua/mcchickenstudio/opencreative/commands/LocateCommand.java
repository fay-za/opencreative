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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>LocateCommand</h1>
 * This command is responsible for finding online player's current world.
 * It allows players to locate each other and join friends.
 * <p>
 * Available: For all players.
 */
public class LocateCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("commands.locate.help"));
            return;
        }

        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
        }

        String nickname = args[0];
        Player player = Bukkit.getPlayer(nickname);

        if (player == null) {
            sender.sendMessage(getLocaleMessage("commands.locate.offline"));
            return;
        }

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);

        if (planet == null) {
            sender.sendMessage(getLocaleMessage("commands.locate.offline"));
            return;
        }

        sendLocateMessage(sender, player, planet);
    }

    private void sendLocateMessage(CommandSender sender, Player player, Planet planet) {
        String locateMessage = parsePlanetLines(planet, parsePAPI(player,
                getLocaleMessage("commands.locate.found").replace("%player%", player.getName())));
        String hoverText = parsePlanetLines(planet, getLocaleMessage("advertisement.hover"));
        String clickCommand = "/ad " + planet.getId();

        Component messageComponent = toComponent(locateMessage);
        Component hoverComponent = toComponent(hoverText);

        Component message = messageComponent
                .hoverEvent(HoverEvent.showText(hoverComponent))
                .clickEvent(ClickEvent.runCommand(clickCommand));

        sender.sendMessage(message);
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length == 1) {
            return new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return null;

    }
}
