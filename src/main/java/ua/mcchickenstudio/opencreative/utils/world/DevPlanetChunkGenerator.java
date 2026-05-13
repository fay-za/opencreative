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

package ua.mcchickenstudio.opencreative.utils.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * <h1>DevPlanetChunkGenerator</h1>
 * This class represents a ChunkGenerator, that
 * generates empty world without any terrain.
 */
public final class DevPlanetChunkGenerator extends ChunkGenerator {

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 2, 1, 2, 0, 0);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

}
