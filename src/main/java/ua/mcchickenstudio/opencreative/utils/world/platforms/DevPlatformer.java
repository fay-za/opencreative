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

package ua.mcchickenstudio.opencreative.utils.world.platforms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>DevPlatformer</h1>
 * This interface represents a platformer, that manipulates and
 * controls coding platforms in developer planets. It can create
 * platform, return a list of existing platforms, set world's border.
 * <p>
 * Platform must be plain formed.
 */
public abstract class DevPlatformer implements ExtensionContent {

    private final String id;

    /**
     * Constructor of coding platform generator.
     *
     * @param id short id of dev platformer that will be used in registry.
     *           <p>
     *           It must be lower-snake-cased, for example: "horizontal", "classic".
     *           If some of registered platformers has same ID as new, it will be not added.
     */
    public DevPlatformer(@NotNull String id) {
        this.id = id.toLowerCase();
    }

    /**
     * Returns id of platforms creator, that will be used
     * to find it in registry.
     *
     * @return id of dev platformer.
     */
    public final @NotNull String getID() {
        return id;
    }

    /**
     * Sets world border for developer planet.
     *
     * @param devPlanet dev planet to set border.
     */
    public abstract void setWorldBorder(@NotNull DevPlanet devPlanet);

    /**
     * Returns coding platform by location.
     *
     * @param location location to get platform.
     * @return coding platform - if location contains coding platform, otherwise - null.
     */
    public abstract @Nullable DevPlatform getPlatformInLocation(@NotNull DevPlanet devPlanet, @NotNull Location location);

    /**
     * Returns begin location of coding line by location.
     *
     * @param location location to get platform.
     * @return executor block location - if location is related to actions or its executor location itself.
     */
    public abstract @Nullable Location getColumnBeginLocation(@NotNull DevPlanet devPlanet, @NotNull Location location);

    /**
     * Returns the most far platform by X coordinate.
     *
     * @param devPlanet developer planet to get platform.
     * @return farthest platform by X, or coding platform placed at (1,1).
     */
    public abstract @NotNull DevPlatform getFarPlatformByX(@NotNull DevPlanet devPlanet);

    /**
     * Returns the most far platform by Z coordinate.
     *
     * @param devPlanet developer planet to get platform.
     * @return farthest platform by Z, or coding platform placed at (1,1).
     */
    public abstract @NotNull DevPlatform getFarPlatformByZ(@NotNull DevPlanet devPlanet);

    /**
     * Returns list of existing coding platforms, that
     * can be used to place coding blocks.
     *
     * @param devPlanet developer planet to get platforms.
     * @return list of developer platforms.
     */
    public abstract @NotNull List<@NotNull DevPlatform> getPlatforms(@NotNull DevPlanet devPlanet);

    /**
     * Claims coding platform, if it doesn't exist yet.
     *
     * @param devPlanet developer planet to claim platform.
     * @param platform  coding platform to claim.
     * @return true - claimed coding platform, false - already built and exists.
     */
    public abstract boolean claimPlatform(@NotNull DevPlanet devPlanet, @NotNull DevPlatform platform);

    /**
     * Builds coding platform by its coordinates.
     * Coordinates are not from Minecraft location.
     *
     * @param platform       platform to create.
     * @param floorMaterial  material of block, that will be used as floor.
     * @param eventMaterial  material of block, that will be placed as cells for event blocks placement.
     * @param actionMaterial material of block, that will be placed as cells for action blocks placement.
     * @return true - created coding platform, false - cannot build it.
     */
    public abstract @NotNull CompletableFuture<Void> buildPlatform(@NotNull DevPlatform platform, Material floorMaterial, Material eventMaterial, Material actionMaterial);

    /**
     * Returns location of top left corner of platform.
     *
     * @param platform dev platform to get location.
     * @return begin location of platform.
     */
    public abstract @NotNull Location getPlatformBeginLocation(@NotNull DevPlatform platform);

    /**
     * Returns location of bottom right corner of platform.
     *
     * @param platform dev platform to get location.
     * @return end location of platform.
     */
    public abstract @NotNull Location getPlatformEndLocation(@NotNull DevPlatform platform);


    /**
     * Returns next platform, that will be created by player.
     *
     * @return next available coding platform.
     */
    public abstract @NotNull DevPlatform getNextAvailablePlatform(@NotNull DevPlanet planet);

    /**
     * Returns maximum amount of coding blocks in 1 column.
     * Includes executor and all actions blocks through coding line.
     *
     * @return limit of coding blocks.
     */
    public abstract int getCodingBlocksLimit(@NotNull DevPlanet planet);

    /**
     * Checks if this manager creates platforms
     * on Z coordinate instead of Y coordinate.
     * Useful for skipping additional checks while
     * parsing coding blocks.
     *
     * @return true - considers only Z or X coordinates, false - considers also Y.
     */
    public abstract boolean notDependsOnHeight();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
