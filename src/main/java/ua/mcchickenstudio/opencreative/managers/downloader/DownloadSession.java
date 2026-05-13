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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.io.File;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <h1>DownloadSession</h1>
 * This class represents a session of downloading a planet.
 */
public final class DownloadSession {

    private final int planetID;
    private final String player;
    private final String secretToken;
    private final long creationTime;
    private File archive;

    /**
     * Creates download session with specified planet ID,
     * and randomly generated token.
     *
     * @param planetID id of planet, that was requested to create an archive.
     */
    public DownloadSession(int planetID, @NotNull String playerName) {
        this.creationTime = System.currentTimeMillis();
        this.planetID = planetID;
        this.player = playerName;
        byte[] key = new byte[10];
        new SecureRandom().nextBytes(key);
        this.secretToken = Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    /**
     * Checks whether session should be removed,
     * because it was created too many seconds ago.
     *
     * @return true - should be removed, false - not yet.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > OpenCreative.getSettings().getDownloaderSettings().getMaxStoringTime() * 1000L;
    }

    /**
     * Returns player name, who wants to download a world.
     *
     * @return name of player.
     */
    public @NotNull String getPlayer() {
        return player;
    }

    /**
     * Sets compressed archive with planet folders.
     *
     * @param archive archive with planet folders.
     */
    public void setArchive(@NotNull File archive) {
        this.archive = archive;
    }

    /**
     * Returns archive with planet folders.
     *
     * @return compressed archive with planet folders, or null - if not exists yet.
     */
    public @Nullable File getArchive() {
        return archive;
    }

    /**
     * Returns ID of planet, that was requested to create an archive.
     *
     * @return id of associated planet.
     */
    public int getPlanetID() {
        return planetID;
    }

    /**
     * Returns token, that gives access to download the planet.
     *
     * @return secret token.
     */
    public @NotNull String getSecretToken() {
        return secretToken;
    }

}
