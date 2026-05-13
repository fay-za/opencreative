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
 * Called when planet's sharing mode will be changed.
 * <p>
 * If a Planet Sharing Change event is cancelled, it will not change planet's sharing.
 * <p>
 * <b>NOTE:</b> It's not recommended to cancel event, when the {@link Cause} is WORLD,
 * because this cause is required to prevent world owner from joining the world while
 * it's being deleted. World can be unloaded at the moment of sharing mode change.
 */
public class PlanetSharingChangeEvent extends PlanetEvent implements Cancellable {

    private final Player player;
    private final Planet.Sharing oldSharing;
    private final Planet.Sharing newSharing;
    private final Cause cause;
    private boolean cancel;

    public PlanetSharingChangeEvent(@NotNull Planet planet, Planet.Sharing oldSharing, Planet.Sharing newSharing) {
        super(planet);
        this.oldSharing = oldSharing;
        this.newSharing = newSharing;
        this.player = null;
        this.cause = Cause.WORLD;
    }

    public PlanetSharingChangeEvent(@NotNull Planet planet, Planet.Sharing oldSharing, Planet.Sharing newSharing, Player player) {
        super(planet);
        this.oldSharing = oldSharing;
        this.newSharing = newSharing;
        this.player = player;
        this.cause = Cause.PLAYER;
    }

    public Planet.Sharing getOldSharing() {
        return oldSharing;
    }

    public Planet.Sharing getNewSharing() {
        return newSharing;
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

    public Cause getCause() {
        return cause;
    }

    public enum Cause {
        PLAYER,
        WORLD,
    }
}
