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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.LikeEvent;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.addPlayerInPlanetList;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlayersFromPlanetList;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.convertTime;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>LikeCommand</h1>
 * This command allows players to rate current world
 * as good one and increase world's reputation.
 * <p>
 * If economy is set up, then world's owner can
 * get server's virtual currency money.
 * <p>
 * Available: For all world players.
 */
public class LikeCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
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
            if (getPlayersFromPlanetList(planet, Planet.PlayersType.LIKED).contains(sender.getName())) {
                sender.sendMessage(getLocaleMessage("world.already-rated"));
            } else if (getPlayersFromPlanetList(planet, Planet.PlayersType.DISLIKED).contains(sender.getName())) {
                sender.sendMessage(getLocaleMessage("world.already-rated"));
            } else {
                if (addPlayerInPlanetList(planet, sender.getName(), Planet.PlayersType.LIKED)) {
                    Sounds.WORLD_LIKED.play(player);
                    planet.getInformation().setPlanetReputation(planet.getInformation().getReputation() + 1);
                    new LikeEvent(player).callEvent();
                    if (planet.getFlagValue(PlanetFlags.PlanetFlag.LIKE_MESSAGES) == 1) {
                        for (Player p : planet.getPlayers()) {
                            p.sendMessage(getLocaleMessage("world.liked").replace("%player%", sender.getName()));
                        }
                    }
                    if (OpenCreative.getEconomy().isEnabled() && !planet.isOwner(player)) {
                        OpenCreative.getEconomy().depositMoney(Bukkit.getOfflinePlayer(planet.getOwner()), planet.getGroup().getLikeReward());
                    }
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
