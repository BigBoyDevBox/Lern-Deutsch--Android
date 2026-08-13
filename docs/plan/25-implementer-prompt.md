# 25 — The prompt for an implementing agent

Copy the block below into a fresh session, replace `<N>`, and send it. One step
per session: a fresh context per step is a feature, not a limitation — it is
what keeps a smaller model from drifting.

---

```text
You are implementing one step of the Wortkatze Android app, in the repo
L-K-M/Lern-Deutsch--Android.

YOUR TASK: roadmap step <N>, and nothing else.

READ THESE FOUR FILES FIRST, IN THIS ORDER, AND NOTHING ELSE YET:
  1. docs/plan/00-index.md
  2. docs/plan/02-architecture.md
  3. docs/plan/24-implementer-rules.md
  4. The step-<N> entry in docs/plan/22-roadmap-foundations.md (steps 0-9)
     or docs/plan/23-roadmap-modes.md (steps 10-17).

Then read ONLY the two or three plan files that step <N> names. Do not read the
whole plan; it is reference material, not preamble, and reading all of it will
crowd out the code.

THEN:
  1. Create the branch: git checkout -b claude/step-<N>-<short-topic>
  2. Implement exactly what step <N> specifies — the files it lists, the
     signatures it gives, the behaviour it describes.
  3. Write every test the step names under "Done when". These are not optional
     and they are not a suggestion for later. A step whose tests do not exist
     is not finished, even if the app works on a phone.
  4. Run, and do not stop until all three pass:
         ./gradlew testDebugUnitTest lintDebug assembleDebug
     If the Android SDK is missing, run: bash .claude/setup-android.sh
  5. Commit, push with: git push -u origin <branch>
  6. Open a PR against main. Title: "Step <N>: <what it does>". In the body say
     what you built, which tests prove it, and anything you departed from.
  7. An automated reviewer (GLM 5.2) will comment within ~20 minutes. Triage
     every finding into exactly one of: apply it, decline it with your reasons
     recorded in REVIEW.md, or refute it with evidence (the code, the docs, an
     actual CI run). Verify a claim before acting on it — the reviewer is
     sometimes confidently wrong. Never apply a change just to make it stop.
  8. When CI is green and the review has no remaining valid findings, merge
     into main with a merge commit titled "Merge PR #NN: ...".

RULES THAT ARE NOT NEGOTIABLE:
  - Do step <N> ONLY. Not step <N> plus a small improvement you noticed, not
    two steps because they look small. If you find something else worth doing,
    write it in the PR body as a follow-up and leave it alone.
  - Nothing in learn/ may import android.*, take a Context, call
    System.currentTimeMillis(), or use an unseeded Random. Time and randomness
    are parameters. The entire test strategy depends on this.
  - Never hand-edit app/src/main/assets/vocab.json. Edit tools/vocab.py, run
    python3 tools/gen_vocab.py, and commit both. CI fails on drift.
  - Never put a version number in a build file. Only in
    gradle/libs.versions.toml.
  - Every user-visible string goes into all three of values/,
    values-de/ and values-b+zh+Hans/ in the same change. Lint fails otherwise.
  - Swiss spelling everywhere: always "ss", never "ß".
  - Cancel every animation, coroutine and timer in onDispose or when its effect
    ends.
  - No "!!" outside a test. No TODO() on a path a user can reach.
  - No new dependency without saying in the PR body what it replaces and why we
    cannot write it ourselves.

IF THE PLAN IS WRONG: it will be, somewhere. Say so plainly in the PR body, fix
the plan file in the SAME PR, and add a line to AGENTS.md under "Conventions
and footguns" if the next person would trip on it too. Do not quietly diverge
from the plan, and do not follow a spec you can see is broken.

IF YOU GET STUCK: say exactly what you tried and what happened. Do not invent
an alternative design to route around a blocker.
```

---

## Milestones, for whoever is watching from outside

Every step is its own PR, so the reviewer runs on all of them. Four natural
checkpoints:

| After step | What exists |
|---|---|
| **3** | No visible change — components, saved progress, and the scheduler. This is the foundation the rest stands on, and it is entirely unit tests. |
| **5** | A home screen, real navigation, and a playable round with a throwaway face. **First time the app does something.** |
| **9** | Mimi, confetti, sound, haptics, XP and stickers — all the feedback, no real mode yet. |
| **17** | All five modes, the album, settings, pronunciation, and v1.0.0 released. |

Steps 0–9 are in [22](22-roadmap-foundations.md), steps 10–17 in
[23](23-roadmap-modes.md).

The dullest-looking stretch (steps 1–3) is the one worth protecting. Every mode
later is small precisely because the scheduler, the progress document and the
components already exist and are tested — skip ahead and each mode grows its
own half-correct copy of all three.

## If a step is too big for the model doing it

Split it, and say so in the PR. A step is a unit of *review*, not a law about
file counts: "step 12a: Distractors + tests" and "step 12b: the Quiz face" are
two fine PRs. What must not happen is a PR that implements half a step and
claims the whole one, or a step that lands without the tests its "Done when"
section names.

Steps 5, 10 and 14 are the largest. Expect to split those first.
