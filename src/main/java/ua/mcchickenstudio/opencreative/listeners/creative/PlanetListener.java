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

package ua.mcchickenstudio.opencreative.listeners.creative;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiments;
import ua.mcchickenstudio.opencreative.events.planet.PlanetDisconnectPlayerEvent;
import ua.mcchickenstudio.opencreative.wanders.Wander;

public final class PlanetListener implements Listener {

    @EventHandler
    public void onDisconnect(PlanetDisconnectPlayerEvent event) {
        if (!event.getPlanet().isOwner(event.getPlayer())) {
            Wander wander = OpenCreative.getWander(event.getPlayer());
            if (wander.getGriefStats().isSuspicious() && OpenCreative.getSettings().shouldLogGriefStats()) {
                OpenCreative.getPlugin().getLogger().info("[GRIEF: "
                        + event.getPlanet().getId()
                        + "] Suspicious changes by "
                        + event.getPlayer().getName() + ": " + wander.getGriefStats().getAsString());
                wander.getGriefStats().clear();
            }
        }
        if (Experiments.isEnabled("wanders")) {
            OpenCreative.getWander(event.getPlayer()).setLastPlayedWorldId(event.getPlanet().getId());
        }
    }

}
