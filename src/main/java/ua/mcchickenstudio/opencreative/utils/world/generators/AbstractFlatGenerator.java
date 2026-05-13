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
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <h1>AbstractFlatGenerator</h1>
 * This class represents a flat world generator, that
 * will generate layers of blocks. To add layers, just
 * put them in {@link AbstractFlatGenerator#blocks blocks map}, that contains
 * Y coordinates and materials of blocks.
 */
public abstract class AbstractFlatGenerator extends WorldGenerator {

    protected final Map<Integer, Material> blocks = new HashMap<>();
    protected Biome biome = Biome.PLAINS;
    protected boolean generateTrees = false;

    public AbstractFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon) {
        super(id, displayIcon);
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return getHighestBlock(world);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    private @NotNull Location getHighestBlock(@NotNull World world) {
        int highestY = world.getMinHeight();
        for (Map.Entry<Integer, Material> entry : blocks.entrySet()) {
            if (entry.getValue().isSolid()) {
                highestY = Math.max(highestY, entry.getKey());
            }
        }
        return new Location(world, 0, highestY + 1, 0);
    }


    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
                    Material material = blocks.getOrDefault(y, Material.AIR);
                    if (!material.isBlock() || material == Material.AIR) {
                        continue;
                    }
                    chunkData.setBlock(i, y, j, material);
                }
            }
        }
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            @Override
            public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return biome;
            }

            @Override
            public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return List.of(biome);
            }
        };
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        List<BlockPopulator> list = new ArrayList<>();
        if (!generateTrees) return list;
        list.add(new BlockPopulator() {
            @Override
            public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
                if (random.nextBoolean()) {
                    TreeType type = getTreeFromBiome(biome);
                    if (type == null) return;
                    int amount = random.nextInt(2) + 1;
                    for (int tree = 1; tree < amount; tree++) {
                        int treeX = (chunkX << 4) + random.nextInt(9) + 3;
                        int treeZ = (chunkZ << 4) + random.nextInt(9) + 3;
                        int treeY = limitedRegion.getHighestBlockYAt(treeX, treeZ);
                        limitedRegion.generateTree(new Location(null, treeX, treeY, treeZ), random, type);
                    }
                }
            }

            public @Nullable TreeType getTreeFromBiome(@NotNull Biome biome) {
                return switch (biome) {
                    case PLAINS, TAIGA, WINDSWEPT_FOREST, SNOWY_TAIGA, OLD_GROWTH_SPRUCE_TAIGA -> TreeType.TREE;
                    case DARK_FOREST -> TreeType.DARK_OAK;
                    case BIRCH_FOREST -> TreeType.BIRCH;
                    case JUNGLE -> TreeType.JUNGLE;
                    case SAVANNA -> TreeType.ACACIA;
                    case LUSH_CAVES -> TreeType.AZALEA;
                    case MANGROVE_SWAMP -> TreeType.MANGROVE;
                    case CHERRY_GROVE -> TreeType.CHERRY;
                    case MUSHROOM_FIELDS -> TreeType.RED_MUSHROOM;
                    case SMALL_END_ISLANDS -> TreeType.CHORUS_PLANT;
                    case ICE_SPIKES, SNOWY_PLAINS -> TreeType.MEGA_PINE;
                    default -> null;
                };
            }
        });
        return list;
    }
}
