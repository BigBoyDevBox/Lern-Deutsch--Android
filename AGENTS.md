# AGENTS.md — operating manual

The operational source of truth for agents (and humans) working on Wortkatze.
When you learn something durable about how this repo behaves — a quirk, a
footgun, a changed convention — **update this document in the same PR**.

## Name

**Wortkatze · 词猫** — "word cat". The launcher label is `Wortkatze` (9
characters, under Android's ~12-character ellipsis point). 词猫 appears beside
it on screen but is never the label.

The applicationId is `ch.lkmc.wortkatze` and so is the package. Changing an
applicationId breaks upgrades for everyone who sideloaded a build, and no user
ever sees it.

The mascot is **Mimi · 咪咪**, a small pink cat. **Elfie** is a guest — a real
tortoiseshell Devon Rex, and an easter egg only.

## What this app is

A kawaii German-learning app for a 14-year-old with Chinese as her first
language, who also needs English. It is the phone-sized successor to
[L-K-M/Lern-Deutsch--Pebble](https://github.com/L-K-M/Lern-Deutsch--Pebble) and
shares its word list verbatim.

Read [PLAN.md](PLAN.md) first — it points at [`docs/plan/`](docs/plan/), which
is the constitution: product framing, architecture, one file per game mode, and
a roadmap of PR-sized steps. Deviations get recorded here as they happen.

The plan is deliberately split across many small files. Keep it that way: the
automated reviewer fails on large files, and a plan nobody can review rots.

## Build, test, lint

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug   # exactly what CI runs
./gradlew testDebugUnitTest                           # the suite (JVM-only, by design)
./gradlew lintDebug                                   # hard CI gate — keep it clean
scripts/build.sh                                      # release APK staged into dist/
scripts/install.sh                                    # build + install + launch
python3 tools/gen_vocab.py                            # after editing the word list
python3 tools/validate_vocab.py                       # word-list rules only
```

- JDK 17. Android SDK path via `local.properties` (`sdk.dir=…`) or
  `ANDROID_HOME`. Agent sessions: `.claude/setup-android.sh` bootstraps the SDK
  idempotently (wired as a SessionStart hook).
- Versions are pinned ONLY in `gradle/libs.versions.toml`. Never add an ad-hoc
  version to a build file; never restate catalog versions in docs.

## Toolchain quirks — don't "fix" these

- `compileSdkVersion("android-37.0")` (string form) is deliberately paired with
  `android.suppressUnsupportedCompileSdk=37` in `gradle.properties`. The two
  move together or not at all.
- There is NO `kotlin-android` plugin: AGP 9 provides built-in Kotlin support.
  Only android-application, kotlin-compose, kotlin-serialization, ksp and hilt
  are applied.
- `app/debug.keystore` is checked in ON PURPOSE and signs BOTH build types
  (`.gitignore` whitelists it). Zero-secret CI, reproducible builds,
  sideload-only distribution — see `docs/decisions/0002`. Do not "rotate" it,
  do not add signing secrets without a recorded product decision.
- `app/build.gradle.kts` puts `src/main/assets` on the **unit-test** classpath
  so JVM tests can parse the real `vocab.json` (`VocabAssetTest`,
  `CandyPaletteTest` read it as `/vocab.json`). Removing that `sourceSets` line
  breaks both with a confusing null.

## Architecture in one paragraph

Single module `:app`, packages first. MVVM with one immutable UiState per
screen (StateFlow from a ViewModel), Hilt DI, single activity, Compose +
Material 3 with a custom always-light "candy" theme. The learning core lives in
`learn/` as **pure Kotlin** — no `android.*` imports, no Context, time and
randomness passed in — which is what makes the whole of it unit-testable on the
JVM. `data/` is the only package that touches assets and disk; `ui/` is thin.

## Conventions and footguns

- **`learn/` may not import `android.*`.** No `Context`, no
  `System.currentTimeMillis()`, no bare `Random()`. This is not style — the
  entire test strategy depends on it (docs/plan/02, 21). If you want a clock,
  add a parameter; if you want a Context, the code belongs in `data/`.
- **The word list is generated.** `tools/vocab.py` (shared verbatim with the
  Pebble app, `docs/decisions/0001`) → `tools/gen_vocab.py` →
  `app/src/main/assets/vocab.json`. **Never hand-edit the JSON.** CI runs
  `gen_vocab.py --check` and fails on drift — a gate the Pebble app lacks and
  whose absence its own review notes flag (`awesome.md` 2.5).
- **Wortart is curated build-time data, never a runtime heuristic**
  (`docs/decisions/0006`). „Danke" is capitalised and is not a noun; „sieben"
  and „offen" end in -en and are not verbs. `tools/vocab_pos.py` owns the
  answer and `validate_vocab.py` re-proves it.
- **A card's id is `deckKey/German`.** Every piece of saved progress hangs off
  it. Reordering decks and inserting cards are therefore both free — unlike the
  Pebble app, which keys by deck *position* and may never reorder `GROUPS`.
  Rewriting a German word retires that card's history, which is correct.
- **der = blue, die = pink, das = green**, identical to the Pebble app so the
  association transfers. These three colours are never used for anything else.
  All colour comes from `ui/theme/Color.kt`; no ad-hoc `Color(0x…)` in a
  screen.
- **Light-only theme, deliberately** (`docs/decisions/0003`).
  `isSystemInDarkTheme()` is ignored on purpose — don't wire it up without the
  designed dark palette the decision asks for.
- **Swiss spelling: always *ss*, never *ß*** — in vocabulary, UI strings and
  comments. `validate_vocab.py` and `VocabAssetTest` both enforce it for the
  word list.
- **User-visible wording is a string resource, everywhere.** ViewModels and
  `learn/` have no Context, so text travels as `@StringRes Int`. The app ships
  `values/` (English, the fallback), `values-de/` and `values-b+zh+Hans/`,
  listed in `res/xml/locales_config.xml` — add a locale to that file in the
  same change that adds its `values-*` folder, or the picker won't offer it.
  Lint's `MissingTranslation` is a hard CI gate, so brand words and German
  vocabulary carry `translatable="false"`.
- **Bilingual labels are two strings, not one.** The German half is *content*
  she is learning and is `translatable="false"`; the other half is translated.
  `BilingualLabel` composes them (docs/plan/19).
- **Stop every animation.** Loops, coroutines and timers get cancelled in
  `onDispose` or when their effect ends. The Pebble sibling shipped exactly
  this as a battery bug (`awesome.md` 2.1); here it shows up as a warm phone.
- **Progress is written once per finished round, not per answer**
  (`docs/decisions/0005`). DataStore rewrites the whole document each time.
- The app has **no permissions at all** (`docs/decisions/0004`). Adding one —
  INTERNET especially — is a product decision requiring an ADR.
- Ears are drawn *before* the head so the head circle swallows their bases.
  Ears anchored at the head's equator float free as the circle narrows, and it
  gets worse the bigger the cat. Learned by the sibling the hard way
  (`awesome.md` 1.7); don't re-learn it.
- Scripts follow the family house style: header comment doubles as `--help` via
  the awk one-liner; `==>` / `--` / `!!` log prefixes; `set -euo pipefail`.

## CI/CD

Three workflows (details: [CICD.md](CICD.md)): `ci.yml` (vocabulary gate +
tests + lint + debug APK on every PR/main push), `release.yml` (v* tags →
verified, published APK), `zai-code-review.yml` (GLM 5.2 reviews every PR;
respond per [CLAUDE.md](CLAUDE.md)). Family contract on every workflow:
least-privilege permissions, explicit concurrency, timeouts, wrapper
validation.

## Releasing

`scripts/release.sh X.Y.Z --push` (shared lkm-release engine) bumps
versionName, auto-increments versionCode by exactly 1, rewrites the README
version marker, commits, tags `vX.Y.Z`, pushes. **Never hand-edit versionCode.
Never create a `v*` tag by hand.** Bump the most-minor version component +
versionCode on every non-trivial change set.

## Review process

PRs are reviewed by GLM 5.2 automatically. Findings are triaged
apply/decline/refute per [CLAUDE.md](CLAUDE.md); declined findings and their
reasons accumulate in [REVIEW.md](REVIEW.md) so later rounds (and later agents)
don't flip-flop.
