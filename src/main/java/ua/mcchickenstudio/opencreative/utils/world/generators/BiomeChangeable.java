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

import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * <h1>BiomeChangeable</h1>
 * This interface is used in {@link WorldGenerator}, that supports choice
 * of Minecraft biomes in {@link ua.mcchickenstudio.opencreative.menus.world.WorldGenerationMenu}.
 */
public interface BiomeChangeable {

    /**
     * Returns map of available lower cased biomes
     * names and their icons.
     * @param environment selected environment.
     * @return map of biomes names and icons.
     */
    @NotNull Map<@NotNull String, @NotNull Material> getBiomes(@NotNull World.Environment environment);

}
