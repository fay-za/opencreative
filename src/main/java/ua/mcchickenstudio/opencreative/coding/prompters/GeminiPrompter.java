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

package ua.mcchickenstudio.opencreative.coding.prompters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;

/**
 * <h1>GeminiPrompter</h1>
 * This class represents a coding prompter, that uses
 * Gemini to generate a code.
 */
public final class GeminiPrompter implements CodingPrompter, PrompterModelCapable {

    private char[] token = new char[200];
    private String model = "gemini-2.5-flash";

    @Override
    public @NotNull CompletableFuture<String> generateCode(@NotNull String nickname,
                                                           @NotNull UUID uuid,
                                                           @NotNull String text,
                                                           int actionsLimit) {
        CompletableFuture<String> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"
                                + model + ":generateContent"))
                        .header("x-goog-api-key", new String(token))
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "OpenCreative+ Coding Prompter")
                        .POST(HttpRequest.BodyPublishers.ofString(getRequest(nickname, uuid, text, actionsLimit)))
                        .timeout(Duration.ofSeconds(OpenCreative.getSettings().getCodingSettings().getPrompterTimeout()))
                        .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 401) {
                        future.completeExceptionally(new UnauthorizedPrompterException());
                    } else if (response.statusCode() == 429) {
                        future.completeExceptionally(new PrompterLimitedException());
                    } else if (response.statusCode() != 200) {
                        future.completeExceptionally(new PrompterDownException());
                    } else {
                        response.body();

                        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                        JsonArray candidates = root.getAsJsonArray("candidates");
                        if (candidates == null || candidates.isEmpty()) {
                            future.complete("");
                            return;
                        }

                        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = firstCandidate.getAsJsonObject("content");
                        JsonArray parts = content.getAsJsonArray("parts");

                        if (parts == null || parts.isEmpty()) {
                            future.complete("");
                            return;
                        }

                        String code = parts.get(0).getAsJsonObject().get("text").getAsString();

                        if (code.startsWith("```yaml")) {
                            code = code.substring(7);
                        }
                        if (code.startsWith("```")) {
                            code = code.substring(3);
                        }
                        if (code.endsWith("```")) {
                            code = code.substring(0, code.length() - 3);
                        }
                        future.complete(code);
                    }
                } catch (ConnectException | HttpTimeoutException error) {
                    future.completeExceptionally(error);
                } catch (Exception error) {
                    sendDebugError("Failed to respond for code generation: " + text, error);
                    future.completeExceptionally(error);
                }
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
        return future;
    }

    private @NotNull String getRequest(@NotNull String nickname,
                                       @NotNull UUID uuid,
                                       @NotNull String text,
                                       int actionsLimit) {
        return new Gson().toJson(
                new GeminiRequest(
                        new GeminiInstruction(
                                List.of(new GeminiParts(new PrompterInstruction(
                                        nickname, uuid.toString(), text, actionsLimit).get()))
                        ),
                        List.of(
                                new GeminiContents("user", List.of(new GeminiParts(text)))
                        )
                )
        );
    }

    @Override
    public @NotNull String getModel() {
        return model;
    }

    @Override
    public void setModel(@NotNull String model) {
        this.model = model;
    }

    @Override
    public void setToken(@NotNull String token) {
        this.token = token.toCharArray();
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return token != null && token.length > 10;
    }

    @Override
    public String getName() {
        return "Gemini Coding Prompter";
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class GeminiInstruction {

        private final List<GeminiParts> parts;

        GeminiInstruction(List<GeminiParts> parts) {
            this.parts = parts;
        }

    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class GeminiParts {

        private final String text;

        GeminiParts(String text) {
            this.text = text;
        }

    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class GeminiContents {

        private final String role;
        private final List<GeminiParts> parts;

        GeminiContents(String role, List<GeminiParts> parts) {
            this.role = role;
            this.parts = parts;
        }

    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class GeminiRequest {

        private final GeminiInstruction systemInstruction;
        private final List<GeminiContents> contents;

        GeminiRequest(GeminiInstruction systemInstruction, List<GeminiContents> contents) {
            this.systemInstruction = systemInstruction;
            this.contents = contents;
        }

    }

}
