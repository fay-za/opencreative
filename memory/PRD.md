# OpenCreative+ — Chat Translation Feature

## Problem statement
> Take a look at https://github.com/fay-za/opencreative, implement translating — 2 buttons that appear in chat below the message if it's in any language apart from English. (Refined by the user to: auto-translate for everyone except sender, with one `[Show Original]` button + a small dark-grey tag showing the original language.)

## Architecture
- The repository is a Maven Java 21 PaperMC plugin (Minecraft).
- Chat is dispatched from `listeners/player/ChatListener.java` (via the `AsyncChatEvent`).
- New translation pipeline lives under `utils/translation/` and is plugged into the existing recipient loops.

## Implementation summary (Feb 2026)
- `utils/translation/GoogleTranslator.java` — async client for the free `translate.googleapis.com/translate_a/single` endpoint (no key). Returns translated text + detected source language.
- `utils/translation/TranslationResult.java` — record returning translatedText / sourceLanguage / targetLanguage.
- `utils/translation/PlayerLocaleResolver.java` — resolves a player's chat target language from `Player.locale()` (with normalisation for legacy ISO codes).
- `utils/translation/OriginalMessageStore.java` — bounded FIFO cache (2 000 entries) mapping UUID → original (sender + language + raw text), so `[Show Original]` can fetch the source later.
- `utils/translation/ChatTranslationService.java` — orchestrates per-recipient dispatch: groups recipients by language, fires one translation per language, sends translated component with `[Show Original]` click event + `[LANG]` tag.
- `commands/ShowOriginalCommand.java` — handles `/showoriginal <uuid>` to render the cached original.
- `listeners/player/ChatListener.java` — replaced the 4 raw recipient-loops with `ChatTranslationService.dispatch(...)` calls.
- `resources/plugin.yml` — registered new `showoriginal` command with aliases (`original`, `showorig`, `oc-original`).
- `resources/config.yml` — new `messages.translation.*` config block (`enabled`, format strings, hover tooltip).
- `OpenCreative.java` — registered `ShowOriginalCommand` in `registerCommands()`.

## Verification
- `mvn clean compile` → BUILD SUCCESS (Java 21, Paper 1.21 API).
- Live `GoogleTranslator` smoke test:
  - "Hola amigos" → "Hello friends" (sourceLang=es)
  - "Привет, как дела?" → "Hi, how are you?" (sourceLang=ru)
  - "Hello world" target=en → returns sourceLang=en (suppression branch hit, original sent unchanged).
- Multi-chunk JSON parsing verified ("Hola, ¿cómo estás?" → "Hello, how are you?").

## Behaviour
1. Sender always sees the original message (unchanged).
2. For every other recipient: client locale is resolved → message translated to that language asynchronously → translated line is sent with a clickable `[Show Original]` button and a small dark-grey `[LANG]` tag of the source language.
3. Clicking `[Show Original]` runs `/showoriginal <uuid>` and prints the original text.
4. If the source language matches the recipient's language (e.g. an English player receives an English message), the recipient gets the unchanged formatted message — no extra UI noise.
5. If Google Translate is unreachable, the dispatch falls back to sending the original message.

## Next action items
- (Optional) Add an in-memory LRU translation cache keyed by `(source, target, text)` to avoid re-translating identical chat lines.
- (Optional) Surface a per-player `/translate toggle` command so individual players can opt out.
- (Optional) Swap the free endpoint for a paid Google Cloud Translate key if hosting servers hit rate limits.
