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

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleBiomeProvider extends BiomeProvider {

    private final Biome biome;

    public SingleBiomeProvider(@NotNull Biome biome) {
        this.biome = biome;
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        return biome;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return List.of(biome);
    }
}
