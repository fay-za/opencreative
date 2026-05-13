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
import org.bukkit.block.Biome;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public final class FlatGenerator extends WorldGenerator implements EnvironmentCapable, BiomeChangeable {

    public FlatGenerator() {
        super("flat", new ItemStack(Material.MOSS_BLOCK));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
        if (biome.isEmpty() || biome.equals("all")) return;
        Biome biomeType = getBiome(biome, creator.environment());
        if (biomeType == null) return;
        creator.biomeProvider(new SingleBiomeProvider(biomeType));
    }

    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        Material customFlatTerrainBlock = null;
        Material customFlatSurfaceBlock = null;
        if (worldInfo.getEnvironment() == World.Environment.NORMAL) {
            customFlatTerrainBlock = Material.DIRT;
            customFlatSurfaceBlock = Material.GRASS_BLOCK;
        }
        switch (chunkData.getBiome(0, 0, 0)) {
            case DESERT -> {
                customFlatTerrainBlock = Material.SANDSTONE;
                customFlatSurfaceBlock = Material.SAND;
            }
            case SNOWY_TAIGA -> {
                customFlatTerrainBlock = Material.SNOW_BLOCK;
                customFlatSurfaceBlock = Material.SNOW_BLOCK;
            }
            case BASALT_DELTAS -> customFlatTerrainBlock = Material.BASALT;
            case WARPED_FOREST -> {
                customFlatTerrainBlock = Material.NETHERRACK;
                customFlatSurfaceBlock = Material.WARPED_NYLIUM;
            }
            case SOUL_SAND_VALLEY -> {
                customFlatTerrainBlock = Material.SOUL_SOIL;
                customFlatSurfaceBlock = Material.SOUL_SAND;
            }
            case CRIMSON_FOREST -> {
                customFlatTerrainBlock = Material.NETHERRACK;
                customFlatSurfaceBlock = Material.CRIMSON_NYLIUM;
            }
        }
        int minHeight = OpenCreative.getSettings().shouldGenerateFlatWorldHigher() ? 0 : -64;
        if (worldInfo.getEnvironment() == World.Environment.NETHER) {
            minHeight = 0;
            customFlatTerrainBlock = Material.NETHERRACK;
        } else if (worldInfo.getEnvironment() == World.Environment.THE_END) {
            minHeight = 0;
            customFlatTerrainBlock = Material.END_STONE;
        }
        if (customFlatTerrainBlock == null) {
            super.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData);
            return;
        }
        if (customFlatSurfaceBlock == null) customFlatSurfaceBlock = customFlatTerrainBlock;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunkData.setBlock(i, minHeight, j, Material.BEDROCK);
                chunkData.setBlock(i, minHeight+1, j, customFlatTerrainBlock);
                chunkData.setBlock(i, minHeight+2, j, customFlatTerrainBlock);
                chunkData.setBlock(i, minHeight+3, j, customFlatSurfaceBlock);
            }
        }
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, getMinHeight(world.getEnvironment()) + 4, 0);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    private int getMinHeight(@NotNull World.Environment environment) {
        return switch (environment) {
            case NORMAL -> (OpenCreative.getSettings().shouldGenerateFlatWorldHigher() ? 0 : -64);
            case NETHER, CUSTOM, THE_END -> 0;
        };
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Material> getBiomes(World.@NotNull Environment environment) {
        Map<String, Material> map = new LinkedHashMap<>();
        switch (environment) {
            case NORMAL -> {
                map.put("all", Material.ENDER_EYE);
                map.put("snowy", Material.SNOW_BLOCK);
                map.put("desert", Material.SAND);
            }
            case NETHER -> {
                map.put("all", Material.ENDER_EYE);
                map.put("soul", Material.SOUL_SAND);
                map.put("warped", Material.WARPED_NYLIUM);
                map.put("crimson", Material.CRIMSON_NYLIUM);
                map.put("basalt", Material.BASALT);
            }
        }
        return map;
    }

    private @Nullable Biome getBiome(@NotNull String name, @NotNull World.Environment environment) {
        if (environment == World.Environment.NORMAL) {
            return switch (name) {
                case "snowy" -> Biome.SNOWY_TAIGA;
                case  "desert" -> Biome.DESERT;
                default -> null;
            };
        } else if (environment == World.Environment.NETHER) {
            return switch (name) {
                case "soul" -> Biome.SOUL_SAND_VALLEY;
                case "warped" -> Biome.WARPED_FOREST;
                case "crimson" -> Biome.CRIMSON_FOREST;
                case "basalt" -> Biome.BASALT_DELTAS;
                default -> null;
            };
        }
        return null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates flat world";
    }

}
