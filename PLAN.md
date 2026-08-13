# Wortkatze — the plan

The design lives in [`docs/plan/`](docs/plan/), split into small files on
purpose: each one is short enough to read in a sitting, review in a PR, and
implement without holding the rest of the app in your head.

**Start at [`docs/plan/00-index.md`](docs/plan/00-index.md).** It explains the
reading order, the rules that never bend, and how the roadmap works.

## The short version

Wortkatze is a kawaii German-learning app for a 14-year-old with Chinese as her
first language, who also needs English. It is the phone-sized successor to the
Pebble watch app [*Lern Deutsch*](https://github.com/L-K-M/Lern-Deutsch--Pebble)
and shares its word list verbatim — 4 tiers, 81 decks, 1204 cards, each one a
`(German, Chinese, English, gender)` tuple.

Five game modes, one shared round engine:

| Mode | Teaches | Plan |
|---|---|---|
| **Karten** | vocabulary, self-graded flip cards | [12](docs/plan/12-mode-karten.md) |
| **Der Die Das** | noun gender, colour-coded | [13](docs/plan/13-mode-artikel.md) |
| **Wortarten** | Nomen / Verben / Adjektive | [14](docs/plan/14-mode-wortarten.md) |
| **Quiz** | four-choice recall, German ⇄ Chinese ⇄ English | [15](docs/plan/15-mode-quiz.md) |
| **Blitz** | 60-second mixed time attack with combos | [16](docs/plan/16-mode-blitz.md) |

Everything under them: a Leitner scheduler that decides what to ask, a progress
document that remembers, and a lot of confetti.

## Map

| | |
|---|---|
| **Why and for whom** | [01 product](docs/plan/01-product.md) |
| **How it is built** | [02 architecture](docs/plan/02-architecture.md) · [03 vocabulary](docs/plan/03-vocabulary.md) · [04 progress](docs/plan/04-progress-store.md) · [05 scheduler](docs/plan/05-scheduler.md) · [06 round engine](docs/plan/06-round-engine.md) |
| **How it looks and feels** | [07 design language](docs/plan/07-design-language.md) · [08 juice](docs/plan/08-juice.md) · [09 mascot](docs/plan/09-mascot.md) |
| **Screens** | [10 navigation](docs/plan/10-navigation.md) · [11 home](docs/plan/11-home-screen.md) · [12–16 modes](docs/plan/) · [17 rewards](docs/plan/17-rewards.md) |
| **Platform** | [18 audio & haptics](docs/plan/18-audio-haptics.md) · [19 localization](docs/plan/19-localization.md) · [20 accessibility](docs/plan/20-accessibility.md) · [21 testing](docs/plan/21-testing.md) |
| **What to build, in order** | [22 roadmap I](docs/plan/22-roadmap-foundations.md) · [23 roadmap II](docs/plan/23-roadmap-modes.md) · [24 implementer rules](docs/plan/24-implementer-rules.md) · [25 implementer prompt](docs/plan/25-implementer-prompt.md) |

Decisions that were hard to reverse are recorded as ADRs in
[`docs/decisions/`](docs/decisions/). Deviations from this plan get written
back into it, and into [AGENTS.md](AGENTS.md), in the same PR that makes them.
