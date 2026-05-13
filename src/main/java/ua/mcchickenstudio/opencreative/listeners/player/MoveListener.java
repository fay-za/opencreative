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

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.ChunkLoadEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.ChunkUnloadEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateSigns;

public final class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getY() < 0 && isEntityInDevPlanet(player)) {
            player.setVelocity(new Vector(0, 0.6f * Math.ceil(Math.abs(player.getY())), 0));
            if (player.getLocation().add(0, 1.9d, -0.8).getBlock().isSolid()) {
                player.teleport(player.getLocation().add(0, 2.5, 1));
            }
        }
        if (isBlockChanged(event.getFrom(), event.getTo())) {
            new ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.PlayerMoveEvent(event.getPlayer(), event).callEvent();
            if (isEntityInDevPlanet(player)) {
                translateSigns(player, 10);
                if (player.getY() < -20) {
                    player.teleport(player.getWorld().getSpawnLocation());
                }
            }
            if (isOutOfBorders(event.getTo())) {
                if (isOutOfBorders(event.getFrom())) {
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    player.teleport(event.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            new JumpEvent(event.getPlayer(), event).callEvent();
        }
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            new StartSneakingEvent(event.getPlayer(), event).callEvent();
        } else {
            new StopSneakingEvent(event.getPlayer(), event).callEvent();
        }
    }

    @EventHandler
    public void onFlying(PlayerToggleFlightEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            if (event.isFlying()) {
                new StartFlyingEvent(event.getPlayer(), event).callEvent();
            } else {
                new StopFlyingEvent(event.getPlayer(), event).callEvent();
            }
        }
    }

    @EventHandler
    public void onSprinting(PlayerToggleSprintEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            if (event.isSprinting()) {
                new StartRunningEvent(event.getPlayer()).callEvent();
            } else {
                new StopRunningEvent(event.getPlayer()).callEvent();
            }
        }
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            new ChunkLoadEvent(event.getPlayer(), event).callEvent();
        }
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            if (!planet.getTerritory().isBusy()) {
                new ChunkUnloadEvent(event.getPlayer(), event).callEvent();
            }
        }
    }

    private boolean isBlockChanged(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}
