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

/**
 * Result of a translation request.
 *
 * @param translatedText the translated text in the target language.
 * @param sourceLanguage the detected source language code (e.g. "es", "ru", "fr").
 * @param targetLanguage the target language code used for translation (e.g. "en").
 */
public record TranslationResult(String translatedText, String sourceLanguage, String targetLanguage) {
}
