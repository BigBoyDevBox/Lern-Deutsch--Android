# 23 — Roadmap II: the modes (steps 10–17)

Continues [22 — Roadmap I](22-roadmap-foundations.md). Same rules: one step,
one PR, `main` shippable after each, and a step is not done until its named
tests pass.

By step 10 everything a mode needs already exists — engine, components,
scheduler, mascot, confetti, XP. Each mode step is therefore small: a question
builder, a face, and its tests.

---

## Step 10 — Karten

**Goal** The first real mode: flip cards, self-graded.

**Files** `learn/round/KartenQuestion.kt`, `ui/round/karten/KartenFace.kt`;
delete the debug face from step 5.

**Spec** [12 Karten](12-mode-karten.md) — four directions, the Y-rotation flip,
the ✓/✗ buttons appearing immediately, the ↻ repeat marker, the third language
on the back.

**Done when** `KartenQuestionTest` passes. A deck can be played end to end from
the deck picker; the summary shows correct stars; the round writes `Progress`
once, at the end; rotating mid-round keeps the same card and face.

---

## Step 11 — Der Die Das

**Goal** The gender drill — the app's reason to exist.

**Files** `learn/round/ArtikelQuestion.kt`,
`ui/round/artikel/ArtikelFace.kt`; add `Gender.article`.

**Spec** [13 Der Die Das](13-mode-artikel.md) — the prompt without its article,
three fixed-order coloured buttons with their gender names, and the article
flying into the gap on a correct answer.

**Done when** `ArtikelQuestionTest` passes. Tap-to-next is under 700 ms. A
wrong answer shows the full correct form and waits for a tap. Colour is never
the only signal.

---

## Step 12 — Quiz

**Goal** Four-choice recall, and the English bridge.

**Files** `learn/round/Distractors.kt`, `learn/round/QuizQuestion.kt`,
`ui/round/quiz/QuizFace.kt`.

**Spec** [15 Quiz](15-mode-quiz.md) — same-deck distractors, widening only when
forced, never a duplicate of the answer *text*, all four directions.

**Done when** `DistractorsTest` and `QuizQuestionTest` pass. `DE_EN` and
`EN_DE` are playable from the deck picker. A card that cannot raise three
distractors is skipped, not shown short.

---

## Step 13 — Wortarten

**Goal** Nomen, Verben, Adjektive.

**Files** `learn/round/WortartenQuestion.kt` (with `WortartenPool`),
`ui/round/wortarten/WortartenFace.kt`.

**Spec** [14 Wortarten](14-mode-wortarten.md) — the 30/35/35 weighted pool, the
three fixed buttons with their permanent examples, and the reason line on a
miss.

**Done when** `WortartenQuestionTest` and `WortartenPoolTest` pass. The
„Nomen schreibt man gross" line appears once on entry. No `other` card is ever
drawn. Reason strings exist in all three locales.

---

## Step 14 — Blitz

**Goal** Sixty seconds, mixed, with combos.

**Files** `learn/round/Blitz.kt`, `ui/round/blitz/BlitzFace.kt`,
`ui/round/blitz/BlitzViewModel.kt`; extend `Progress` with `blitzBest` and
`blitzBestCombo`.

**Spec** [16 Blitz](16-mode-blitz.md) — the clock with its bonus, penalty and
90 s cap, the step multipliers, the round-robin mixed pool, no mascot.

**Done when** `BlitzScoreTest`, `BlitzClockTest` and `BlitzPoolTest` pass. The
tick coroutine stops on round end **and** on `onDispose`. Beating the best
shows „Rekord!".

---

## Step 15 — The sticker album

**Goal** The long game.

**Files** `ui/album/AlbumScreen.kt`, `AlbumViewModel.kt`; a stats sheet behind
the home stats strip.

**Spec** [17 rewards](17-rewards.md) — the grid, locked stickers shown as
silhouettes with their conditions, the flip-to-reveal on the summary, Mimi in
the header.

**Done when** Every sticker in `Stickers.ALL` renders in both states, with a
name in all three locales. A newly earned sticker is announced on the round
summary. Tapping an earned sticker wobbles it and plays its tone.

---

## Step 16 — Settings and speech

**Goal** The switches, and German pronunciation.

**Files** `ui/settings/SettingsScreen.kt`, `SettingsViewModel.kt`,
`data/Speaker.kt`; 🔊 buttons in the Karten, Quiz and Artikel faces.

**Spec** [18 audio & haptics](18-audio-haptics.md) — TTS with
`Locale.GERMANY`, silent degradation when no German voice exists, speak on tap
only, `shutdown()` on destroy. Settings: sound, vibration, pronunciation,
reduce motion, English gloss, daily goal (10/20/40), default direction, and an
"about" block naming the licence and the Pebble sibling.

**Done when** With no German TTS voice installed, no 🔊 button appears anywhere
and nothing crashes. With one, tapping speaks „der Hund" in full. Every switch
persists across a process restart.

---

## Step 17 — Polish and the first release

**Goal** Ship it.

**Files** README screenshots, `docs/decisions/` for anything decided along the
way, `AGENTS.md` updates for every convention learned.

**Checklist**

* Play every mode at 200 % font scale on a small screen.
* Play every mode with reduce-motion on.
* Play every mode muted.
* Leave the app mid-round, return, confirm nothing was lost.
* Run for a week of simulated days: the streak, the goal and the boxes behave.
* `scripts/build.sh` produces an installable APK; `scripts/install.sh` runs it.
* `scripts/release.sh 1.0.0 --push`, and confirm the Release workflow publishes.

**Done when** v1.0.0 is on the Releases page and the README's version marker
matches.

---

## After v1

Ideas, not commitments. Each needs its own proposal before it is built.

* **Satzbau** — drag words into order to build a sentence. The natural fifth
  skill after gender and Wortarten, and the word list already has phrase cards.
* **Plural forms** — „der Hund → die Hunde". Needs a new field in `vocab.py`
  and would be shared back to the Pebble app.
* **Verb conjugation** — ich/du/er for the 90 verbs. Needs data.
* **A dark theme** — a designed one, not an inversion (`docs/decisions/0003`).
* **Widget** — the streak flame and the daily goal on the home screen.
* **Custom decks** — let her type in the words from this week's vocabulary
  test. Probably the single most-requested thing once she uses it in earnest.
* **Handwriting practice for Chinese** — the reverse direction, for a German
  speaker. A different app, really.
