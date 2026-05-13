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

package ua.mcchickenstudio.opencreative.managers.space;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;
import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerator;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldTemplate;

import java.util.List;
import java.util.Set;

/**
 * <h1>PlanetsManager</h1>
 * This interface represents a planets manager,
 * that can manipulates with planets. It has methods
 * to get current planet by player, world and info.
 * It creates, registers and deletes planets.
 */
public interface PlanetsManager extends Manager {

    /**
     * Returns a set of all stable planets in base.
     *
     * @return set of planets.
     */
    @NotNull Set<Planet> getPlanets();

    /**
     * Returns a set of all corrupted planets in base.
     * They will be not displayed in worlds browser
     * menu or even in search orders.
     *
     * @return set of planets.
     */
    @NotNull Set<Planet> getCorruptedPlanets();

    /**
     * Returns a planet, that has same
     * original ID as specified one.
     *
     * @param id to get planet.
     * @return if exists - planet, else - null.
     */
    @Nullable Planet getPlanetById(@NotNull String id);

    /**
     * Returns a developers planet, where player
     * currently is connected.
     *
     * @param player to get dev planet.
     * @return if player is in dev planet - returns dev planet, else - null.
     */
    @Nullable DevPlanet getDevPlanet(@NotNull Player player);

    /**
     * Returns a developers planet, that has
     * same world as specified one.
     *
     * @param world to get dev planet.
     * @return if exists - dev planet, else - null.
     */
    @Nullable DevPlanet getDevPlanet(@NotNull World world);

    /**
     * Returns a planet, where player currently is connected.
     * Should return planet even if player is in dev planet.
     *
     * @param player to get planet.
     * @return if player is in planet - returns planet, else - null.
     */
    @Nullable Planet getPlanetByPlayer(@NotNull Player player);

    /**
     * Returns a planet, that has same
     * world as specified one.
     *
     * @param world to get planet.
     * @return if exists - planet, else - null.
     */
    @Nullable Planet getPlanetByWorld(@NotNull World world);

    /**
     * Returns a set of planets, that are owned
     * by specified player name.
     *
     * @param owner owner of planets.
     * @return set of player's created planets.
     */
    @NotNull Set<Planet> getPlanetsByOwner(@NotNull String owner);

    /**
     * Returns a set of planets, that are owned
     * by specified player.
     *
     * @param owner owner of planets.
     * @return set of player's created planets.
     */
    @NotNull Set<Planet> getPlanetsByOwner(@NotNull Player owner);

    /**
     * Returns a planet, that has same Minecraft
     * world name as specified one.
     *
     * @param name world name to get planet.
     * @return if exists - planet, else - null.
     */
    @Nullable Planet getPlanetByWorldName(@NotNull String name);

    /**
     * Returns a planet, that has same
     * numeric or custom ID as specified one.
     *
     * @param id to get planet.
     * @return if exists - planet, else - null.
     */
    @Nullable Planet getPlanetByAnyID(@NotNull String id);

    /**
     * Returns a planet, that has same
     * custom ID as specified one.
     *
     * @param id to get planet.
     * @return if exists - planet, else - null.
     */
    @Nullable Planet getPlanetByCustomID(@NotNull String id);

    /**
     * Returns a list of planets, that are
     * marked in config.yml as recommended.
     *
     * @return list of recommended planets.
     */
    @NotNull List<Planet> getRecommendedPlanets();

    /**
     * Returns a list of player's favorite worlds.
     *
     * @param wander player as wander.
     * @return set of player's favorite planets.
     */
    @NotNull Set<Planet> getFavoritePlanets(@NotNull OfflineWander wander);

    /**
     * Returns a set of planets, that contain
     * specified custom ID.
     *
     * @param id custom id.
     * @return set of planets with similar custom IDs.
     */
    @NotNull Set<Planet> getPlanetsContainingID(@NotNull String id);

    /**
     * Returns a set of planets, that contain
     * specified display name.
     *
     * @param name display name.
     * @return set of planets with similar display names.
     */
    @NotNull Set<Planet> getPlanetsContainingName(@NotNull String name);

    /**
     * Copies template world folder and creates a new planet for player.
     *
     * @param owner    owner of planet.
     * @param id       ID of planet.
     * @param template template of world.
     */
    void createPlanet(@NotNull Player owner, int id, @NotNull WorldTemplate template);

    /**
     * Creates and loads a new planet for player with specified world generator.
     *
     * @param owner     Owner of planet.
     * @param id        ID of planet.
     * @param generator Generator of world.
     */
    void createPlanet(@NotNull Player owner, int id, @NotNull WorldGenerator generator);

    /**
     * Creates and loads a new planet for player with specified world generator, environment, seed and generate sturctures option.
     *
     * @param owner              Owner of planet.
     * @param id                 ID of planet.
     * @param generator          Generator of world.
     * @param environment        Environment of world.
     * @param seed               Seed for generation.
     * @param generateStructures Generate or not generate structures.
     * @param biome              Biome for generation.
     */
    void createPlanet(@NotNull Player owner, int id, @NotNull WorldGenerator generator,
                      @NotNull World.Environment environment, long seed,
                      boolean generateStructures, @NotNull String biome);

    /**
     * Unregisters planet, teleports planet players to lobby,
     * unloads world and removes planet folders.
     *
     * @param planet planet to delete.
     */
    boolean deletePlanet(@NotNull Planet planet);

    /**
     * Registers planet to base, for example if plugin
     * found planet data while loading planets.
     *
     * @param planet planet to register.
     */
    void registerPlanet(@NotNull Planet planet);

    /**
     * Unregisters planet from base, so it will
     * be not displayed in worlds browser menu.
     *
     * @param planet planet to unregister.
     */
    void unregisterPlanet(@NotNull Planet planet);

    /**
     * Check if connection with database is stable.
     *
     * @return true - if connection is normal, false - not stable.
     */
    boolean isStableConnection();

}
