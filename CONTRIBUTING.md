# Contributing

Read first, in order: [PLAN.md](PLAN.md) → [`docs/plan/00-index.md`](docs/plan/00-index.md)
(what and why), [AGENTS.md](AGENTS.md) (how this repo operates),
[CICD.md](CICD.md) (pipelines). Implementers working through the roadmap should
also read [`docs/plan/24-implementer-rules.md`](docs/plan/24-implementer-rules.md).

## Ground rules

- **Versions live in `gradle/libs.versions.toml` only.** No ad-hoc pins in
  build files; no version numbers restated in docs.
- **`learn/` stays pure.** JVM-only Kotlin: no Android imports, injectable time
  and randomness, exhaustively unit-tested. UI and data layers stay thin.
- **The word list is generated.** Edit `tools/vocab.py`, run
  `python3 tools/gen_vocab.py`, commit both. Never hand-edit `vocab.json`.
- **No secrets in the repo.** The checked-in debug keystore is the sole,
  documented exception (docs/decisions/0002).
- **No new permissions.** Especially not INTERNET (docs/decisions/0004). A
  permission is a product decision → ADR first.
- **Three locales or none.** A string added to `values/` must be added to
  `values-de/` and `values-b+zh+Hans/` in the same change; lint enforces it.
- Lint and tests are hard gates; `./gradlew testDebugUnitTest lintDebug
  assembleDebug` must pass before pushing.
- Branches: `claude/<topic>` or `fable/<topic>`; merge commits titled
  `Merge PR #NN: …` (the history convention).
- **Keep files small**, docs especially. The automated reviewer fails on large
  files, so a 2000-line document gets no review at all. Split it instead.

## One step, one PR

The roadmap ([22](docs/plan/22-roadmap-foundations.md),
[23](docs/plan/23-roadmap-modes.md)) is a sequence of PR-sized steps, ordered so
each leaves `main` shippable. Do the step you are on — not the step plus an
improvement you noticed, and not two steps because they look small.

A step is finished when the tests it names exist and pass. Not when it works on
a phone.

## Architecture decisions

Significant, hard-to-reverse choices get a numbered ADR in `docs/decisions/`
(template there). An ADR records context, the decision, and consequences —
including what would make us revisit it.

## When the plan is wrong

Fix the plan file in the same PR that discovers it, and say so in the PR body.
Add a line to [AGENTS.md](AGENTS.md) under "Conventions and footguns" if it is
the kind of thing the next person would trip over too.

## Review loop

Every PR is auto-reviewed by GLM 5.2. Maintainers (usually agents) triage each
finding per [CLAUDE.md](CLAUDE.md): apply, decline with recorded reasons (in
[REVIEW.md](REVIEW.md)), or refute with evidence. Don't flip-flop on recorded
declines without genuinely new evidence.
