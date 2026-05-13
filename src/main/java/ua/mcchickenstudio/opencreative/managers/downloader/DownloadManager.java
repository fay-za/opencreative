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

package ua.mcchickenstudio.opencreative.managers.downloader;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>DownloadManager</h1>
 * This interface represents a manager, that controls web server
 * for downloading players worlds with /world download.
 */
public interface DownloadManager extends Manager {

    /**
     * Compresses planet folders to archive in temporary folder,
     * then generates unique token and returns link to download world.
     * @param planet planet, that was requested to download.
     * @param player player, who requested.
     * @return string of link.
     */
    @NotNull CompletableFuture<String> uploadPlanet(@NotNull Planet planet, @NotNull Player player);

    /**
     * Compresses planet folders to one archive.
     * @param planet planet to compress folders.
     * @param session download session.
     * @return compressed archive.
     */
    @NotNull File compressPlanetToArchive(@NotNull Planet planet, @NotNull DownloadSession session);

    /**
     * Removes saved archives of planet, because it was unloaded.
     * @param planet planet to remove archive.
     */
    void clearArchives(@NotNull Planet planet);

    /**
     * Clears all archives from temporary folder.
     */
    void clearAllArchives();

    /**
     * Shutdowns web world downloader server.
     */
    void shutdown();

}
