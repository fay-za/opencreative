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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.managers.downloader.DisabledDownloader;
import ua.mcchickenstudio.opencreative.managers.downloader.Downloader;

/**
 * <h1>DownloaderSettings</h1>
 * This class represents a settings of downloader.
 */
public final class DownloaderSettings {

    private int maxStoringTime = 60;
    private int maxArchiveSize = 30;

    /**
     * Loads settings of downloader from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("web.downloader");
        if (section == null) {
            section = config.createSection("web.downloader");
        }
        maxArchiveSize = section.getInt("max-size", 30);
        maxStoringTime = section.getInt("storing-time", 60);
        if (section.getBoolean("enabled", false)) {
            OpenCreative.setDownloadManager(new Downloader());
        } else {
            OpenCreative.setDownloadManager(new DisabledDownloader());
        }
    }

    /**
     * Returns maximum size of world archive
     * in megabytes.
     *
     * @return maximum size of archive in MB.
     */
    public int getMaxArchiveSize() {
        return maxArchiveSize;
    }

    /**
     * Returns how many seconds need to pass
     * before world archive will be removed
     * automatically.
     *
     * @return maximum time of storing archive in
     * temp folder in seconds.
     */
    public int getMaxStoringTime() {
        return maxStoringTime;
    }
}
