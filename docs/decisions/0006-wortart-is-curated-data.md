# 0006 — Wortart is curated data, not a runtime heuristic

- **Status:** accepted
- **Date:** 2026-08-13

## Context

The Wortarten mode teaches Nomen / Verben / Adjektive, but the shared word list
(`tools/vocab.py`, see 0001) has no word-class field — the Pebble app never
needed one.

Three ways to get one: infer it in Kotlin at runtime from spelling; infer it at
build time with the same rules; or curate it.

Inference is tempting because German gives hints — nouns are capitalised, verbs
end in `-en`. Both hints are wrong often enough to matter in this exact data
set. „Danke", „Hallo", „Vielleicht" are capitalised and are not nouns
(they open their card's phrase). „sieben", „trocken", „offen", „modern",
„selten" end in `-en` and are a numeral, three adjectives and an adverb. A mode
whose job is to teach word classes cannot be wrong about word classes.

## Decision

The Wortart is computed **at build time**, in `tools/vocab_pos.py`, and baked
into `vocab.json` as a `pos` field. Nothing is inferred at runtime.

Two derivations that cannot be wrong, plus two curated sets:

| Class | Rule |
|---|---|
| `noun` | the card has a gender (its article proves it), or it is listed in `EXTRA_NOUNS` |
| `verb` | the English gloss starts with `"to "` **and** the German is a single lower-case word |
| `adjective` | listed in `ADJECTIVES` |
| `other` | everything else |

`tools/validate_vocab.py` re-proves both derivations on every run: a curated
word that no longer exists in the list fails the build, a capitalised
"adjective" fails, a verb that does not end in `-n` fails, and a card glossed as
an infinitive that also carries a gender fails.

## Consequences

- **`other` is a first-class answer**, not a gap. 302 cards — pronouns,
  numerals, particles, adverbs, interjections, whole phrases — are genuinely
  none of the three, and the mode simply never draws from that bucket.
- Adding an adjective is a two-line change in a Python file, and the build
  tells you if you got it wrong. Adding a verb is free: gloss it "to …".
- The curated lists deliberately exclude defensible edge cases (adverbs of
  frequency, quantifiers, ordinals, the particles „auf"/„zu"). A grammar book
  would argue about several of them; a 14-year-old learning what an adjective
  *is* should not meet them. That exclusion is a pedagogical choice recorded
  here so it is not "fixed" later by someone reading a grammar book.
- The pools are unbalanced (697 nouns, 90 verbs, 115 adjectives), so the mode
  weights its draw rather than sampling uniformly — see
  [14](../plan/14-mode-wortarten.md).
- Revisit if the word list ever gains a real part-of-speech field upstream, at
  which point this file becomes a validator instead of a source.
