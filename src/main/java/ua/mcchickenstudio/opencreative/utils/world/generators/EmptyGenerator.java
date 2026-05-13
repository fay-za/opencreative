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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class EmptyGenerator extends WorldGenerator implements EnvironmentCapable {

    public EmptyGenerator() {
        super("empty", new ItemStack(Material.GLASS));
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator, @NotNull String biome) {
        creator.type(WorldType.FLAT);
        creator.generator(this);
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 5, 0, -90, 0);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public void afterCreation(@NotNull World world) {
        world.setSpawnLocation(0, 5, 0);
        for (int x = 5; x >= -5; x--) {
            for (int z = 5; z >= -5; z--) {
                world.getBlockAt(x, 4, z).setType(Material.BEDROCK);
            }
        }
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates empty world";
    }

}
