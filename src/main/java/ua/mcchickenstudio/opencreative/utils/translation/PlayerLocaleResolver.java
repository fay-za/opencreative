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

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Resolves the desired target translation language for a given player.
 * <p>
 * By default we read the player's Minecraft client locale (e.g. "ru_ru",
 * "en_us", "uk_ua") and reduce it to the 2-letter ISO code that Google
 * Translate accepts (e.g. "ru", "en", "uk").
 */
public final class PlayerLocaleResolver {

    private PlayerLocaleResolver() {}

    /**
     * Returns the player's preferred translation language code.
     *
     * @param player the player.
     * @return ISO 639-1 language code (lowercase).
     */
    public static @NotNull String getLanguage(@NotNull Player player) {
        Locale locale = player.locale();
        if (locale == null) return "en";
        String lang = locale.getLanguage();
        if (lang == null || lang.isBlank()) return "en";
        // Normalize a few special-cases that Google expects differently.
        return switch (lang.toLowerCase()) {
            case "in" -> "id"; // legacy Indonesian
            case "iw" -> "he"; // legacy Hebrew
            case "ji" -> "yi"; // legacy Yiddish
            default -> lang.toLowerCase();
        };
    }

    /**
     * Tests whether two language codes represent the same language.
     */
    public static boolean sameLanguage(@NotNull String a, @NotNull String b) {
        if (a.equalsIgnoreCase(b)) return true;
        String aa = a.toLowerCase().split("[-_]")[0];
        String bb = b.toLowerCase().split("[-_]")[0];
        return aa.equals(bb);
    }
}
