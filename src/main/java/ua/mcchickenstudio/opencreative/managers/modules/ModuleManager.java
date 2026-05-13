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

package ua.mcchickenstudio.opencreative.managers.modules;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;

import java.util.Set;
import java.util.UUID;

/**
 * <h1>ModuleManager</h1>
 * This interface represents a module manager,
 * that registers, creates and deletes modules.
 */
public interface ModuleManager extends Manager {

    /**
     * Register a module to base, so it will be visible
     * in modules browser menu.
     *
     * @param module module to register
     */
    void registerModule(@NotNull Module module);

    /**
     * Creates a new module and registers it in base.
     *
     * @param owner     owner of module.
     * @param devPlanet dev planet to parse executor locations.
     * @param locations set of location, that contains executors.
     */
    void createModule(@NotNull Player owner, @NotNull DevPlanet devPlanet, @NotNull Set<Location> locations);

    /**
     * Deletes module from files and base.
     *
     * @param module module to delete.
     */
    void deleteModule(@NotNull Module module);

    /**
     * Returns modules, that were made by player with
     * specified unique ID.
     *
     * @param uuid unique id of player.
     * @return set of player created modules.
     */
    @NotNull Set<Module> getPlayerModules(@NotNull UUID uuid);

    /**
     * Returns set of all registered modules in base.
     *
     * @return set of modules.
     */
    @NotNull Set<Module> getModules();

    /**
     * Returns module by its ID.
     *
     * @param id id to get module.
     * @return module - if found, or null - not exists.
     */
    @Nullable Module getModuleById(@NotNull String id);

}
