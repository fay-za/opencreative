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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class PlanetRunnable extends BukkitRunnable {

    protected final Planet planet;

    protected PlanetRunnable(Planet planet) {
        this.planet = planet;
    }

    @Override
    public final void run() {
        planet.getTerritory().removeBukkitRunnable(this);
        execute();
    }

    public abstract void execute();

}
