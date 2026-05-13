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

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.commands.ChatCommand;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetPlayer;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

/**
 * <h1>QuitListener</h1>
 * This class represents a listener for when a player
 * leaves the server. Responsible for clearing temporary
 * player data.
 */
public final class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.spigot().respawn();

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName(player.getWorld().getName().replace("dev", ""));
        if (planet != null) {
            new QuitEvent(player).callEvent();
            PlanetPlayer planetPlayer = planet.getWorldPlayers().getPlanetPlayer(player);
            if (planetPlayer != null) planetPlayer.save();
            planet.getWorldPlayers().unregisterPlayer(player);
            planet.getInformation().updateIconAsync();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (planet.getOnline() == 0 && !planet.getTerritory().isBusy() && OpenCreative.getPlugin().isEnabled()) {
                        planet.getTerritory().unload();
                    }
                }
            }.runTaskLater(OpenCreative.getPlugin(), 20L);

        }
        if (WorldUtils.isPlanet(player.getWorld())) {
            player.setGameMode(GameMode.ADVENTURE);
            teleportToLobby(player);
        }

        PlayerConfirmation.clearConfirmations(player);
        ChatCommand.creativeChatOff.remove(player);
        DeathListener.deathLocations.remove(player.getUniqueId());
        removeFromPermissionsMap(player);
        CooldownUtils.clearPlayerCooldowns(player);
        disableSpying(player);
        OpenCreative.getPlugin().unregisterWander(player);
    }

}
