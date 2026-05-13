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
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public final class DisabledDownloader implements DownloadManager {
    @Override
    public @NotNull CompletableFuture<String> uploadPlanet(@NotNull Planet planet, @NotNull Player player) {
        return new CompletableFuture<>();
    }

    @Override
    public @NotNull File compressPlanetToArchive(@NotNull Planet planet, @NotNull DownloadSession session) {
        throw new IllegalStateException("Downloader is disabled");
    }

    @Override
    public void clearArchives(@NotNull Planet planet) {}

    @Override
    public void clearAllArchives() {}

    @Override
    public void init() {}

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void shutdown() {}

    @Override
    public String getName() {
        return "Disabled Download Manager";
    }
}
