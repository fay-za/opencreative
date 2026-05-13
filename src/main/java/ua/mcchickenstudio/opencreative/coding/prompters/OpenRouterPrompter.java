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
 * <h1>OpenRouterPrompter</h1>
 * This class represents a coding prompter, that uses
 * OpenRouterAI to generate a code.
 */
public final class OpenRouterPrompter implements CodingPrompter, PrompterModelCapable {

    private char[] token = new char[200];
    private String model = "google/gemini-2.5-flash";

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
                        .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                        .header("Authorization", "Bearer " + new String(token))
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "OpenCreative+ Coding Prompter")
                        .header("HTTP-Referer", "opencreative.plugin")
                        .header("X-Title", "OpenCreative+ Coding Prompter")
                        .timeout(Duration.ofSeconds(OpenCreative.getSettings().getCodingSettings().getPrompterTimeout()))
                        .POST(HttpRequest.BodyPublishers.ofString(getRequest(nickname, uuid, text, actionsLimit)))
                        .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 401) {
                        future.completeExceptionally(new UnauthorizedPrompterException());
                    } else if (response.statusCode() == 429 || response.statusCode() == 402) {
                        future.completeExceptionally(new PrompterLimitedException());
                    } else if (response.statusCode() != 200) {
                        future.completeExceptionally(new PrompterDownException());
                    } else {
                        response.body();
                        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                        JsonArray choices = root.getAsJsonArray("choices");
                        JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                        String code = message.get("content").getAsString();
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
        return new Gson().toJson(new OpenRouterRequest(model,
                List.of(new Message("system", new PrompterInstruction(
                                nickname, uuid.toString(), text, actionsLimit).get()),
                        new Message("user", text))));
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
        return "OpenRouterAI Coding Prompter";
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class OpenRouterRequest {

        private final String model;
        private final List<Message> messages;

        OpenRouterRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }

    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    static class Message {

        private final String role;
        private final String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

}
