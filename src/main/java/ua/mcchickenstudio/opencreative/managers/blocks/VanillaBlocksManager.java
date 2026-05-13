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

package ua.mcchickenstudio.opencreative.managers.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class VanillaBlocksManager implements BlocksManager {

    @Override
    public @NotNull CompletableFuture<Integer> setBlocksType(@NotNull Location first, @NotNull Location second, @NotNull Material material, int limit) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        World world = first.getWorld();
        int changed = 0;
        for (int x = first.getBlockX(); x <= second.getBlockX(); x++) {
            for (int z = first.getBlockZ(); z <= second.getBlockZ(); z++) {
                if (changed > limit) {
                    future.complete(changed);
                    return future;
                }
                Block block = world.getBlockAt(x, 0, z);
                block.setType(material);
                changed++;
            }
        }
        future.complete(changed);
        return future;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Vanilla Blocks Manager";
    }
}
