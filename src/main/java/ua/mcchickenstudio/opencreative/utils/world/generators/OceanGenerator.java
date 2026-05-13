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

package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.*;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class OceanGenerator extends WorldGenerator {

    public OceanGenerator() {
        super("water", new ItemStack(Material.WATER_BUCKET));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 7, 0);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public void afterCreation(@NotNull World world) {
        world.setSpawnLocation(0, 8, 0);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates ocean world";
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunkData.setBlock(i, 0, j, Material.BEDROCK);
                chunkData.setBlock(i, 1, j, Material.SAND);
                chunkData.setBlock(i, 2, j, Material.SAND);
                chunkData.setBlock(i, 3, j, Material.WATER);
                chunkData.setBlock(i, 4, j, Material.WATER);
                chunkData.setBlock(i, 5, j, Material.WATER);
                chunkData.setBlock(i, 6, j, Material.WATER);
                chunkData.setBlock(i, 7, j, Material.WATER);
            }
        }
    }

}
