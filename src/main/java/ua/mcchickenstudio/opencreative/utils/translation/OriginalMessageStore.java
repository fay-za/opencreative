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
import java.util.UUID;

/**
 * <h1>OriginalMessageStore</h1>
 * Keeps a bounded, FIFO cache of original (untranslated) chat messages.
 * <p>Each translated chat line carries a {@link UUID} that players can
 * use via the {@code /showoriginal <id>} command to view the source text.
 */
public final class OriginalMessageStore {

    private static final int MAX_ENTRIES = 2_000;

    private static final Map<UUID, Entry> STORE = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_ENTRIES + 1, 0.75f, false) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<UUID, Entry> eldest) {
                    return size() > MAX_ENTRIES;
                }
            }
    );

    private OriginalMessageStore() {}

    /**
     * Store an original message and return a fresh ID for retrieval.
     *
     * @param senderName     name of the sender for display.
     * @param sourceLanguage detected source language code.
     * @param original       the untranslated raw text.
     * @return the assigned id used by /showoriginal.
     */
    public static @NotNull UUID put(@NotNull String senderName,
                                    @NotNull String sourceLanguage,
                                    @NotNull String original) {
        UUID id = UUID.randomUUID();
        STORE.put(id, new Entry(senderName, sourceLanguage, original));
        return id;
    }

    /**
     * Fetch a stored original by id.
     *
     * @param id id returned by {@link #put(String, String, String)}.
     * @return entry, or {@code null} if the id is unknown / evicted.
     */
    public static @Nullable Entry get(@NotNull UUID id) {
        return STORE.get(id);
    }

    /**
     * Cached message entry.
     *
     * @param sender         display name of the sender.
     * @param sourceLanguage detected language code.
     * @param original       original chat text.
     */
    public record Entry(String sender, String sourceLanguage, String original) {}
}
