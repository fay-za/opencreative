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

package ua.mcchickenstudio.opencreative.events.planet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * Called when player tries to advertise planet.
 * <p>
 * If a Planet Advertisement event is cancelled, the advertisement will not display for all players.
 * <p>
 * <b>NOTE:</b> Planet can be unloaded at the moment of advertisement.
 */
public class PlanetAdvertisementEvent extends PlanetEvent implements Cancellable {

    private final Player player;
    private boolean cancel;

    public PlanetAdvertisementEvent(@NotNull Planet planet, Player player) {
        super(planet);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
