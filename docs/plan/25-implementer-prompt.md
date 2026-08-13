# 25 — The prompt for the implementing agent

One prompt, handed over once. It drives the app from an empty scaffold to a
released v1.0.0, one roadmap step at a time, opening and merging a pull request
per step so the automated reviewer gets a look at every increment instead of
one enormous diff at the end.

Copy the block below verbatim. It is also safe to re-send after a session dies:
step 0 of the loop is working out where the work actually stopped.

---

```text
You are building the Wortkatze Android app, in the repo
L-K-M/Lern-Deutsch--Android. Wortkatze is a kawaii German-learning app for a
14-year-old whose first language is Chinese.

THE JOB: implement the whole app — roadmap steps 1 through 17 — in order,
without stopping in between. Keep going until step 17 is merged. But build it
in SEPARATE PULL REQUESTS, one per roadmap step, merging each into main before
you start the next. Never batch several steps into one PR, and never work on
step N+1 before step N is merged. The per-step PR is not bureaucracy: it is how
the automated reviewer gets to catch a mistake in step 3 before steps 4 to 17
are built on top of it.

ORIENT FIRST (once, at the start of a session):
  1. Read docs/plan/00-index.md
  2. Read docs/plan/02-architecture.md
  3. Read docs/plan/24-implementer-rules.md
  4. Work out which step is next rather than assuming: check the merged pull
     requests and `git log main --oneline`. The highest merged "Step N" means
     your next step is N+1. If none have been merged, start at step 1. (Step 0
     is the scaffolding and is already done.)
  Do not read the rest of the plan yet.

THEN REPEAT THIS LOOP until step 17 is merged:

  a. Read the entry for your step in docs/plan/22-roadmap-foundations.md
     (steps 0-9) or docs/plan/23-roadmap-modes.md (steps 10-17). Then read ONLY
     the two or three plan files that step names. Do not read the whole plan —
     it is reference material, and pulling all of it into context every step
     will crowd out the code.
  b. git checkout main && git pull
     git checkout -b claude/step-<N>-<short-topic>
  c. Implement exactly what the step specifies: the files it lists, the
     signatures it gives, the behaviour it describes.
  d. Write every test the step names under "Done when". These are not optional
     and not something to come back to. A step whose tests do not exist is not
     finished, even if the app works on a phone.
  e. Run, and do not proceed until all three pass:
         ./gradlew testDebugUnitTest lintDebug assembleDebug
     If the Android SDK is missing, run: bash .claude/setup-android.sh
  f. Commit. Push with: git push -u origin <branch>
  g. Open a PR against main, titled "Step <N>: <what it does>". In the body say
     what you built, which tests prove it, and anything you departed from.
  h. The automated reviewer (GLM 5.2) comments within about 20 minutes. Triage
     every finding into exactly one of: apply it; decline it with your reasons
     recorded in REVIEW.md; or refute it with evidence — the code, the docs, or
     an actual CI run. VERIFY A CLAIM BEFORE ACTING ON IT: the reviewer is
     sometimes confidently wrong, and has previously reported compile errors in
     code that does not exist. Never apply a change just to make it stop, and
     never re-open something REVIEW.md already records as declined.
  i. When CI is green and no valid findings remain, merge into main with a
     merge commit titled "Merge PR #NN: ...".
  j. Go back to (a) for the next step. Do not pause for permission between
     steps — the whole roadmap is the task.

AFTER STEP 17: cut the release with scripts/release.sh 1.0.0 --push and confirm
the Release workflow publishes the APK.

MANAGING YOUR CONTEXT: this is a long job. After each merged step, keep only
00-index, 02-architecture, 24-implementer-rules and the roadmap in mind; let
the finished step's plan files go. If you are running short, finish the step
you are on, merge it, and say clearly which step is next — the loop above is
written so a fresh session can pick up from a merged main with no handover
notes.

RULES THAT ARE NOT NEGOTIABLE:
  - One step per PR. If you spot a genuine improvement that is outside the
    current step's scope, do not fold it into that step's PR — but do not throw
    it away either. Finish and merge the step, then do the improvement as its
    own PR, through the same loop, before starting the next step. Title it
    "Improve: <what>" so it is obvious it is not a roadmap step. If you are not
    sure it is worth doing, note it in the step's PR body and move on — every
    improvement PR costs a full review cycle, so they should be worth one.
  - Nothing in learn/ may import android.*, take a Context, call
    System.currentTimeMillis(), or use an unseeded Random. Time and randomness
    are parameters. The entire test strategy depends on this.
  - Never hand-edit app/src/main/assets/vocab.json. Edit tools/vocab.py, run
    python3 tools/gen_vocab.py, and commit both. CI fails on drift.
  - Never put a version number in a build file. Only in
    gradle/libs.versions.toml.
  - Every user-visible string goes into all three of values/, values-de/ and
    values-b+zh+Hans/ in the same change. Lint fails otherwise.
  - Swiss spelling everywhere: always "ss", never "ß".
  - Cancel every animation, coroutine and timer in onDispose or when its
    effect ends.
  - No "!!" outside a test. No TODO() on a path a user can reach.
  - No new dependency without saying in the PR body what it replaces and why we
    cannot write it ourselves.

IF A STEP IS TOO BIG to do well in one go, split it into "Step <N>a" and
"Step <N>b" PRs and say so. A step is a unit of review, not a law about file
counts. What must not happen is a PR that implements half a step and claims the
whole one.

IF THE PLAN IS WRONG: it will be, somewhere. Say so plainly in the PR body, fix
the plan file in the SAME PR, and add a line to AGENTS.md under "Conventions
and footguns" if the next person would trip on it too. Do not quietly diverge
from the plan, and do not follow a spec you can see is broken.
```

---

## What the loop produces

Seventeen pull requests, each reviewed before the next is built on it. Four
checkpoints worth watching from outside:

| After step | What exists |
|---|---|
| **3** | Nothing visible — components, the saved-progress document, the scheduler. All unit tests. |
| **5** | A home screen, real navigation, and a playable round with a throwaway face. **First time the app does something.** |
| **9** | Mimi, confetti, sound, haptics, XP and stickers — all the feedback, no real mode yet. |
| **17** | All five modes, the album, settings, pronunciation, v1.0.0 released. |

The dullest-looking stretch, steps 1–3, is the one worth protecting. Every mode
later is small precisely because the scheduler, the progress document and the
components already exist and are tested — skip ahead and each mode grows its
own half-correct copy of all three.

Steps 5, 10 and 14 are the largest; expect those to be the ones that get split.

## Handing over a single step instead

If you ever want just one step done — a fix, a retry, a second opinion — the
same block works: replace the JOB paragraph with *"Implement roadmap step N,
and nothing else"*, and drop the loop's step (j).
