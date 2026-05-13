# PRD — OpenCreative+ `/module loadFromURL` feature

## Original problem statement
Add a new feature to the OpenCreative+ Minecraft plugin
(https://github.com/fay-za/opencreative): a `/module` subcommand
`loadFromURL <url>` that downloads a module YAML from a remote URL and places
its coding blocks into the calling player's developer planet.

## User choices
- **Persistence:** ephemeral — placed once, *not* saved to disk or registered.
- **Permission:** none — anyone who can already develop on a dev planet may use it.

## Files added
- `src/main/java/ua/mcchickenstudio/opencreative/coding/modules/ModuleUrlLoader.java`
  - Validates URL (http/https only, with host)
  - Downloads asynchronously (no main-thread stall) with 10 s connect + 15 s read timeouts
  - Refuses payloads larger than **5 MB**
  - Follows up to **5 HTTP redirects**, refuses non-http(s) redirect targets
  - Parses the downloaded text as `YamlConfiguration`, requires a `code.blocks` section
  - Switches back to the main thread to call `CodingBlockPlacer.placeCodingLines`
  - Reports success / `NOT_ENOUGH_SPACE` / `ERROR` / `CANNOT_PLACE` / `INVALID_FORMAT`
    via locale messages and the proper sound effects

## Files modified
- `src/main/java/ua/mcchickenstudio/opencreative/commands/ModuleCommand.java`
  - New `case "loadfromurl"` branch (case-insensitive switch on `args[0]`).
    Reuses the existing `canUseCommand` guard (player + dev planet + developer
    role check), then calls `ModuleUrlLoader.loadFromUrl(...)`.
  - Tab-completion: `loadFromURL` suggested at level 1 (only when in a dev
    planet); `https://` placeholder suggested at level 2.
- `src/main/resources/locales/en.yml`, `ua.yml`, `ru.yml`
  - Added `modules.load-from-url.{invalid-url,downloading,download-failed,
    invalid-format,place-failed,loaded}` messages.
  - Extended `modules.help` to mention `/module loadFromURL URL`.

## Verification
- `mvn compile` (Java 21 / Temurin 21.0.5, Paper 1.21 deps) — **passes** for
  all my changed/new files. The build still fails on an unrelated pre-existing
  bug in
  `coding/blocks/actions/structureactions/manipulations/DecrementStructureValueAction.java`
  (`Arguments.getDouble` signature mismatch) that exists on upstream `master`
  before my changes.
- Standalone runtime smoke-test of the private `parseHttpUrl` and `download`
  helpers against an in-process `HttpServer` — **6/6 assertions passed**:
  - Rejects `file://`, `ftp://`, malformed input, missing host.
  - Accepts http(s) case-insensitively.
  - 200 returns body; 404 → `DownloadException`; >5 MB → `DownloadException`;
    redirect loop → caps at 5 hops; single redirect followed correctly.

## Usage
```
/module loadFromURL https://example.com/path/to/module.yml
```
Requires the issuer to be a player on a developer planet they can develop on,
exactly like the existing `/module load <id>` subcommand.

## Backlog / future ideas
- P2: Optionally persist the downloaded module (assign new ID, write to
  `modules/moduleN.yml`, register) — flag like `loadFromURL <url> --save`.
- P2: Allowed-hosts allowlist in plugin config for security-conscious server owners.
- P2: Reuse existing `modules.installed` broadcast so other developers on the planet
  also see when someone loads from URL.
