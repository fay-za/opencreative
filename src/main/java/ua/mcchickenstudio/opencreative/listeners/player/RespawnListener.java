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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerTotemRespawnEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.items.ItemsGroup;

public final class RespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!DeathListener.deathLocations.containsKey(event.getPlayer().getUniqueId())) return;
        Location deathLocation = DeathListener.deathLocations.get(player.getUniqueId());
        event.setRespawnLocation(deathLocation);
        DeathListener.deathLocations.remove(player.getUniqueId());
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) {
            Sounds.PLAYER_RESPAWN.play(player);
            if (planet.isOwner(player)) {
                if (planet.getMode() == Planet.Mode.BUILD) {
                    ItemsGroup.BUILD_OWNER.setItemsIfAbsent(player);
                } else {
                    ItemsGroup.PLAY_OWNER.setItemsIfAbsent(player);
                }
            }
            Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                planet.getTerritory().showBorders(player);
            }, 1L);
            new ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerRespawnEvent(player).callEvent();
        }
    }

    @EventHandler
    public void onTotemUsing(EntityResurrectEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld((event.getEntity().getWorld()));
        if (event.getEntity() instanceof Player player) {
            if (planet != null) {
                new PlayerTotemRespawnEvent(player).callEvent();
            }
        }

    }
}
