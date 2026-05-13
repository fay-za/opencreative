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

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>WorldEvent</h1>
 * This class represents event in Creative's planet.
 */
public abstract class WorldEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final World world;
    protected List<Entity> selection = new ArrayList<>();

    public WorldEvent(@NotNull Planet planet, @NotNull List<Entity> selection) {
        world = planet.getTerritory().getWorld();
        this.selection = selection;
    }

    public WorldEvent(@NotNull Planet planet) {
        world = planet.getTerritory().getWorld();
        if (world != null) {
            selection.addAll(world.getPlayers());
        }
    }

    public WorldEvent(@NotNull Planet planet, @NotNull Block block) {
        world = block.getWorld();
        if (planet.getTerritory().getWorld() != null) {
            selection.addAll(planet.getTerritory().getWorld().getPlayers());
        }
    }

    public WorldEvent(@NotNull Entity entity) {
        world = entity.getWorld();
        selection.add(entity);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public List<Entity> getSelection() {
        return selection;
    }

    public World getWorld() {
        return world;
    }

    public Planet getPlanet() {
        if (getWorld() == null) return null;
        return OpenCreative.getPlanetsManager().getPlanetByWorld(getWorld());
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
