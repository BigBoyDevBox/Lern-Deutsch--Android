# 03 — The vocabulary

## Where words come from

`tools/vocab.py` is the source of truth, and it is **shared verbatim with the
Pebble app**. The same file, byte for byte, feeds a C watchapp and this one.
That is deliberate: a word added in either project can be copied across without
translating anything, and the two apps can never teach different things.

```
tools/vocab.py            4 tiers, 81 decks, 1204 cards  (the file you edit)
      │
      ├── tools/vocab_pos.py        Wortart classification
      ├── tools/validate_vocab.py   the rules, enforced
      └── tools/gen_vocab.py        ──> app/src/main/assets/vocab.json
```

A card in `vocab.py` is a 4-tuple:

```python
("das Brot", "面包", "bread", "n")
#  German     Chinese  English  gender: "" | "m" | "f" | "n"
```

German nouns carry their article. The gender field exists so the article can be
colour-coded; it is also what proves a card is a noun.

## Regenerating

```sh
python3 tools/gen_vocab.py          # validate + write vocab.json
python3 tools/gen_vocab.py --check  # fail if the committed JSON is stale
python3 tools/validate_vocab.py     # rules only
```

**Never hand-edit `app/src/main/assets/vocab.json`.** CI runs `--check` and
fails on drift — which is a gate the Pebble app does not have, and its review
notes list exactly that drift as an open problem (`awesome.md` 2.5).

## The JSON shape

```jsonc
{
  "schema": 1,
  "source": "tools/vocab.py",
  "tiers": [ { "id": "beginner", "de": "Anfänger", "zh": "初级",
               "en": "Beginner", "accent": "mint" } ],
  "decks": [ { "key": "greet", "tier": "beginner",
               "de": "Erste Worte", "zh": "入门", "en": "First Words",
               "accent": "lemon", "icon": "👋",
               "cards": [ { "id": "greet/Hallo", "de": "Hallo", "zh": "你好",
                            "en": "hello", "gender": "none", "pos": "other" } ] } ],
  "ui": { "praise": ["太棒了", …], "nudge": ["差一点", …] }
}
```

Mirrored in Kotlin by `learn/Vocabulary.kt`, which is both the wire format and
the domain model — one shape, no mapping layer to keep in step.

Three things the generator adds that `vocab.py` does not contain:

* **`id`** — `"<deckKey>/<german>"`. Every piece of saved progress hangs off
  this, so it must stay stable. Reordering decks and inserting cards are both
  free (the Pebble app keys progress by deck *position* and therefore may never
  reorder anything); only rewriting a German word retires its history, which is
  correct, because that is a different word.
* **`accent`** — a palette token (`sky`, `rose`, `mint`, …) remapped from the
  Pebble colour name. `CandyPaletteTest` fails if a token has no colour, or if
  a colour has no token.
* **`icon`** — an emoji, remapped from the Pebble icon id. No drawables, no
  density buckets, and it reads as cute for free.
* **`pos`** — the Wortart. See below.

`gender` is spelled out (`none` / `der` / `die` / `das`) rather than kept in
`vocab.py`'s terse `""`/`m`/`f`/`n`, because an enum constant whose serial name
is the empty string is a trap nobody should think about twice.

## Wortarten: how a word gets its class

This is the part most likely to be got wrong, so it is data, not a heuristic
(`docs/decisions/0006`). `tools/vocab_pos.py`:

| Class | Rule | Count |
|---|---|---:|
| `noun` | gender is der/die/das, **or** listed in `EXTRA_NOUNS` | 697 |
| `verb` | English gloss starts with `"to "` **and** the German is a single lower-case word | 90 |
| `adjective` | listed in `ADJECTIVES` | 115 |
| `other` | everything else | 302 |

**`other` is an honest answer, not a gap.** „und", „vielleicht", „zwanzig" and
„Wie geht's?" genuinely are none of the three. Wortarten mode never draws from
that bucket ([14](14-mode-wortarten.md)).

Why the verb rule is safe: every verb in the list is glossed as an English
infinitive and nothing else is. The single-word/lower-case guard drops the ten
phrase cards that also begin with "to " („Musik hören" → "to listen to music",
„Zum Wohl!" → "to your health!"). `validate_vocab.py` re-proves both halves on
every run — a verb glossed "eat" instead of "to eat" fails the build rather
than silently teaching a wrong Wortart.

Why adjectives are curated: German has no spelling rule separating „traurig"
from „selten". Guessing would teach something false. The list also deliberately
*excludes* defensible edge cases — adverbs of frequency, quantifiers, ordinals,
the particles „auf"/„zu" — because an edge case is the last thing a learner
needs while working out what an adjective *is*.

## Adding words

1. Add the tuple to the right deck in `tools/vocab.py`. Nouns get their
   article and the matching gender letter. Verbs get a `"to …"` gloss and
   classify themselves.
2. If it is an adjective, add it to `ADJECTIVES` in `tools/vocab_pos.py`.
3. `python3 tools/gen_vocab.py`
4. Commit `vocab.py`, `vocab_pos.py` **and** the regenerated `vocab.json`.

Adding a whole deck: append it to `GROUPS` with a `tier`, an `accent` that
`gen_vocab.py::ACCENTS` knows, and an `icon` that `ICONS` knows. Unknown ones
fail generation loudly rather than falling back to pink.

## What the validator enforces

Errors (build fails): article ↔ gender agreement in both directions; `ß`
anywhere; duplicate cards in a deck; duplicate deck keys; empty sides; control
characters; unknown tier; over-long sides; a curated word that no longer exists
in the list; a word in two curated classes; a Wortarten pool under 30 cards.

Warnings (printed, tolerated): a word taught in two decks (`gross` and `klein`
each appear twice, deliberately); a deck under 8 cards; a Chinese side with no
CJK characters.

## The Kotlin side

`VocabAssetTest` parses the **real shipped asset** — `app/build.gradle.kts`
puts `src/main/assets` on the unit-test classpath for exactly this — and
asserts what Kotlin depends on: it deserializes, ids are unique, articles match
genders, `withoutArticle` strips exactly the article, and each mode has a pool.
Everything checkable about the *word list* stays in the Python validator; this
test is about the *contract*.
