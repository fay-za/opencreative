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

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.TeleportEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isPlanet;

public final class TeleportListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY
                || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        ) {
            if (!isPlanet(event.getTo().getWorld())) {
                event.setCancelled(true);
            }
        }
        if (isOutOfBorders(event.getTo())) {
            event.setCancelled(true);
        }
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            switch (event.getCause()) {
                case UNKNOWN, PLUGIN, COMMAND -> {}
                default -> {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getFrom().getWorld());
        if (planet != null) {
            if (event.getTo().getWorld().equals(event.getFrom().getWorld())) {
                new TeleportEvent(event.getPlayer()).callEvent();
            }
        }
    }

    @EventHandler
    public void onPortalTeleport(PlayerPortalEvent event) {
        event.setCancelled(true);
        event.setCanCreatePortal(false);
    }

    @EventHandler
    public void onPortalTeleport(EntityPortalEvent event) {
        event.setCancelled(true);
        event.setCanCreatePortal(false);
    }

    @EventHandler
    public void onPortalTeleport(EntityPortalEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalTeleport(EntityInsideBlockEvent event) {
        if (event.getBlock().getType() == Material.END_PORTAL || event.getBlock().getType() == Material.END_GATEWAY) {
            event.setCancelled(true);
        }
    }

}
