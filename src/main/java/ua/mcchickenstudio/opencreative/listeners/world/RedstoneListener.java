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

package ua.mcchickenstudio.opencreative.listeners.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LimitReachedRedstoneEvent;
import ua.mcchickenstudio.opencreative.indev.messages.PlaceholderReplacer;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.sendMessageOnce;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public final class RedstoneListener implements Listener {

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        if (isDevPlanet(event.getBlock().getWorld())) {
            event.setNewCurrent(event.getOldCurrent());
            return;
        }
        Location location = event.getBlock().getLocation();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(location.getWorld());
        if (planet != null) {
            planet.getLimits().setLastRedstoneOperationsAmount(planet.getLimits().getLastRedstoneOperationsAmount() + 1);
            if (planet.getLimits().getLastRedstoneOperationsAmount() > planet.getLimits().getRedstoneOperationsLimit()) {
                sendMessageOnce(planet, "world.redstone-limit",
                        new PlaceholderReplacer("count", planet.getLimits().getRedstoneOperationsLimit()),
                        null, null, 5);
                if (location.getBlock().getType() == Material.OBSERVER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            location.getBlock().setType(Material.AIR);
                        }
                    }.runTaskLater(OpenCreative.getPlugin(), 1L);
                } else {
                    location.getBlock().setType(Material.CAVE_AIR);
                }
                planet.getLimits().setLastRedstoneOperationsAmount(0);
                new LimitReachedRedstoneEvent(planet).callEvent();
            } else {
                new ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockRedstoneEvent(planet, event).callEvent();
            }
            if (planet.getLimits().getLastRedstoneOperationsAmount() > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        planet.getLimits().setLastRedstoneOperationsAmount(planet.getLimits().getLastRedstoneOperationsAmount() - 1);
                    }
                }.runTaskLater(OpenCreative.getPlugin(), 5L);
            }
        }

    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        if (isDevPlanet(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

}
