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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h1>TranslationCache</h1>
 * Bounded LRU cache keyed by (source=auto, target, text) → {@link TranslationResult}.
 *
 * <p>Avoids re-translating identical chat lines on busy servers. The
 * source is always {@code auto} for our use-case, but it is included
 * in the key for future flexibility.</p>
 */
public final class TranslationCache {

    private static final int MAX_ENTRIES = 1_000;

    private static final Map<String, TranslationResult> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_ENTRIES + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, TranslationResult> eldest) {
                    return size() > MAX_ENTRIES;
                }
            }
    );

    private static long hits = 0;
    private static long misses = 0;

    private TranslationCache() {}

    /**
     * Fetch a cached translation, or {@code null} if absent.
     *
     * @param targetLanguage target ISO code.
     * @param text           original (untranslated) text.
     * @return cached result, or {@code null}.
     */
    public static @Nullable TranslationResult get(@NotNull String targetLanguage, @NotNull String text) {
        TranslationResult r = CACHE.get(key(targetLanguage, text));
        if (r == null) misses++; else hits++;
        return r;
    }

    /**
     * Insert a translation result into the cache.
     *
     * @param targetLanguage target ISO code.
     * @param text           original text.
     * @param result         translation result to cache.
     */
    public static void put(@NotNull String targetLanguage,
                           @NotNull String text,
                           @NotNull TranslationResult result) {
        CACHE.put(key(targetLanguage, text), result);
    }

    /** Clears every cached entry. */
    public static void clear() {
        CACHE.clear();
        hits = 0;
        misses = 0;
    }

    /** Current number of cached translations. */
    public static int size() {
        return CACHE.size();
    }

    /** Number of cache hits since the last {@link #clear()}. */
    public static long hits() {
        return hits;
    }

    /** Number of cache misses since the last {@link #clear()}. */
    public static long misses() {
        return misses;
    }

    private static @NotNull String key(@NotNull String targetLanguage, @NotNull String text) {
        return targetLanguage.toLowerCase() + "\0" + text;
    }
}
