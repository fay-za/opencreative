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

package ua.mcchickenstudio.opencreative.commands.world.reputation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.convertTime;

/**
 * <h1>DislikeCommand</h1>
 * This command allows players to rate current world
 * as bad one and decrease world's reputation.
 * <p>
 * Available: For all world players.
 */
public class DislikeCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(MessageUtils.getLocaleMessage("only-in-world"));
                return;
            }

            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            long createdSeconds = (System.currentTimeMillis() - planet.getCreationTime()) / 1000;
            if (OpenCreative.getSettings().getRequirements().getWorldReputationMinSeconds() > createdSeconds) {
                Sounds.PLAYER_CANCEL.play(player);
                long unlockTime = (OpenCreative.getSettings().getRequirements().getWorldReputationMinSeconds() - createdSeconds) * 1000;
                player.sendMessage(MessageUtils.getPlayerLocaleMessage("world.cant-rate", player).replace("%time%",
                        convertTime(unlockTime)));
                return;
            }
            if (FileUtils.getPlayersFromPlanetList(planet, Planet.PlayersType.LIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else if (FileUtils.getPlayersFromPlanetList(planet, Planet.PlayersType.DISLIKED).contains(sender.getName())) {
                sender.sendMessage(MessageUtils.getLocaleMessage("world.already-rated"));
            } else {
                if (FileUtils.addPlayerInPlanetList(planet, sender.getName(), Planet.PlayersType.DISLIKED)) {
                    planet.getInformation().setPlanetReputation(planet.getInformation().getReputation() - 1);
                    Sounds.WORLD_DISLIKED.play(player);
                    sender.sendMessage(MessageUtils.getPlayerLocaleMessage("world.disliked", player));
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
