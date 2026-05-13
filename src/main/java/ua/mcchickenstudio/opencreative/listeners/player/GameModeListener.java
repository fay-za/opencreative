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

package ua.mcchickenstudio.opencreative.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.hidePlayerInTab;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;

public final class GameModeListener implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(OpenCreative.getPlugin(), () -> {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                // If player is not in planet
                if (isEntityInLobby(player) && event.getNewGameMode() == GameMode.CREATIVE && !player.hasPermission("opencreative.game-mode.change")) {
                    if (player.isConnected()) {
                        OpenCreative.getPlugin().getLogger().warning("Player " + player.getName() + " tried to get Creative mode in lobby, but he doesn't have permission.");
                    }
                    event.setCancelled(true);
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(onlinePlayer.getWorld())) {
                        hidePlayerInTab(onlinePlayer, player);
                        hidePlayerInTab(player, onlinePlayer);
                    }
                }
                return;
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.getWorld().equals(planet.getTerritory().getWorld()) && !onlinePlayer.getWorld().equals(planet.getDevPlanet().getWorld())) {
                    hidePlayerInTab(onlinePlayer, player);
                    hidePlayerInTab(player, onlinePlayer);
                }
            }
        }, 5L);
    }

}
