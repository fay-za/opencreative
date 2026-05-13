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

package ua.mcchickenstudio.opencreative.coding.modules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>ModuleUrlLoader</h1>
 * Downloads a module YAML from a remote URL and places its coding blocks
 * directly into a developer's planet, without persisting it as a registered
 * module.
 * <p>
 * The download runs asynchronously to avoid blocking the main server thread.
 * All Bukkit-API interactions (block placement, player messaging) are scheduled
 * back to the main thread.
 */
public final class ModuleUrlLoader {

    /** Maximum size of a remote module YAML payload (5 MB). */
    private static final int MAX_BYTES = 5 * 1024 * 1024;
    /** TCP connect timeout for the remote module download (milliseconds). */
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    /** Read timeout for the remote module download (milliseconds). */
    private static final int READ_TIMEOUT_MS = 15_000;
    /** Max number of HTTP redirects we will follow. */
    private static final int MAX_REDIRECTS = 5;
    /** User-Agent used when downloading a remote module. */
    private static final String USER_AGENT = "OpenCreative+/ModuleUrlLoader";

    private ModuleUrlLoader() {
    }

    /**
     * Downloads a module YAML from {@code rawUrl} and places its coding blocks
     * into {@code devPlanet}. Sends progress / error feedback messages to
     * {@code player}.
     *
     * @param player    player who issued the command, will receive feedback.
     * @param devPlanet dev planet to place blocks on.
     * @param rawUrl    raw http(s) URL pointing to a module YAML file.
     */
    public static void loadFromUrl(@NotNull Player player, @NotNull DevPlanet devPlanet, @NotNull String rawUrl) {
        URL url = parseHttpUrl(rawUrl);
        if (url == null) {
            player.sendMessage(getLocaleMessage("modules.load-from-url.invalid-url")
                    .replace("%url%", rawUrl));
            Sounds.PLAYER_FAIL.play(player);
            return;
        }

        player.sendMessage(getLocaleMessage("modules.load-from-url.downloading")
                .replace("%url%", url.toString()));

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String yaml = download(url);
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                            () -> placeOnMainThread(player, devPlanet, url, yaml));
                } catch (DownloadException e) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                        player.sendMessage(getLocaleMessage("modules.load-from-url.download-failed")
                                .replace("%url%", url.toString())
                                .replace("%error%", e.getMessage()));
                        Sounds.PLAYER_FAIL.play(player);
                    });
                }
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }

    // -------- main-thread placement --------

    private static void placeOnMainThread(@NotNull Player player, @NotNull DevPlanet devPlanet,
                                          @NotNull URL url, @NotNull String yamlText) {
        if (!devPlanet.isLoaded() || !devPlanet.getWorld().getPlayers().contains(player)) {
            // Player has left or the world unloaded during the download.
            return;
        }

        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(yamlText);
        } catch (InvalidConfigurationException e) {
            player.sendMessage(getLocaleMessage("modules.load-from-url.invalid-format")
                    .replace("%url%", url.toString()));
            Sounds.PLAYER_FAIL.play(player);
            return;
        }

        ConfigurationSection blocks = yaml.getConfigurationSection("code.blocks");
        if (blocks == null || blocks.getKeys(false).isEmpty()) {
            player.sendMessage(getLocaleMessage("modules.load-from-url.invalid-format")
                    .replace("%url%", url.toString()));
            Sounds.PLAYER_FAIL.play(player);
            return;
        }

        CodingBlockPlacer placer = new CodingBlockPlacer(devPlanet);
        CodingBlockPlacer.CodePlacementResult result = placer.placeCodingLines(devPlanet, blocks);
        switch (result.getType()) {
            case NOT_ENOUGH_SPACE -> {
                int requiredColumns = blocks.getKeys(false).size();
                player.sendMessage(getLocaleMessage("modules.few-space")
                        .replace("%required%", String.valueOf(requiredColumns)));
                Sounds.DEV_NOT_ALLOWED.play(player);
            }
            case ERROR -> {
                player.sendMessage(getLocaleMessage("modules.load-from-url.place-failed")
                        .replace("%url%", url.toString()));
                Sounds.PLAYER_FAIL.play(player);
                result.getPlacedColumns().forEach(devPlanet::addChangedColumn);
            }
            case NOTHING_TO_PLACE, CANNOT_PLACE -> {
                player.sendMessage(getLocaleMessage("modules.load-from-url.invalid-format")
                        .replace("%url%", url.toString()));
                Sounds.PLAYER_FAIL.play(player);
            }
            case SUCCESS -> {
                player.sendMessage(getLocaleMessage("modules.load-from-url.loaded")
                        .replace("%url%", url.toString())
                        .replace("%amount%", String.valueOf(result.getPlacedColumns().size())));
                Sounds.DEV_MODULE_INSTALLED.play(player);
                result.getPlacedColumns().forEach(devPlanet::addChangedColumn);
            }
        }
    }

    // -------- helpers --------

    /**
     * Validates {@code rawUrl} and returns a parsed URL if it is an http(s) URL,
     * otherwise null.
     */
    private static URL parseHttpUrl(@NotNull String rawUrl) {
        try {
            URI uri = new URI(rawUrl);
            String scheme = uri.getScheme();
            if (scheme == null) return null;
            scheme = scheme.toLowerCase();
            if (!scheme.equals("http") && !scheme.equals("https")) return null;
            if (uri.getHost() == null || uri.getHost().isEmpty()) return null;
            return uri.toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Downloads the given URL as a UTF-8 string. Follows up to
     * {@value #MAX_REDIRECTS} http(s) redirects and refuses payloads larger
     * than {@value #MAX_BYTES} bytes.
     *
     * @throws DownloadException for any I/O error, non-2xx response, oversize
     *                           payload, or invalid redirect.
     */
    private static String download(@NotNull URL initialUrl) throws DownloadException {
        URL current = initialUrl;
        for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) current.openConnection();
            } catch (IOException e) {
                throw new DownloadException("connection failed: " + e.getMessage(), e);
            }
            try {
                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
                conn.setReadTimeout(READ_TIMEOUT_MS);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestProperty("Accept", "application/x-yaml, text/yaml, text/plain, */*");

                int status;
                try {
                    status = conn.getResponseCode();
                } catch (IOException e) {
                    throw new DownloadException("connection failed: " + e.getMessage(), e);
                }

                if (status >= 300 && status < 400) {
                    String location = conn.getHeaderField("Location");
                    if (location == null || location.isEmpty()) {
                        throw new DownloadException("redirect without location header (HTTP " + status + ")");
                    }
                    URL next = parseHttpUrl(location);
                    if (next == null) {
                        // Maybe a relative redirect; resolve against the current URL.
                        try {
                            next = new URL(current, location);
                        } catch (MalformedURLException e) {
                            throw new DownloadException("invalid redirect target: " + location);
                        }
                        String s = next.getProtocol().toLowerCase();
                        if (!s.equals("http") && !s.equals("https")) {
                            throw new DownloadException("redirect to non-http(s) URL: " + next);
                        }
                    }
                    current = next;
                    continue;
                }

                if (status < 200 || status >= 300) {
                    throw new DownloadException("HTTP " + status);
                }

                long declared = conn.getContentLengthLong();
                if (declared > MAX_BYTES) {
                    throw new DownloadException("payload too large (" + declared + " > " + MAX_BYTES + " bytes)");
                }

                try (InputStream in = conn.getInputStream()) {
                    return readBoundedUtf8(in);
                } catch (IOException e) {
                    throw new DownloadException("read failed: " + e.getMessage(), e);
                }
            } finally {
                conn.disconnect();
            }
        }
        throw new DownloadException("too many redirects (>" + MAX_REDIRECTS + ")");
    }

    /**
     * Reads up to {@link #MAX_BYTES} from {@code in} and returns the UTF-8
     * decoded text. Throws if the stream exceeds the limit.
     */
    private static String readBoundedUtf8(@NotNull InputStream in) throws IOException, DownloadException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int total = 0;
        while (true) {
            int read = in.read(buf);
            if (read == -1) break;
            total += read;
            if (total > MAX_BYTES) {
                throw new DownloadException("payload too large (>" + MAX_BYTES + " bytes)");
            }
            out.write(buf, 0, read);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    /** Internal exception used to bubble download errors back to the main thread. */
    private static final class DownloadException extends Exception {
        DownloadException(String message) {
            super(message);
        }

        DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
