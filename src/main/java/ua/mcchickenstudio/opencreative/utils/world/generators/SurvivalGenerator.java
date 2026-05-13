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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SurvivalGenerator extends WorldGenerator implements EnvironmentCapable, StructuresCapable, BiomeChangeable {

    public SurvivalGenerator() {
        super("survival", new ItemStack(Material.OAK_SAPLING));
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 70, 0);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
        creator.type(WorldType.NORMAL);
        creator.generator(this);
        if (biome.isEmpty() || biome.equals("all")) return;
        Biome biomeType = getBiome(biome, creator.environment());
        if (biomeType == null) return;
        creator.biomeProvider(new SingleBiomeProvider(biomeType));
    }

    @Override
    public void afterCreation(@NotNull World world) {}

    @Override
    public @NotNull Map<@NotNull String, @NotNull Material> getBiomes(@NotNull World.Environment environment) {
        Map<String, Material> map = new LinkedHashMap<>();
        switch (environment) {
            case NORMAL -> {
                map.put("all", Material.ENDER_EYE);
                map.put("snowy", Material.SNOW_BLOCK);
                map.put("jungle", Material.JUNGLE_SAPLING);
                map.put("desert", Material.SAND);
            }
            case NETHER -> {
                map.put("all", Material.ENDER_EYE);
                map.put("soul", Material.SOUL_SAND);
                map.put("warped", Material.WARPED_NYLIUM);
                map.put("crimson", Material.CRIMSON_NYLIUM);
                map.put("basalt", Material.BASALT);
            }
            case THE_END -> {
                map.put("all", Material.ENDER_EYE);
                map.put("city", Material.CHORUS_FLOWER);
                map.put("islands", Material.END_STONE_BRICK_STAIRS);
            }
        }
        return map;
    }

    private @Nullable Biome getBiome(@NotNull String name, @NotNull World.Environment environment) {
        if (environment == World.Environment.NORMAL) {
            return switch (name) {
                case "snowy" -> Biome.SNOWY_TAIGA;
                case "jungle" -> Biome.JUNGLE;
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
        } else if  (environment == World.Environment.THE_END) {
            return switch (name) {
                case "city" -> Biome.END_HIGHLANDS;
                case "islands" -> Biome.SMALL_END_ISLANDS;
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
        return "Creates normal world";
    }

}
