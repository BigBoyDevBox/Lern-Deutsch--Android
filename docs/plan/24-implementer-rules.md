# 24 — Working rules for whoever builds this

Read this once, before your first step. It is short on purpose.

The prompt that drives the whole build — steps 1 to 17, one pull request each
— is in [25](25-implementer-prompt.md).

## The shape of a step

1. Read the step in [22](22-roadmap-foundations.md) or
   [23](23-roadmap-modes.md), then the two or three plan files it names. **Do
   not read the whole plan.**
2. Branch: `claude/<topic>` or `fable/<topic>`.
3. Write the tests the step names *and* the code. Either order; both in the PR.
4. `./gradlew testDebugUnitTest lintDebug assembleDebug` — all three, green.
5. Push, open a PR, let GLM review it, triage the findings per
   [CLAUDE.md](../../CLAUDE.md), merge.

Do the step. Not the step plus a small improvement you noticed; not two steps
because they are both small. A PR that does one thing gets a review that is
about that thing.

## Before you write a line

* Does this belong in `learn/` or `ui/`? If a `when` decides *what the app
  does* rather than *what it draws*, it is `learn/`.
* Does something already do this? `ui/components/` has the buttons and cards;
  `learn/` has stars, streaks, boxes and selection. Do not write a second
  rounded button.
* Which plan file owns this decision? Follow it. If you disagree, say so in the
  PR and change the plan file **in the same PR** — do not quietly diverge.

## Things that will bite you

* **`learn/` may not import `android.*`.** No `Context`, no
  `System.currentTimeMillis()`, no bare `Random()`. Time and randomness are
  parameters. This is not style; the whole test suite depends on it.
* **Never hand-edit `app/src/main/assets/vocab.json`.** Edit `tools/vocab.py`,
  run `python3 tools/gen_vocab.py`, commit both. CI fails on drift.
* **Never write a version number in a build file.** Only
  `gradle/libs.versions.toml`.
* **Never hand-edit `versionCode`, never create a `v*` tag by hand.**
  `scripts/release.sh` does both.
* **Add all three locales at once.** A string added to `values/` and nowhere
  else fails lint, and a "TODO translate" that reaches `main` never gets done.
* **Stop your animations.** Every loop, coroutine and timer gets cancelled in
  `onDispose` or when its effect ends. This is the app's most likely
  battery bug and its sibling shipped exactly it.
* **Swiss spelling: *ss*, never *ß*.** Everywhere, including comments.
* **`compileSdkVersion("android-37.0")` is paired with
  `android.suppressUnsupportedCompileSdk=37`.** The two move together or not
  at all. Do not "fix" either alone.
* **There is no `kotlin-android` plugin.** AGP 9 provides built-in Kotlin
  support. Applied plugins: android-application, kotlin-compose,
  kotlin-serialization, ksp, hilt. Do not add more.
* **`app/debug.keystore` is checked in on purpose** and signs both build types
  (`docs/decisions/0002`). Do not rotate it, do not add signing secrets.
* **"Works in debug, breaks in release"** is almost always a missing R8 keep
  rule for a new serialization entry point — `app/proguard-rules.pro` first.

## Writing code that fits

* Comments explain **why**, not what. The existing files are the reference:
  when something is deliberate and looks wrong, say so and say what would make
  it wrong to change.
* Match the surrounding style. Same naming, same comment density, same idiom.
* Public API in `learn/` gets a KDoc sentence saying what it is for. Private
  helpers usually need nothing.
* No `!!` outside a test. No `TODO()` on a path a learner can reach.
* No new dependency without a line in the PR body saying what it replaces and
  why we cannot write it. This app has 12 dependencies and should stay small.

## Writing a PR

Body: what changed, why, and how you proved it. Name the tests. If you departed
from the plan, say which file and why — and update it.

Do not describe the code line by line; the diff does that. Do describe anything
a reviewer would otherwise have to guess: a deliberate omission, a workaround,
a thing that looks wrong and isn't.

## Reviews

GLM 5.2 reviews every PR. Triage each finding into exactly one of:

* **apply** — a real bug or a real improvement;
* **decline with recorded reasons** — write them in the commit message and in
  [REVIEW.md](../../REVIEW.md), so a later round does not re-litigate it;
* **refute with evidence** — the claim is factually wrong; cite the code, the
  docs or an actual CI run.

Never apply a change to appease a reviewer, and never flip-flop on something
already declined without genuinely new evidence. The full policy, including
when to declare steady state and stop, is in [CLAUDE.md](../../CLAUDE.md).

## When the plan is wrong

It will be somewhere. The plan was written before the code, and code teaches
things a plan cannot know.

Say so, in the PR, plainly. Fix the plan file in that PR. Add a line to
[AGENTS.md](../../AGENTS.md) under "Conventions and footguns" if it is the kind
of thing the next person would trip over too. A plan that lies is worse than no
plan, and the fix costs five minutes.
