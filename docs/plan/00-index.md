# 00 — How to use this plan

This directory is the implementation plan for Wortkatze. It is written to be
executed by an agent working one roadmap step at a time, not read end to end.

## Reading order

**Before your first line of code**, read exactly four files:

1. [`01-product.md`](01-product.md) — who this is for. Every design argument
   in the other files bottoms out here.
2. [`02-architecture.md`](02-architecture.md) — the stack, the package layout,
   and the one rule about purity that everything else depends on.
3. [`24-implementer-rules.md`](24-implementer-rules.md) — how to work in this
   repo without breaking things.
4. The roadmap step you are doing, in
   [`22-roadmap-foundations.md`](22-roadmap-foundations.md) or
   [`23-roadmap-modes.md`](23-roadmap-modes.md).

Then read the two or three topic files your step names. **Do not read all of
them.** They are reference, not preamble.

## What each file is

| File | What it tells you |
|---|---|
| [01 product](01-product.md) | The learner, the tone, what "fun" has to mean |
| [02 architecture](02-architecture.md) | Stack, packages, MVVM, the purity rule |
| [03 vocabulary](03-vocabulary.md) | `vocab.json`, where words come from, Wortarten |
| [04 progress store](04-progress-store.md) | What is remembered and how it is written |
| [05 scheduler](05-scheduler.md) | Leitner boxes: what to ask next |
| [06 round engine](06-round-engine.md) | The shared session loop all five modes use |
| [07 design language](07-design-language.md) | Colour, shape, type, spacing, components |
| [08 juice](08-juice.md) | Confetti, springs, shakes — the reward feedback |
| [09 mascot](09-mascot.md) | Mimi the cat, her moods, and Elfie |
| [10 navigation](10-navigation.md) | Screen graph and routes |
| [11 home screen](11-home-screen.md) | The first thing she sees |
| [12](12-mode-karten.md)–[16](16-mode-blitz.md) modes | One file per game mode |
| [17 rewards](17-rewards.md) | XP, levels, stars, stickers, the daily goal |
| [18 audio & haptics](18-audio-haptics.md) | Pronunciation, tones, vibration |
| [19 localization](19-localization.md) | Three locales and the bilingual label rule |
| [20 accessibility](20-accessibility.md) | Reduce-motion, contrast, touch targets |
| [21 testing](21-testing.md) | What must be tested, and how |
| [22](22-roadmap-foundations.md)–[23](23-roadmap-modes.md) roadmap | The PR-sized steps, in order |
| [24 implementer rules](24-implementer-rules.md) | Working agreements |
| [25 implementer prompt](25-implementer-prompt.md) | The prompt that starts a step, and the milestone map |

## The rules that never bend

These are load-bearing. If a step seems to require breaking one, the step is
wrong — say so in the PR instead of breaking the rule.

1. **`learn/` is pure Kotlin.** No `android.*` imports, no `Context`, no
   `System.currentTimeMillis()`, no unseeded `Random`. Time and randomness are
   parameters. This is what makes the whole learning core testable on the JVM
   in under a second. See [02](02-architecture.md).
2. **The word list is generated, never hand-edited.** `tools/vocab.py` is the
   source; `app/src/main/assets/vocab.json` is output. CI fails if they
   disagree. See [03](03-vocabulary.md).
3. **der = blue, die = pink, das = green.** Identical to the Pebble app, so the
   association transfers. These three colours are never reused for anything
   else. See [07](07-design-language.md).
4. **Swiss spelling: always *ss*, never *ß*.** In vocabulary, in UI strings, in
   comments. A test enforces it for the word list.
5. **Colour is never the only signal.** Every article chip carries its word,
   every verdict carries a shape as well as a hue. See [20](20-accessibility.md).
6. **User-visible text is a string resource.** ViewModels and pure classes have
   no `Context`, so failures travel as `@StringRes Int`. German vocabulary is
   content, not UI, and is not translated. See [19](19-localization.md).
7. **No new permissions, and no network.** The app is offline by design
   (`docs/decisions/0004`). Adding one is a product decision needing an ADR.
8. **`./gradlew testDebugUnitTest lintDebug assembleDebug` must pass** before
   any push. Lint is a hard gate, `MissingTranslation` included.

## How the roadmap works

Each step is one PR. A step names:

* **Goal** — one sentence.
* **Files** — exactly which files to create or change.
* **Spec** — the signatures and behaviour, precise enough that two
  implementers would produce the same thing.
* **Done when** — the acceptance criteria, including named tests. A step is not
  finished until those tests exist and pass.

Steps are ordered so that each one leaves `main` shippable. Do not start step
N+1 before step N is merged; do not bundle two steps into one PR because they
look small. The order is the plan's main safety mechanism.

If a step's spec turns out to be wrong or impossible, **fix the plan file in
the same PR** and say so in the PR body. A plan that lies is worse than no
plan.
