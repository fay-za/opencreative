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
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * Called when planet's mode will be changed.
 * <p>
 * If a Planet Mode Change event is cancelled, it will not change planet's mode.
 * <p>
 * <b>NOTE:</b> It's not recommended to cancel event, when the {@link Cause} is CODE,
 * because this cause is required to stop code running due to critical errors in code.
 * Plugins should check is player a cause of Planet Mode Change event.
 * Planet can be unloaded at the moment of mode change.
 */
public class PlanetModeChangeEvent extends PlanetEvent implements Cancellable {

    private final Player player;
    private final Planet.Mode oldMode;
    private final Planet.Mode newMode;
    private final Cause cause;
    private boolean cancel;

    public PlanetModeChangeEvent(@NotNull Planet planet, Planet.Mode oldMode, Planet.Mode newMode) {
        super(planet);
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.player = null;
        this.cause = Cause.CODE;
    }

    public PlanetModeChangeEvent(@NotNull Planet planet, Planet.Mode oldMode, Planet.Mode newMode, Player player) {
        super(planet);
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.player = player;
        this.cause = Cause.PLAYER;
    }

    @SuppressWarnings("unused")
    public @NotNull Planet.Mode getOldMode() {
        return oldMode;
    }

    @SuppressWarnings("unused")
    public @NotNull Planet.Mode getNewMode() {
        return newMode;
    }

    public @Nullable Player getPlayer() {
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

    public @NotNull Cause getCause() {
        return cause;
    }

    public enum Cause {
        PLAYER,
        CODE,
    }
}
