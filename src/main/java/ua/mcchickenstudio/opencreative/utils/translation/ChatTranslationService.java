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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sends a chat message to a set of recipients, auto-translating it
 * into each recipient's preferred language (skipping the sender, and
 * any recipient whose language matches the source).
 *
 * <p>The translated line is appended with:</p>
 * <ul>
 *     <li>a clickable {@code [Show Original]} button that runs
 *     {@code /showoriginal <id>}</li>
 *     <li>a small dark-grey language code tag (e.g. {@code [ES]}).</li>
 * </ul>
 */
public final class ChatTranslationService {

    private ChatTranslationService() {}

    /**
     * Translate and dispatch a chat message.
     *
     * @param sender          player who sent the message.
     * @param rawMessage      plaintext content of the message.
     * @param formatTemplate  the chat format string from config (containing
     *                        {@code %player%} and {@code %message%}).
     * @param recipients      players who must receive the message.
     * @param defaultMessage  the already-formatted Component to send to
     *                        the sender / when translation is disabled.
     */
    public static void dispatch(@NotNull Player sender,
                                @NotNull String rawMessage,
                                @NotNull String formatTemplate,
                                @NotNull Collection<? extends Player> recipients,
                                @NotNull Component defaultMessage) {
        boolean enabled = OpenCreative.getPlugin().getConfig().getBoolean("messages.translation.enabled", true);
        if (!enabled) {
            for (Player p : recipients) p.sendMessage(defaultMessage);
            return;
        }

        boolean enOnlySource = OpenCreative.getPlugin().getConfig()
                .getBoolean("messages.translation.en-only-source", false);
        // Forced target language: only meaningful with en-only-source = true.
        String forcedTarget = OpenCreative.getPlugin().getConfig()
                .getString("messages.translation.forced-target", "en");

        // Always show original to the sender immediately.
        if (recipients.contains(sender)) {
            sender.sendMessage(defaultMessage);
        }

        // Group recipients by their target language so we batch translation calls.
        // Players that opted out (or whose locale equals the forced target in
        // en-only-source mode and message is already English) get the original.
        Map<String, List<Player>> byLang = new HashMap<>();
        List<Player> sendOriginal = new ArrayList<>();
        for (Player p : recipients) {
            if (p.getUniqueId().equals(sender.getUniqueId())) continue;
            if (TranslationPreferences.isOptedOut(p)) {
                sendOriginal.add(p);
                continue;
            }
            String lang = enOnlySource ? forcedTarget.toLowerCase() : PlayerLocaleResolver.getLanguage(p);
            byLang.computeIfAbsent(lang, k -> new ArrayList<>()).add(p);
        }

        for (Player p : sendOriginal) p.sendMessage(defaultMessage);
        if (byLang.isEmpty()) return;

        for (Map.Entry<String, List<Player>> entry : byLang.entrySet()) {
            String targetLang = entry.getKey();
            List<Player> group = entry.getValue();

            GoogleTranslator.translate(rawMessage, targetLang).thenAccept(result -> {
                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                    boolean sameLanguage = result == null
                            || result.translatedText().equalsIgnoreCase(rawMessage)
                            || PlayerLocaleResolver.sameLanguage(result.sourceLanguage(), targetLang);

                    // In en-only-source mode we only translate when the detected
                    // source language is NOT English; English sources stay as-is.
                    boolean blockedByEnOnly = enOnlySource
                            && result != null
                            && PlayerLocaleResolver.sameLanguage(result.sourceLanguage(), "en");

                    if (sameLanguage || blockedByEnOnly) {
                        for (Player p : group) p.sendMessage(defaultMessage);
                        return;
                    }

                    UUID id = OriginalMessageStore.put(sender.getName(), result.sourceLanguage(), rawMessage);
                    Component line = buildTranslatedLine(sender, result, formatTemplate, id);

                    for (Player p : group) p.sendMessage(line);
                });
            });
        }
    }

    /**
     * Build the final chat line shown to a translated recipient:
     * <pre>
     *   &lt;formatted translation&gt; [Show Original] [LANG]
     * </pre>
     */
    private static @NotNull Component buildTranslatedLine(@NotNull Player sender,
                                                          @NotNull TranslationResult result,
                                                          @NotNull String formatTemplate,
                                                          @NotNull UUID id) {
        String mainFormatted = formatTemplate
                .replace("%player%", sender.getName())
                .replace("%message%", MiniMessage.miniMessage().escapeTags(result.translatedText()));
        mainFormatted = legacyToMini(mainFormatted);

        Component base = MiniMessage.miniMessage().deserialize(mainFormatted);

        String showOriginalText = OpenCreative.getPlugin().getConfig().getString(
                "messages.translation.show-original-button",
                " <dark_gray>[<gray><u>Show Original</u><dark_gray>]"
        );
        String langTagFormat = OpenCreative.getPlugin().getConfig().getString(
                "messages.translation.language-tag",
                " <dark_gray>[<gray>%lang%<dark_gray>]"
        );
        String hoverText = OpenCreative.getPlugin().getConfig().getString(
                "messages.translation.show-original-hover",
                "<gray>Click to view the original message"
        );

        Component showOriginal = MiniMessage.miniMessage().deserialize(legacyToMini(showOriginalText))
                .clickEvent(ClickEvent.runCommand("/showoriginal " + id))
                .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(legacyToMini(hoverText))));

        Component langTag = MiniMessage.miniMessage().deserialize(legacyToMini(
                langTagFormat.replace("%lang%", result.sourceLanguage().toUpperCase())
        )).hoverEvent(HoverEvent.showText(
                MiniMessage.miniMessage().deserialize("<gray>Original language: <white>"
                        + result.sourceLanguage().toUpperCase())
        ));

        return base.append(showOriginal).append(langTag);
    }

    /**
     * Convert legacy color codes ({@code &a}, {@code §a}) into MiniMessage
     * tags so the format strings authored in config.yml keep working.
     */
    private static @NotNull String legacyToMini(@NotNull String input) {
        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(i + 1));
                String tag = switch (code) {
                    case '0' -> "<black>";
                    case '1' -> "<dark_blue>";
                    case '2' -> "<dark_green>";
                    case '3' -> "<dark_aqua>";
                    case '4' -> "<dark_red>";
                    case '5' -> "<dark_purple>";
                    case '6' -> "<gold>";
                    case '7' -> "<gray>";
                    case '8' -> "<dark_gray>";
                    case '9' -> "<blue>";
                    case 'a' -> "<green>";
                    case 'b' -> "<aqua>";
                    case 'c' -> "<red>";
                    case 'd' -> "<light_purple>";
                    case 'e' -> "<yellow>";
                    case 'f' -> "<white>";
                    case 'k' -> "<obfuscated>";
                    case 'l' -> "<bold>";
                    case 'm' -> "<strikethrough>";
                    case 'n' -> "<underlined>";
                    case 'o' -> "<italic>";
                    case 'r' -> "<reset>";
                    default -> null;
                };
                if (tag != null) {
                    out.append(tag);
                    i++;
                    continue;
                }
            }
            out.append(c);
        }
        return out.toString();
    }
}
