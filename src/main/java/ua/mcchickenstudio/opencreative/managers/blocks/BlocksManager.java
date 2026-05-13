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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

import java.util.concurrent.CompletableFuture;

/**
 * <h1>BlocksManager</h1>
 * This interface represents a manager, that controls
 * changing many blocks in world.
 */
public interface BlocksManager extends Manager {

    /**
     * Sets blocks type in region.
     * <p>
     * <b>NOTE:</b> thenAccept methods should be used with {@link org.bukkit.scheduler.BukkitScheduler#runTask(Plugin, Runnable)},
     * if they require synchronous usage (changing some blocks after setting area).
     *
     * @param first begin.
     * @param second end.
     * @param material material to set.
     * @param limit limit of changed blocks.
     * @return future with changed blocks amount.
     */
    @NotNull CompletableFuture<Integer> setBlocksType(@NotNull Location first, @NotNull Location second, @NotNull Material material, int limit);

}
