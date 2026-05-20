/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package ua.mcchickenstudio.opencreative.utils.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>GoogleTranslator</h1>
 * Lightweight client for the Google Translate free endpoint
 * (translate.googleapis.com/translate_a/single). No API key required.
 *
 * <p>Used to detect language and translate chat messages so players
 * in different locales can understand each other.</p>
 */
public final class GoogleTranslator {

    private static final String ENDPOINT = "https://translate.googleapis.com/translate_a/single";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private GoogleTranslator() {}

    /**
     * Translate the given text to the target language asynchronously.
     *
     * @param text           the text to translate.
     * @param targetLanguage ISO language code (e.g. "en", "ru", "uk").
     * @return future resolving to a {@link TranslationResult}, or {@code null} on failure.
     */
    public static @NotNull CompletableFuture<@Nullable TranslationResult> translate(@NotNull String text,
                                                                                    @NotNull String targetLanguage) {
        if (text.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }
        TranslationResult cached = TranslationCache.get(targetLanguage, text);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        String url = ENDPOINT
                + "?client=gtx"
                + "&sl=auto"
                + "&tl=" + URLEncoder.encode(targetLanguage, StandardCharsets.UTF_8)
                + "&dt=t"
                + "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(6))
                .header("User-Agent", "Mozilla/5.0 OpenCreative-Translator")
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) return null;
                    TranslationResult parsed = parseResponse(response.body(), targetLanguage);
                    if (parsed != null) {
                        TranslationCache.put(targetLanguage, text, parsed);
                    }
                    return parsed;
                })
                .exceptionally(ex -> null);
    }

    /**
     * Parse the Google Translate JSON response.
     * Response format example:
     * [[["Hello","Hola",null,null,1]],null,"es",...]
     */
    static @Nullable TranslationResult parseResponse(@NotNull String body, @NotNull String targetLanguage) {
        try {
            // Concatenate translated sentence chunks: body[0][i][0] for each i.
            StringBuilder translated = new StringBuilder();
            int idx = 0;
            // Expect to start with "[[["
            if (!body.startsWith("[[")) return null;
            idx = 2; // position after "[["
            while (idx < body.length() && body.charAt(idx) == '[') {
                // each chunk: ["translated","original",...]
                int strStart = body.indexOf('"', idx);
                if (strStart == -1) break;
                StringBuilder chunk = new StringBuilder();
                int p = strStart + 1;
                while (p < body.length()) {
                    char c = body.charAt(p);
                    if (c == '\\' && p + 1 < body.length()) {
                        char next = body.charAt(p + 1);
                        switch (next) {
                            case 'n' -> chunk.append('\n');
                            case 't' -> chunk.append('\t');
                            case 'r' -> chunk.append('\r');
                            case '"' -> chunk.append('"');
                            case '\\' -> chunk.append('\\');
                            case '/' -> chunk.append('/');
                            case 'u' -> {
                                if (p + 5 < body.length()) {
                                    String hex = body.substring(p + 2, p + 6);
                                    try {
                                        chunk.append((char) Integer.parseInt(hex, 16));
                                    } catch (NumberFormatException ex) {
                                        chunk.append('?');
                                    }
                                    p += 4;
                                }
                            }
                            default -> chunk.append(next);
                        }
                        p += 2;
                    } else if (c == '"') {
                        break;
                    } else {
                        chunk.append(c);
                        p++;
                    }
                }
                translated.append(chunk);
                // Find end of this chunk array (look for "],[" or "]]")
                int next = body.indexOf("],[", p);
                int end = body.indexOf("]]", p);
                if (next != -1 && (end == -1 || next < end)) {
                    idx = next + 2; // jump to next [
                } else {
                    idx = end + 2;
                    break;
                }
            }

            // Parse detected source language: it sits after the first big array, like ...,null,"es",
            // Find the closing of the outer first array then read the language code.
            String detectedLang = extractSourceLanguage(body);

            String result = translated.toString();
            if (result.isEmpty()) return null;
            return new TranslationResult(result, detectedLang, targetLanguage);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Extracts the detected source language code from Google's response.
     * The detected language is the second string literal in the structure
     * after the translations array — typically {@code "...]],null,"<lang>",...}.
     */
    private static @NotNull String extractSourceLanguage(@NotNull String body) {
        // Find ",null," followed by a quoted language code.
        int marker = body.indexOf(",null,\"");
        if (marker == -1) return "auto";
        int start = marker + ",null,\"".length();
        int end = body.indexOf('"', start);
        if (end == -1 || end - start > 16) return "auto";
        return body.substring(start, end);
    }
}
