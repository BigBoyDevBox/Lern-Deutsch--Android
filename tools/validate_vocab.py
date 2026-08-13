#!/usr/bin/env python3
# coding: utf-8
"""
Sanity-check the word list against the constraints the app actually has.

Runs standalone (`python3 tools/validate_vocab.py`), from `gen_vocab.py` before
it writes anything, and as a CI step before the Gradle build. Dependency-free
on purpose — it must run anywhere Python 3 runs.

Errors fail the build. Warnings are printed and tolerated: they flag things
that are probably deliberate (a word taught in two decks) but worth seeing.

Usage: tools/validate_vocab.py [--strict]
       --strict  treat warnings as errors
"""
import re
import sys
import unicodedata
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import vocab  # noqa: E402
import vocab_pos  # noqa: E402

ARTICLE_GENDER = {"der": "m", "die": "f", "das": "n"}
VALID_GENDERS = {"", "m", "f", "n"}

# The card faces are laid out for a phone, not a 200 px watch, but a card that
# needs six lines still looks broken. These are generous, and exceeding one is
# an error rather than a warning because nothing downstream can recover.
MAX_DE = 44
MAX_ZH = 20
MAX_EN = 48

errors = []
warnings = []


def err(msg):
    errors.append(msg)


def warn(msg):
    warnings.append(msg)


def check_tiers():
    ids = [t["id"] for t in vocab.TIERS]
    if len(ids) != len(set(ids)):
        err("TIERS: duplicate tier id")
    for t in vocab.TIERS:
        for field in ("id", "de", "zh", "en"):
            if not t.get(field):
                err("TIERS: %r is missing %s" % (t.get("id"), field))
    return set(ids)


def check_decks(tier_ids):
    keys = set()
    for deck in vocab.GROUPS:
        key = deck.get("key", "?")
        if key in keys:
            err("deck %s: duplicate deck key" % key)
        keys.add(key)
        if not re.fullmatch(r"[a-z][a-z0-9]*", key):
            err("deck %s: key must be lower-case alphanumeric" % key)
        if deck.get("tier") not in tier_ids:
            err("deck %s: unknown tier %r" % (key, deck.get("tier")))
        for field in ("de", "zh", "en", "accent", "icon"):
            if not deck.get(field):
                err("deck %s: missing %s" % (key, field))
        cards = deck.get("cards") or []
        if not cards:
            err("deck %s: has no cards" % key)
        if len(cards) < 8:
            warn("deck %s: only %d cards — rounds will feel thin" % (key, len(cards)))
        check_cards(key, cards)
    return keys


def check_cards(key, cards):
    seen_de = set()
    for i, card in enumerate(cards):
        where = "deck %s card %d" % (key, i)
        if not isinstance(card, (tuple, list)) or len(card) != 4:
            # isinstance first: a stray None or int in the list would make
            # len() raise, and a traceback is the one output this tool must
            # never produce.
            err("%s: expected a 4-tuple (de, zh, en, gender)" % where)
            continue
        de, zh, en, gender = card

        for name, value in (("de", de), ("zh", zh), ("en", en)):
            if not isinstance(value, str) or not value.strip():
                err("%s: %s is empty" % (where, name))
            elif value != value.strip():
                err("%s: %s has leading/trailing whitespace" % (where, name))
            elif any(unicodedata.category(ch) == "Cc" for ch in value):
                err("%s: %s contains a control character" % (where, name))

        if de in seen_de:
            err("%s: %r appears twice in this deck" % (where, de))
        seen_de.add(de)

        if gender not in VALID_GENDERS:
            err("%s: gender %r is not one of %s" % (where, gender, sorted(VALID_GENDERS)))

        # Article and gender must agree, in both directions. A card is a noun
        # card when it is exactly "<article> <word>"; anything longer is a
        # phrase that merely happens to start with an article („die
        # Speisekarte, bitte"), and phrases carry no gender by design.
        tokens = de.split()
        if not tokens:
            continue          # already reported as empty above; tokens[0] would raise
        if gender:
            if tokens[0] not in ARTICLE_GENDER:
                err("%s: gender %r but %r has no article" % (where, gender, de))
            elif ARTICLE_GENDER[tokens[0]] != gender:
                err("%s: %r starts with %r but gender is %r"
                    % (where, de, tokens[0], gender))
        elif (len(tokens) == 2 and tokens[0] in ARTICLE_GENDER
                and tokens[1].isalpha()):
            err("%s: %r looks like a noun but carries no gender" % (where, de))

        if "ß" in de:
            err("%s: %r uses ß — this project spells Swiss (always ss)" % (where, de))

        if len(de) > MAX_DE:
            err("%s: German side is %d chars (max %d)" % (where, len(de), MAX_DE))
        if len(zh) > MAX_ZH:
            err("%s: Chinese side is %d chars (max %d)" % (where, len(zh), MAX_ZH))
        if len(en) > MAX_EN:
            err("%s: English side is %d chars (max %d)" % (where, len(en), MAX_EN))

        if not any(ord(ch) > 0x2E7F for ch in zh):
            warn("%s: Chinese side %r has no CJK characters" % (where, zh))


