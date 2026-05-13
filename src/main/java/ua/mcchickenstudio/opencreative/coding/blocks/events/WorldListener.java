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

package ua.mcchickenstudio.opencreative.coding.blocks.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.PlanetExecutors;
import ua.mcchickenstudio.opencreative.planets.Planet;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isPlanet;

/**
 * <h1>WorldListener</h1>
 * This class represents listener of all Creative events in planet.
 * It activates executors on events listening.
 */
public class WorldListener implements EventExecutor, Listener {

    public void registerExecutors() {
        try {
            Bukkit.getPluginManager().registerEvent(
                    WorldEvent.class,
                    this, EventPriority.NORMAL, this,
                    OpenCreative.getPlugin());
        } catch (Exception error) {
            sendCriticalErrorMessage("Cannot register world events listener: planets code will not work.", error);
        }
    }

    /**
     * Checks if world event can activate executors or not.
     * World event must:
     * <p>
     * <ul>
     * <li>Be in planet's build world
     * <li>Have entities selection in planet's build world
     * <li>Happen while planet is in play mode
     * <li>Happen while planet is loaded
     * </ul>
     *
     * @param worldEvent event to check.
     * @return true - if possible, false - disallowed.
     */
    public boolean canActivate(@NotNull WorldEvent worldEvent) {
        if (worldEvent.getPlanet() == null) return false;
        if (worldEvent.getWorld() == null) return false;
        if (!isPlanet(worldEvent.getWorld())) return false;
        if (isDevPlanet(worldEvent.getWorld())) return false;
        if (!worldEvent.getPlanet().isLoaded()) return false;
        if (worldEvent.getPlanet().getMode() != Planet.Mode.PLAYING) return false;
        if (!worldEvent.getSelection().isEmpty()) {
            for (Entity entity : worldEvent.getSelection()) {
                if (!entity.getWorld().equals(worldEvent.getPlanet().getWorld())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (event instanceof WorldEvent worldEvent) {
            if (canActivate(worldEvent)) {
                PlanetExecutors.activate(worldEvent);
            }
        }
    }

}
