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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;

public final class BucketListener implements Listener {

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet != null) {
            event.setCancelled(true);
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isPlacingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.placing-blocks.bypass")) {
            event.setCancelled(true);
            player.sendMessage(getLocaleComponent("not-for-lobby"));
        } else if (event.getBucket() == Material.LAVA_BUCKET) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) return;
            if (!planet.isOwner(event.getPlayer())) {
                OpenCreative.getWander(event.getPlayer()).getGriefStats().addLavaPlacementsAmount(1);
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isDestroyingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getLocaleMessage("not-for-lobby"));
        }
    }

}
