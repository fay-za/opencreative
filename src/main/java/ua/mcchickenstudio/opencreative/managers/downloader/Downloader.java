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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.FileUtils;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>Downloader</h1>
 * This class represents a download manager, that will be used
 * to compress and upload planet folders archive by using
 * command: /world download.
 */
public final class Downloader implements DownloadManager {

    private final Map<String, DownloadSession> sessions = new ConcurrentHashMap<>();
    private final BukkitRunnable cleanerRunnable;
    private HttpServer server;

    public Downloader() {
        cleanerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                clearAllArchives();
            }
        };
    }

    @Override
    public @NotNull CompletableFuture<String> uploadPlanet(@NotNull Planet planet, @NotNull Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        DownloadSession session = new DownloadSession(planet.getId(), player.getName());
        long time = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Player " + player.getName() + " requested to download world " + planet.getId()
                + "\nCreating session (" + session.getSecretToken() + ") and compressing world folder...");
        File archive;
        try {
            archive = compressPlanetToArchive(planet, session);
        } catch (Exception error) {
            future.completeExceptionally(error);
            return future;
        }
        if (!archive.exists()) {
            future.completeExceptionally(new RuntimeException("World archive doesn't exists."));
            return future;
        }
        session.setArchive(archive);
        sessions.put(session.getSecretToken(), session);
        String link = "http://" + OpenCreative.getSettings().getWebSettings().getDisplayLink()
                + "/download?token=" + session.getSecretToken();
        OpenCreative.getPlugin().getLogger().info("Compressed world " + planet.getId() + " folder for downloader: " + player.getName()
                + " in " + (System.currentTimeMillis() - time) + " ms. Player can download world :)");
        future.complete(link);
        return future;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NotNull File compressPlanetToArchive(@NotNull Planet planet, @NotNull DownloadSession session) {
        File tempFolder = FileUtils.getTempFolder();
        if (!tempFolder.isDirectory()) {
            tempFolder.delete();
        }
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }

        File planetFolder = FileUtils.getPlanetFolder(planet);
        File devPlanetFolder = FileUtils.getDevPlanetFolder(planet.getDevPlanet());

        File zipFile = new File(FileUtils.getTempFolder(), planet.getId() + "-" + session.getSecretToken() + ".zip");
        try (ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(
                new LimitedOutputStream(OpenCreative.getSettings().getDownloaderSettings().getMaxArchiveSize(),
                        Files.newOutputStream(zipFile.toPath()))))) {
            addFolderToArchive(planetFolder, zipStream, planet.getId());
            if (devPlanetFolder.exists()) {
                addFolderToArchive(devPlanetFolder, zipStream, planet.getId());
            }
        } catch (TooBigWorldException error) {
            OpenCreative.getPlugin().getLogger().info("Cancelled compressing " + planet.getId() + " folder: too big size.");
            throw error;
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to compress " + planetFolder.getName() + " folders for world downloader.", error);
            return zipFile;
        }
        return zipFile;
    }

    @Override
    public void clearArchives(@NotNull Planet planet) {
        for (Map.Entry<String, DownloadSession> session : new HashSet<>(sessions.entrySet())) {
            if (session.getValue().getPlanetID() == planet.getId()) {
                sessions.remove(session.getKey());
            }
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void clearAllArchives() {
        sessions.entrySet().removeIf(entry -> {
            DownloadSession session = entry.getValue();
            if (session.isExpired()) {
                File archive = session.getArchive();
                if (archive != null && archive.exists()) {
                    archive.delete();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void init() {
        int port = OpenCreative.getSettings().getWebSettings().getPort();
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", this::handle);
            cleanerRunnable.runTaskTimerAsynchronously(OpenCreative.getPlugin(), 20L, 1200L);
            server.start();
            OpenCreative.getPlugin().getLogger().info("Started world downloader web service on port: " + port);
        } catch (Exception error) {
            if (error instanceof BindException) {
                sendCriticalErrorMessage("Something is already using world downloader web server's port " + port + ", so it's failed to start.");
            } else {
                sendCriticalErrorMessage("Failed to start world downloader web server.", error);
            }
        }
    }

    /**
     * Returns token from link's query.
     *
     * @param exchange handler of request.
     * @return token, or empty string.
     */
    private @NotNull String getTokenFromQuery(@NotNull HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        String token = "";
        if (query != null) {
            for (String part : query.split("&")) {
                if (part.startsWith("token=")) {
                    token = part.substring(6);
                    break;
                }
            }
        }
        return token;
    }

    private void handle(@NotNull HttpExchange exchange) {
        String IP = getIP(exchange);
        try {
            sendDebug("[DOWNLOADER] " + IP + " opened " + exchange.getRequestURI().getPath() + " with query " + exchange.getRequestURI().getQuery());
            if (!exchange.getRequestURI().getPath().equals("/download")) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            String token = getTokenFromQuery(exchange);
            if (token.isEmpty() || !sessions.containsKey(token)) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            DownloadSession session = sessions.get(token);
            File archive = session.getArchive();
            if (archive == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            OpenCreative.getPlugin().getLogger().info("Sending world archive " + archive.getName() + " to: " + IP + " by session: " + session.getSecretToken());
            sendArchive(IP, session, archive, exchange);
        } catch (Exception error) {
            sendDebugError("Failed to send response to " + IP + " by world downloader by query: " +
                    exchange.getRequestURI().getPath(), error);
        }

    }

    private void sendArchive(@NotNull String IP,
                             @NotNull DownloadSession session,
                             @NotNull File archive,
                             @NotNull HttpExchange exchange) throws IOException {
        String fileName = getLocaleMessage("world.downloader.archive")
                .replace("%id%", String.valueOf(session.getPlanetID())) + ".zip";

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        exchange.getResponseHeaders().add(
                "Content-Disposition",
                "attachment; filename=\"planet" + session.getPlanetID() + ".zip\"; filename*=UTF-8''" + encodedFileName
        );
        exchange.getResponseHeaders().add(
                "Content-Type",
                "application/zip"
        );

        exchange.sendResponseHeaders(200, 0);

        long start = System.currentTimeMillis();
        long maxTime = OpenCreative.getSettings().getDownloaderSettings().getMaxStoringTime() * 1000L;
        try (exchange; exchange; InputStream in = Files.newInputStream(archive.toPath());
             OutputStream out = exchange.getResponseBody()) {

            byte[] buffer = new byte[8192];
            int read;

            while ((read = in.read(buffer)) != -1) {
                if (System.currentTimeMillis() - start > maxTime) {
                    sendDebug("[DOWNLOADER] Time out of downloading " + archive.getName() + " by " + IP);
                    break;
                }
                out.write(buffer, 0, read);
            }

        } catch (IOException e) {
            sendDebug("[DOWNLOADER] Client disconnected when downloading " + archive.getName() + " by " + IP);
            OpenCreative.getPlugin().getLogger().info("Ended session for world downloader: " + session.getSecretToken());
        } finally {
            sessions.remove(session.getSecretToken());
            if (archive.delete()) {
                sendDebug("[DOWNLOADER] Ended world download session " + session.getSecretToken() + " by " + IP + " (requested by: " + session.getPlayer() + ")");
            }
            OpenCreative.getPlugin().getLogger().info("World " + session.getPlanetID() + " was downloaded by " + session.getPlayer() + " (" + IP + ") in session " + session.getSecretToken());
        }
    }

    /**
     * Compresses and adds folder to archive.
     *
     * @param folder folder to add to archive.
     * @param zipStream archive stream.
     * @param planetID id of planet.
     */
    private static void addFolderToArchive(@NotNull File folder, @NotNull ZipOutputStream zipStream, int planetID) throws IOException {
        try (Stream<Path> stream = Files.walk(folder.toPath())) {
            String folderDisplayName;
            if (folder.getName().startsWith("planet")) {
                if (folder.getName().endsWith("dev")) {
                    folderDisplayName = getLocaleMessage("world.downloader.dev-folder")
                            .replace("%id%", String.valueOf(planetID));
                } else {
                    folderDisplayName = getLocaleMessage("world.downloader.build-folder")
                            .replace("%id%", String.valueOf(planetID));
                }
            } else {
                folderDisplayName = folder.getName();
            }
            stream.forEach(path -> {
                try {
                    String entryName = folderDisplayName + File.separator + folder.toPath().relativize(path)
                            .toString().replace("\\", "/");
                    if (Files.isDirectory(path)) {
                        if (!entryName.endsWith("/")) {
                            entryName += "/";
                        }
                        zipStream.putNextEntry(new ZipEntry(entryName));
                        zipStream.closeEntry();
                    } else if (!entryName.endsWith("session.lock")) {
                        zipStream.putNextEntry(new ZipEntry(entryName));
                        Files.copy(path, zipStream);
                        zipStream.closeEntry();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    /**
     * Returns IP address from requester.
     *
     * @param exchange handler of request.
     * @return IP address.
     */
    private @NotNull String getIP(@NotNull HttpExchange exchange) {
        return exchange.getRemoteAddress()
                .getAddress()
                .getHostAddress();
    }

    @Override
    public void shutdown() {
        if (server != null) server.stop(0);
    }

    @Override
    public boolean isEnabled() {
        return server != null;
    }

    @Override
    public String getName() {
        return "World Downloader";
    }
}
