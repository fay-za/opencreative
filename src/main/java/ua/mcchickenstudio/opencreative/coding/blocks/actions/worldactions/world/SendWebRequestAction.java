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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.WebResponseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public final class SendWebRequestAction extends WorldAction {
    public SendWebRequestAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    private static HttpURLConnection getHttpURLConnection(int id, String url, String request, String media) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod(request);
        connection.setRequestProperty("User-Agent", "OpenCreative+ Web Request, World ID: " + id);
        connection.setDoOutput("POST".equalsIgnoreCase(request));

        if ("json".equalsIgnoreCase(media)) {
            connection.setRequestProperty("Content-Type", "application/json");
        } else {
            connection.setRequestProperty("Content-Type", "text/plain");
        }
        return connection;
    }

    @Override
    protected void execute() {
        if (!getPlanet().getLimits().canSendWebRequest()) {
            return;
        }

        String url = getArguments().getText("url", "", this);
        String body = getArguments().getText("body", "", this);
        String request = getArguments().getText("request", "GET", this).toUpperCase();
        String media = getArguments().getText("media", "text", this);

        if (url.isEmpty()) return;

        if (!List.of("GET", "POST").contains(request)) {
            throw new IllegalArgumentException("Invalid HTTP method, use GET or POST.");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Invalid URL protocol, use http:// or https:// on the start of URL.");
        }

        if (url.toLowerCase().startsWith("http://localhost")
                || url.toLowerCase().startsWith("http://127.0.0.1")
                || url.toLowerCase().startsWith("https://localhost")
                || url.toLowerCase().startsWith("https://127.0.0.1")
        ) {
            throw new IllegalArgumentException("This URL is blocked from sending requests.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = getHttpURLConnection(getPlanet().getId(), url, request, media);

                    if ("POST".equalsIgnoreCase(request) && !body.isEmpty()) {
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(body.getBytes(StandardCharsets.UTF_8));
                        }
                    }

                    int code = connection.getResponseCode();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                            code >= 400 ? connection.getErrorStream() : connection.getInputStream()))) {
                        char[] buffer = new char[1024];
                        int read = reader.read(buffer);
                        String response = new String(buffer, 0, Math.max(0, read));
                        Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                                () -> new WebResponseEvent(getPlanet(), url, code, response).callEvent());
                    }
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                            () -> new WebResponseEvent(getPlanet(), url, 408, "Error: " + e.getMessage()));
                }
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());

    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SEND_WEB_REQUEST;
    }
}
