# 0001 — Share the Pebble app's word list verbatim

- **Status:** accepted
- **Date:** 2026-08-13

## Context

The sibling project [L-K-M/Lern-Deutsch--Pebble](https://github.com/L-K-M/Lern-Deutsch--Pebble)
already carries a curated German ⇄ Chinese vocabulary: 4 tiers, 81 decks, 1204
cards, each a `(German, Chinese, English, gender)` tuple, with nouns carrying
their article and a validator enforcing the rules. It took real work to build
and it is good.

This app needs the same words. The options were to fork the list, to import it
at build time from the other repository, or to copy the file and keep the two
in sync by hand.

## Decision

`tools/vocab.py` is **copied verbatim** into this repository and treated as the
source of truth here too. Both apps hold byte-identical copies; a word added in
either can be copied across with no translation and no format change.

A generator (`tools/gen_vocab.py`) turns it into
`app/src/main/assets/vocab.json`, adding what only this app needs: a stable
card id, a Wortart, a palette token and an emoji. CI regenerates and diffs, so
the JSON can never drift from the Python.

Cross-repo submodules, a package, or a build-time fetch were rejected: they
make an offline build depend on a network and a second repository, for a file
that changes a few times a year.

## Consequences

- The two apps can never teach different things for the same card.
- Keeping them in sync is a manual copy. That is acceptable at this change
  rate and is written into AGENTS.md; the cost of forgetting is that one app
  lags, not that either breaks.
- The word list's constraints are the Pebble's constraints — deck sizes, Swiss
  spelling, an English gloss on every card. They happen to suit this app, but
  they were not chosen for it.
- The Pebble app keys saved progress by deck *position* and therefore may never
  reorder `GROUPS`. This app keys by `deckKey/german`, so it is free to
  reorder — but must not, while the file is shared.
- Revisit if the lists genuinely need to diverge (e.g. this app adds plural
  forms the watch cannot render). At that point the shared file becomes a
  shared *core* plus per-app extensions, which is a bigger change than it
  sounds.