def well_formed(deck):
    """The cards of `deck` that check_cards did not reject outright.

    check_cards reports a malformed tuple and moves on, so everything after it
    has to filter too — unpacking a 3-tuple into four names raises ValueError
    and turns an error report into a traceback.
    """
    return [c for c in (deck.get("cards") or [])
            if isinstance(c, (tuple, list)) and len(c) == 4]


def check_pos():
    """Re-prove the two derivations vocab_pos.py relies on, and the curated sets."""
    all_de = {}
    for deck in vocab.GROUPS:
        for de, zh, en, gender in well_formed(deck):
            all_de.setdefault(de, []).append((deck.get("key", "?"), en, gender))

    # Every curated word must actually exist, or the list has rotted.
    for word in sorted(vocab_pos.ADJECTIVES):
        if word not in all_de:
            err("ADJECTIVES: %r is not in the word list" % word)
        elif not word[:1].islower():
            err("ADJECTIVES: %r is capitalised — adjectives are lower-case" % word)
    for word in sorted(vocab_pos.EXTRA_NOUNS):
        if word not in all_de:
            err("EXTRA_NOUNS: %r is not in the word list" % word)
        elif not word[:1].isupper():
            err("EXTRA_NOUNS: %r is lower-case — German nouns are capitalised" % word)

    overlap = vocab_pos.ADJECTIVES & vocab_pos.EXTRA_NOUNS
    if overlap:
        err("a word cannot be both adjective and noun: %s" % sorted(overlap))

    # The verb derivation: an infinitive gloss must never collide with a noun,
    # and a German verb always ends in -n.
    for de, uses in sorted(all_de.items()):
        for deck_key, en, gender in uses:
            if not vocab_pos.is_derived_verb(de, en):
                continue
            if gender:
                err("deck %s: %r is glossed as an infinitive but has a gender"
                    % (deck_key, de))
            if not de.endswith("n"):
                err("deck %s: %r is classified as a verb but does not end in -n"
                    % (deck_key, de))
            if de in vocab_pos.ADJECTIVES:
                err("deck %s: %r is listed as an adjective but glossed as a verb"
                    % (deck_key, de))

    counts = {}
    for deck in vocab.GROUPS:
        for de, zh, en, gender in well_formed(deck):
            pos = vocab_pos.classify(de, en, gender)
            counts[pos] = counts.get(pos, 0) + 1

    # The Wortarten mode needs a real pool in each of its three bins.
    for pos in (vocab_pos.NOUN, vocab_pos.VERB, vocab_pos.ADJECTIVE):
        if counts.get(pos, 0) < 30:
            err("Wortarten pool too small: only %d %s cards" % (counts.get(pos, 0), pos))

    for de, uses in sorted(all_de.items()):
        if len(uses) > 1:
            warn("%r is taught in %d decks: %s"
                 % (de, len(uses), ", ".join(u[0] for u in uses)))

    return counts


def check_ui():
    for name, text in sorted(vocab.UI_STRINGS.items()):
        if not text.strip():
            err("UI_STRINGS[%s] is empty" % name)
    if not vocab.UI_PRAISE or not vocab.UI_NUDGE:
        err("UI_PRAISE and UI_NUDGE must both be non-empty")


def main():
    strict = "--strict" in sys.argv[1:]

    tier_ids = check_tiers()
    check_decks(tier_ids)
    counts = check_pos()
    check_ui()

    total = sum(len(d.get("cards") or []) for d in vocab.GROUPS)
    print("-- %d tiers, %d decks, %d cards"
          % (len(vocab.TIERS), len(vocab.GROUPS), total))
    print("-- Wortarten: %s"
          % ", ".join("%s=%d" % (k, counts[k]) for k in sorted(counts)))

    for w in warnings:
        print("-- warning: %s" % w)
    for e in errors:
        print("!! %s" % e, file=sys.stderr)

    if errors:
        print("!! %d error(s)" % len(errors), file=sys.stderr)
        return 1
    if strict and warnings:
        print("!! %d warning(s) with --strict" % len(warnings), file=sys.stderr)
        return 1
    print("==> vocabulary OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
