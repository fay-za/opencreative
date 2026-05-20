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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <h1>TranslationPreferences</h1>
 * Tracks which players have opted out of chat translation.
 * <p>
 * Backed by a small YAML file at {@code plugins/OpenCreative/translation-opt-out.yml}.
 * The set is loaded once on first access and persisted on every toggle.
 */
public final class TranslationPreferences {

    private static final Set<UUID> OPTED_OUT = Collections.synchronizedSet(new HashSet<>());
    private static volatile boolean loaded = false;
    private static File file;

    private TranslationPreferences() {}

    /**
     * Returns {@code true} when this player has opted out of receiving translations.
     */
    public static boolean isOptedOut(@NotNull Player player) {
        ensureLoaded();
        return OPTED_OUT.contains(player.getUniqueId());
    }

    /**
     * Toggles the opt-out flag for the given player.
     *
     * @return new state after toggle: {@code true} = opted-out (no translations),
     *         {@code false} = opted-in (default behaviour).
     */
    public static boolean toggle(@NotNull Player player) {
        ensureLoaded();
        boolean nowOptedOut;
        synchronized (OPTED_OUT) {
            if (OPTED_OUT.contains(player.getUniqueId())) {
                OPTED_OUT.remove(player.getUniqueId());
                nowOptedOut = false;
            } else {
                OPTED_OUT.add(player.getUniqueId());
                nowOptedOut = true;
            }
        }
        save();
        return nowOptedOut;
    }

    private static synchronized void ensureLoaded() {
        if (loaded) return;
        try {
            file = new File(OpenCreative.getPlugin().getDataFolder(), "translation-opt-out.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                for (String raw : cfg.getStringList("opted-out")) {
                    try {
                        OPTED_OUT.add(UUID.fromString(raw));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
            // Fail-safe: empty set, file will be re-created on first save.
        } finally {
            loaded = true;
        }
    }

    private static void save() {
        if (file == null) return;
        try {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            YamlConfiguration cfg = new YamlConfiguration();
            cfg.set("opted-out", OPTED_OUT.stream().map(UUID::toString).collect(Collectors.toList()));
            cfg.save(file);
        } catch (IOException ignored) {
        }
    }
}
