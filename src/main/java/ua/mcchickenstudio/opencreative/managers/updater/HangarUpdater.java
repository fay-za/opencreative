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

package ua.mcchickenstudio.opencreative.managers.updater;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;

public final class HangarUpdater implements Updater {

    private final String downloadUrl = "https://hangar.papermc.io/mcchickenstudio/OpenCreative";
    private final String apiUrl = "https://hangar.papermc.io/api/v1/projects/OpenCreative/latestrelease";
    private boolean updatesAvailable;

    @Override
    public CompletableFuture<String> checkUpdates() {
        CompletableFuture<String> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runDelayed(OpenCreative.getPlugin(), (task) -> {
            try {
                HttpURLConnection connection = getHttpURLConnection();
                int code = connection.getResponseCode();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
                    char[] buffer = new char[64];
                    int read = reader.read(buffer);
                    String response = new String(buffer, 0, Math.max(0, read));
                    double latestVersion = getVersionFromText(response);
                    double currentVersion = getVersionFromText(getCurrentVersion());
                    if (latestVersion > currentVersion) {
                        updatesAvailable = true;
                        OpenCreative.getPlugin().getLogger().info("A new version (" + response + ") is available for downloading! Current: " + getCurrentVersion());
                        OpenCreative.getPlugin().getLogger().info("Visit this site, select latest version, read changelogs and replace OpenCreative.jar with new one.");
                        OpenCreative.getPlugin().getLogger().info(downloadUrl);
                        future.complete(response);
                    } else {
                        updatesAvailable = false;
                        OpenCreative.getPlugin().getLogger().info("No updates detected, probably it's latest version :) (" + currentVersion + " - " + latestVersion + ")");
                        future.complete("");
                    }
                }
            } catch (Exception error) {
                updatesAvailable = false;
                sendDebugError("Can't check updates: " + apiUrl, error);
                future.completeExceptionally(error);
            }
        }, 1, TimeUnit.SECONDS);
        return future;
    }

    private HttpURLConnection getHttpURLConnection() throws IOException, URISyntaxException {
        URL requestUrl = new URI(apiUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        return connection;
    }

    @Override
    public boolean canBeUpdated() {
        return updatesAvailable;
    }

    private @NotNull String getCurrentVersion() {
        return OpenCreative.getPlugin().getPluginMeta().getVersion();
    }

    private double getVersionFromText(@NotNull String text) {
        try {
            int firstDot = text.indexOf('.');
            if (firstDot == -1) {
                // "5 Preview"
                StringBuilder versionBuilder = new StringBuilder();
                for (char c : text.toCharArray()) {
                    if (Character.isDigit(c)) {
                        versionBuilder.append(c);
                    } else {
                        break;
                    }
                }
                return Double.parseDouble(versionBuilder.toString());
            }
            // "5.9.0 Preview 2"
            String firstNumber = text.substring(0, firstDot);
            int first = Integer.parseInt(firstNumber); // 5

            String secondText = text.substring(firstDot + 1); // 9.0 Preview 2
            StringBuilder secondBuilder = new StringBuilder();
            for (char c : secondText.toCharArray()) {
                if (Character.isDigit(c)) {
                    secondBuilder.append(c);
                } else if (c == '.') {
                    secondBuilder.append("0");
                } else {
                    break;
                }
            }
            return Double.parseDouble(first + "." + secondBuilder); // 5.90
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    @Override
    public void init() {
        Bukkit.getAsyncScheduler().runDelayed(OpenCreative.getPlugin(), (task) -> checkUpdates(), 3, TimeUnit.SECONDS);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Hangar Updater";
    }
}
