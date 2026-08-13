# 22 — Roadmap I: foundations (steps 0–9)

One step, one PR. Each leaves `main` shippable. Do not start step N+1 before
step N is merged, and do not bundle two steps because they look small — the
order is the plan's main safety mechanism.

Every step's **Done when** implicitly includes: `./gradlew testDebugUnitTest
lintDebug assembleDebug` passes, and the named tests exist and pass.

---

## Step 0 — Scaffolding ✅ done

Shipped in the PR that introduced this plan. Gradle project, CI/CD, scripts,
the vocabulary pipeline (`tools/` → `assets/vocab.json`), `learn/Vocabulary`,
`VocabParser`, `Stars`, `Streak`, `VocabRepository`, the Candy palette, the
theme, and a placeholder home screen that lists every deck. 35 tests.

---

## Step 1 — Components

**Goal** The reusable candy widgets, so no later step invents its own button.

**Files** `ui/components/CandyButton.kt`, `CandyIconButton.kt`,
`StickerCard.kt`, `ArticleChip.kt`, `StarRow.kt`, `StreakFlame.kt`,
`ProgressPill.kt`, `BilingualLabel.kt`.

**Spec** [07 design language](07-design-language.md) for shape, colour,
elevation and press behaviour; [19](19-localization.md) for `BilingualLabel`.
Each component takes `modifier: Modifier = Modifier` as its first optional
parameter, hoists all state, and reads no ViewModel.

**Done when** Every component has at least two `@Preview`s (a default and an
extreme: longest text, 200 % font scale). `ArticleChip` shows the word as well
as the colour. Strings for the three gender names exist in all three locales.

---

## Step 2 — The progress document

**Goal** Remember things.

**Files** `learn/Progress.kt`, `data/ProgressStore.kt`, `di/AppModule.kt`.

**Spec** [04 progress store](04-progress-store.md) — the full document, the
pure update functions, DataStore with a corruption handler falling back to
`Progress()`.

**Done when** `ProgressTest` and `ProgressSerializerTest` pass with the cases
listed in [04](04-progress-store.md). Nothing reads or writes progress yet;
this step ships the storage only.

---

## Step 3 — The scheduler

**Goal** Decide what to ask.

**Files** `learn/Leitner.kt`, `learn/Selection.kt`.

**Spec** [05 scheduler](05-scheduler.md) — five boxes, `INTERVAL_DAYS`,
promote/demote, and the due → new → filler selection order.

**Done when** `LeitnerTest` and `SelectionTest` pass with the cases listed in
[05](05-scheduler.md), including the determinism-under-a-seed case.

---

## Step 4 — Home and navigation

**Goal** A real home screen and the screen graph, replacing the placeholder.

**Files** `ui/navigation/Routes.kt`, `WortkatzeNavHost.kt`; rewrite
`ui/home/HomeScreen.kt` and `HomeViewModel.kt`; add
`ui/home/DeckPickerScreen.kt`; add `learn/HomeStats.kt`.

**Spec** [10 navigation](10-navigation.md) and [11 home](11-home-screen.md).
Mode buttons navigate; the round destination can be reached and shows a stub.
The daily goal ring, the stats strip and the level bar all render from real
`Progress` (which is still all zeroes — that is fine and should look fine).

**Done when** `RouteTest` and `HomeStatsTest` pass. Every mode button leads
somewhere. Back from Home exits. The deck picker lists all 81 decks under their
four tiers with correct accents and star rows.

---

## Step 5 — The round engine

**Goal** The session loop every mode will share.

**Files** `learn/round/Question.kt`, `Direction.kt`, `Round.kt`;
`ui/round/RoundViewModel.kt`, `RoundScaffold.kt`, `RoundSummary.kt`.

**Spec** [06 round engine](06-round-engine.md). `RoundScaffold` owns the
chrome — progress pill, direction chip, back-confirm dialog — and takes the
mode's face as a slot. `RoundSummary` is a phase of the state, not a
destination.

**Done when** `RoundTest` passes with every case in [06](06-round-engine.md).
A throwaway debug face (prompt + a Correct and a Wrong button) can play a full
round of a real deck and reach a summary showing the right stars. Do **not**
ship that debug face past this step; step 10 replaces it.

---

## Step 6 — Mimi

**Goal** The mascot.

**Files** `ui/components/MascotView.kt`, `learn/MascotMood.kt`.

**Spec** [09 mascot](09-mascot.md). Canvas drawing, five moods, the blink
loop, ears rooted inside the head. `moodFor(...)` is pure and lives in
`learn/`.

**Done when** `MascotMoodTest` covers the mood table. Five `@Preview`s, one per
mood, at 32dp and 160dp — the ears must stay attached at both sizes. The blink
coroutine is cancelled in `onDispose` (check with a recomposition test or by
inspection, and say which in the PR).

---

## Step 7 — Confetti

**Goal** The particle system, and the first thing that feels good.

**Files** `learn/juice/Confetti.kt`, `ui/components/ConfettiOverlay.kt`.

**Spec** [08 juice](08-juice.md) — the burst model, 24 particles, gravity, the
900 ms life, and the rule that the animation loop **stops** when the burst
ends.

**Done when** `ConfettiTest` passes: exact particle count, seed determinism,
continuity in `t`, alpha 0 at the end, no `NaN` anywhere in range. The overlay
is wired into the debug round face and visibly fires on a correct answer.

---

## Step 8 — Feedback: haptics, tones, reduce-motion

**Goal** The rest of the answer feedback, and the accessibility switch that
governs all of it.

**Files** `learn/audio/ToneSpec.kt`, `data/TonePlayer.kt`, `data/Haptics.kt`,
`data/MotionSettings.kt`, `ui/theme/LocalReduceMotion.kt`.

**Spec** [18 audio & haptics](18-audio-haptics.md) for the tone specs and the
haptic table; [20 accessibility](20-accessibility.md) for the reduce-motion
replacement table.

**Done when** `ToneSpecTest` passes (PCM length, fades, no clipping). Turning
on the system's "remove animations" replaces every effect rather than removing
it. The app is fully playable muted.

---

## Step 9 — XP, levels, stickers

**Goal** The progression maths, before any mode can earn it.

**Files** `learn/Levels.kt`, `learn/Xp.kt`, `learn/Stickers.kt`; extend
`Progress` with the counters the sticker rules need and bump
`Progress.SCHEMA`.

**Spec** [17 rewards](17-rewards.md) — the level curve, the XP table, the
~24 starter stickers and their rules.

**Done when** `LevelsTest`, `XpTest` and `StickersTest` pass with the boundary
cases listed in [17](17-rewards.md). A document written by step 2 still
deserializes after the schema bump, with the new counters defaulting to 0 —
add that case to `ProgressSerializerTest`.

---

Continue in [23 — Roadmap II: the modes](23-roadmap-modes.md).
