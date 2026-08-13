# 21 — Testing

## The shape of the suite

**JVM unit tests only.** `./gradlew testDebugUnitTest`. There is no
`androidTest` directory, and adding one means adding an emulator CI job — a
decision, not a detail.

That constraint is what forces the architecture: decision logic lives in
`learn/` as pure Kotlin, so it is all reachable from a fast test
([02](02-architecture.md)). If something important cannot be tested, it is in
the wrong package.

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug   # exactly what CI runs
./gradlew testDebugUnitTest --tests '*LeitnerTest'    # one class
```

## What must be tested

Everything in `learn/`, exhaustively. Concretely, by the time the roadmap is
done:

| Area | Class | Plan |
|---|---|---|
| The shipped word list | `VocabAssetTest` | [03](03-vocabulary.md) |
| Palette ↔ data agreement | `CandyPaletteTest` | [07](07-design-language.md) |
| Star maths | `StarsTest` | [06](06-round-engine.md) |
| Streak arithmetic | `StreakTest` | [17](17-rewards.md) |
| Saved document | `ProgressTest`, `ProgressSerializerTest` | [04](04-progress-store.md) |
| Scheduling | `LeitnerTest`, `SelectionTest` | [05](05-scheduler.md) |
| The round loop | `RoundTest` | [06](06-round-engine.md) |
| Per-mode questions | `KartenQuestionTest`, `ArtikelQuestionTest`, `WortartenQuestionTest`, `WortartenPoolTest`, `QuizQuestionTest`, `DistractorsTest` | [12](12-mode-karten.md)–[15](15-mode-quiz.md) |
| Blitz | `BlitzScoreTest`, `BlitzClockTest`, `BlitzPoolTest` | [16](16-mode-blitz.md) |
| Progression | `LevelsTest`, `XpTest`, `StickersTest` | [17](17-rewards.md) |
| Particles | `ConfettiTest` | [08](08-juice.md) |
| Tones | `ToneSpecTest` | [18](18-audio-haptics.md) |
| Routes | `RouteTest` | [10](10-navigation.md) |
| Home derivations | `HomeStatsTest` | [11](11-home-screen.md) |

Each roadmap step names the tests it must add. **A step whose tests do not
exist is not finished**, regardless of whether the feature works on a phone.

## What is not tested, and why

* **Composables.** No Compose UI tests: they need an emulator or Robolectric,
  and they mostly assert that a layout is the layout. Correctness lives below
  them by construction. Use `@Preview` for the visual work — one per mascot
  mood, one per mode face, one per component state.
* **Navigation.** Route *serialization* is tested; navigating is not.
* **The TTS engine and AudioTrack.** `ToneSpec` is tested; the playback is not.
* **The word list's content.** German correctness is not a unit test's job —
  `tools/validate_vocab.py` enforces the structural rules and a human reads the
  words.

## How to write them here

* **Given a seed, assert exactly.** Anything random takes a `Random(seed)` and
  the test asserts the actual output, not a property like "is non-empty". Two
  runs of the same seed must produce identical results, and there is a test for
  that in every seeded class.
* **Given a date, assert exactly.** `learn/` never reads a clock, so a test
  can walk a year of days in a millisecond. Do it: streaks, boxes and daily
  goals all have year-boundary and leap-day cases, and `StreakTest` has both.
* **Test the boundary, not the middle.** `Stars` is 90/60; test 89, 90, 59, 60.
  `Levels` is a curve; test the exact XP at every boundary and one below.
  `BlitzScore` steps at 5/10/20; test 4, 5, 9, 10, 19, 20.
* **Name tests as sentences.** `` fun `a missed day resets to one`() ``. The
  failure output should read like a spec.
* **One assertion idea per test.** A test that checks six things reports one
  failure and hides five.
* **Assert on the real data where possible.** `VocabAssetTest` parses the
  shipped asset rather than a fixture, because a fixture drifts and the shipped
  file is what a learner gets.

## Lint

`lintDebug` is a hard gate, equal to the tests. Notably:

* `MissingTranslation` — every string in all three locales
  ([19](19-localization.md)).
* Accessibility checks on touch targets and content descriptions.

If lint is wrong about something, suppress it **narrowly, in `app/lint.xml`,
with a comment saying why** — the way the `ObsoleteSdkInt` exemption for
`mipmap-anydpi-v26` already does. Never disable a check globally, and never
add `//noinspection` in a source file.

## When a test is inconvenient

If a step's spec forces a test you cannot write — usually because the logic
ended up inside a composable or needs a `Context` — that is the signal to move
the logic into `learn/`, not to skip the test. Say so in the PR if the plan
made it hard, and fix the plan file in the same PR.
