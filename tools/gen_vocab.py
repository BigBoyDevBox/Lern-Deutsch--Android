#!/usr/bin/env python3
# coding: utf-8
"""
Generate app/src/main/assets/vocab.json from tools/vocab.py.

`vocab.py` is the ONE place words live, and it is shared verbatim with the
Pebble app (L-K-M/Lern-Deutsch--Pebble) so a word added in either project can
be copied across without translation. This script turns it into the asset the
Android app parses at startup:

  * Wortart per card, from `vocab_pos.py`
  * Pebble accent colour names remapped onto this app's candy palette
  * icon ids remapped onto emoji (no drawable assets to keep in sync)
  * a stable card id per card

Run it after editing `vocab.py`, and commit the regenerated JSON:

    python3 tools/gen_vocab.py

CI regenerates and `git diff --exit-code`s the result, so a forgotten
regeneration fails the build instead of shipping stale words. The output is
therefore required to be byte-stable: no timestamps, no dict iteration order,
fixed indent, LF endings, UTF-8 written through (the file is meant to be
readable in a diff).

Usage: tools/gen_vocab.py [--check]
       --check  validate and report, write nothing (exit 1 if the file on disk
                differs from what would be generated)
"""
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "tools"))

import vocab  # noqa: E402
import vocab_pos  # noqa: E402
import validate_vocab  # noqa: E402

OUT = ROOT / "app" / "src" / "main" / "assets" / "vocab.json"

SCHEMA_VERSION = 1

# ---------------------------------------------------------------------------
# Pebble's GColor names -> this app's palette tokens (ui/theme/CandyPalette.kt).
# The watch had 64 colours and no gradients; the phone has a designed palette,
# so this is a remap and not a conversion. Every name in vocab.py must appear
# here or generation fails loudly — a missing accent would silently flatten a
# deck to the fallback colour.
# ---------------------------------------------------------------------------
ACCENTS = {
    "BrilliantRose": "rose",
    "ChromeYellow": "honey",
    "Cyan": "aqua",
    "Folly": "cherry",
    "Green": "mint",
    "KellyGreen": "clover",
    "Magenta": "orchid",
    "Melon": "peach",
    "MintGreen": "mint",
    "Orange": "tangerine",
    "PictonBlue": "sky",
    "Purple": "grape",
    "Rajah": "apricot",
    "Red": "cherry",
    "SpringBud": "lime",
    "VividCerulean": "sky",
    "Yellow": "lemon",
}

# Deck icons as emoji: no drawables to draw, no density buckets to maintain,
# and they read as "cute" for free. Kept in the generator rather than in Kotlin
# so the icon vocabulary is validated at build time, not at runtime.
ICONS = {
    "bag": "👜", "ball": "⚽", "book": "📚", "box": "📦",
    "building": "🏢", "calendar": "📅", "car": "🚗", "clock": "🕐",
    "cloud": "☁️", "cup": "🍵", "fork": "🍴", "globe": "🌍",
    "heart": "💖", "house": "🏠", "leaf": "🍃", "num": "🔢",
    "palette": "🎨", "paw": "🐾", "people": "👨‍👩‍👧", "phone": "📞",
    "plane": "✈️", "plus": "➕", "run": "🏃", "shirt": "👕",
    "speech": "💬", "sun": "☀️", "warning": "⚠️", "wave": "👋",
}


def accent(name, where):
    if name not in ACCENTS:
        sys.exit("!! %s: unknown accent %r — add it to ACCENTS" % (where, name))
    return ACCENTS[name]


def icon(name, where):
    if name not in ICONS:
        sys.exit("!! %s: unknown icon %r — add it to ICONS" % (where, name))
    return ICONS[name]


# vocab.py encodes gender the way the Pebble C code wanted it ("" / m / f / n).
# The JSON spells the article out instead: the Kotlin side deserializes it into
# an enum, and an enum constant whose serial name is the empty string is a trap
# nobody should have to think about twice.
GENDERS = {"": "none", "m": "der", "f": "die", "n": "das"}


def gender_name(code, where):
    if code not in GENDERS:
        sys.exit("!! %s: unknown gender %r — add it to GENDERS" % (where, code))
    return GENDERS[code]


def card_id(deck_key, german):
    """Stable identity for one card, used as the SRS progress key.

    Deck key + German text, NOT an index: the Pebble app keys progress by deck
    POSITION and so can never reorder its decks. Here reordering and inserting
    are both free, and only rewriting a German word retires its progress —
    which is the right outcome, because that is a different word.
    """
    return "%s/%s" % (deck_key, german)


def build():
    tiers = [
        {
            "id": t["id"],
            "de": t["de"],
            "zh": t["zh"],
            "en": t["en"],
            "accent": accent(t["accent"], "tier %s" % t["id"]),
        }
        for t in vocab.TIERS
    ]

    decks = []
    for d in vocab.GROUPS:
        where = "deck %s" % d["key"]
        cards = []
        for de, zh, en, gender in d["cards"]:
            cards.append({
                "id": card_id(d["key"], de),
                "de": de,
                "zh": zh,
                "en": en,
                "gender": gender_name(gender, "%s/%s" % (d["key"], de)),
                "pos": vocab_pos.classify(de, en, gender),
            })
        decks.append({
            "key": d["key"],
            "tier": d["tier"],
            "de": d["de"],
            "zh": d["zh"],
            "en": d["en"],
            "accent": accent(d["accent"], where),
            "icon": icon(d["icon"], where),
            "cards": cards,
        })

    return {
        "schema": SCHEMA_VERSION,
        "source": "tools/vocab.py",
        "tiers": tiers,
        "decks": decks,
        "ui": {
            "praise": list(vocab.UI_PRAISE),
            "nudge": list(vocab.UI_NUDGE),
        },
    }


def render(doc):
    # ensure_ascii=False keeps Chinese and umlauts readable in a diff; the
    # trailing newline keeps the file POSIX-clean.
    return json.dumps(doc, ensure_ascii=False, indent=2, sort_keys=False) + "\n"


def main():
    check_only = "--check" in sys.argv[1:]

    if validate_vocab.main() != 0:
        return 1

    text = render(build())

    if check_only:
        current = OUT.read_text(encoding="utf-8") if OUT.exists() else ""
        if current != text:
            print("!! %s is stale — run tools/gen_vocab.py" % OUT.relative_to(ROOT),
                  file=sys.stderr)
            return 1
        print("==> %s is up to date" % OUT.relative_to(ROOT))
        return 0

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(text, encoding="utf-8", newline="\n")
    print("==> wrote %s (%.1f KB)" % (OUT.relative_to(ROOT), len(text.encode()) / 1024))
    return 0


if __name__ == "__main__":
    sys.exit(main())
