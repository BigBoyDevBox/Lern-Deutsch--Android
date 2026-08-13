# coding: utf-8
"""
Wortarten (part-of-speech) classification for the vocabulary.

`vocab.py` is shared verbatim with the Pebble app and carries no word-class
field, so the Wortarten game mode gets its labels from here instead. The rule
is deliberately boring: two derivations that cannot be wrong, plus two curated
sets, and everything else stays `other`.

    noun       gender is m/f/n            (662 cards — the article proves it)
               or the German is listed in EXTRA_NOUNS below
    verb       the English gloss starts with "to " AND the German is a single
               lower-case word            (91 cards)
    adjective  the German is listed in ADJECTIVES below
    other      everything else — pronouns, numerals, particles, adverbs,
               interjections, whole phrases

`other` is not a failure state. It is the honest answer for „und", „vielleicht"
and „Wie geht's?", and the Wortarten mode simply never draws from it.

Why the verb rule works: every verb in the word list is glossed in English
infinitive form ("to eat"), and nothing else is. The single-word/lower-case
guard drops the ten phrase cards that also start with "to " ("Musik hören" →
"to listen to music", "Zum Wohl!" → "to your health!"). `validate_vocab.py`
re-proves both halves of that claim on every run, so if someone adds a verb
glossed "eat" the build fails instead of silently teaching a wrong Wortart.

Adding words? Put verbs in `vocab.py` with a "to …" gloss and they classify
themselves. Adjectives must be listed here by hand — German has no spelling
rule that separates „traurig" from „selten", and guessing would teach the
learner something false.
"""

# ---------------------------------------------------------------------------
# Adjectives. Lower-case, single word, and genuinely an adjective — a word
# that can stand in front of a noun („ein *trauriger* Tag").
#
# Deliberately EXCLUDED, though a grammar book might argue: adverbs of time and
# frequency (oft, selten, täglich, bald, plötzlich), quantifiers (viel, wenig,
# mehr, genug), ordinals (erste … letzte), directions (links, rechts) and the
# particles „auf"/„zu". They are all defensible edge cases, and an edge case is
# the last thing a 14-year-old needs while learning what an adjective IS.
# ---------------------------------------------------------------------------
ADJECTIVES = {
    # Essen & Trinken / Alltag
    "lecker", "hungrig", "durstig",
    "gut", "schlecht", "gross", "klein",
    # Farben
    "rot", "blau", "grün", "gelb", "schwarz", "weiss", "orange", "lila",
    "rosa", "braun", "grau", "hell", "dunkel",
    # Wetter
    "warm", "kalt", "heiss", "nass", "trocken", "sonnig", "windig",
    "bewölkt", "neblig",
    # Gefühle
    "glücklich", "traurig", "müde", "krank", "wütend",
    "aufgeregt", "enttäuscht", "stolz", "eifersüchtig", "überrascht",
    "verliebt", "einsam", "ängstlich", "gelangweilt", "zufrieden",
    "dankbar", "erschöpft", "verwirrt", "begeistert", "besorgt", "nervös",
    # Gegensätze
    "schön", "hässlich", "neu", "alt", "jung", "reich", "arm",
    "stark", "schwach", "einfach", "schwer",
    "richtig", "falsch", "leer", "voll", "offen", "geschlossen",
    "laut", "leise",
    # Einkaufen / Weg / Gesundheit
    "teuer", "billig", "weit", "nah", "gesund", "wichtig", "beschäftigt",
    # Aussehen
    "dick", "dünn", "hübsch", "süss", "attraktiv",
    # Charakter
    "freundlich", "nett", "höflich", "ehrlich", "fleissig", "faul",
    "klug", "dumm", "mutig", "schüchtern", "geduldig", "grosszügig",
    "ruhig", "lustig", "ernst", "streng",
    # Beschreiben
    "rund", "eckig", "weich", "hart", "glatt", "rau", "scharf", "stumpf",
    "sauber", "schmutzig", "frisch", "modern", "altmodisch",
    "breit", "schmal", "flach",
}

# ---------------------------------------------------------------------------
# Nouns the gender field cannot prove: capitalised words that carry no article
# in the word list. Weekdays, months, countries and language names are all
# Nomen, and so are „die Entschuldigung" and „der Glückwunsch".
#
# Deliberately EXCLUDED: „Hallo", „Danke", „Bitte", „Ja", „Nein", „Prost",
# „Genau", „Natürlich", „Vielleicht", „Schade", „Willkommen" — capitalised
# because they open a sentence, not because they are nouns. Also „Hilfe!" and
# „Vorsicht!", which are nouns wearing an exclamation mark; the mode would have
# to strip punctuation to show them, so they stay out.
# ---------------------------------------------------------------------------
EXTRA_NOUNS = {
    "Entschuldigung", "Glückwunsch",
    # Wochentage
    "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag",
    "Samstag", "Sonntag",
    # Monate
    "Januar", "Februar", "März", "April", "Mai", "Juni",
    "Juli", "August", "September", "Oktober", "November", "Dezember",
    # Länder
    "Deutschland", "China", "Frankreich", "Japan", "Russland", "Mexiko",
    # Sprachen
    "Deutsch", "Chinesisch", "Französisch", "Japanisch", "Russisch",
    "Englisch", "Spanisch", "Italienisch",
}

NOUN, VERB, ADJECTIVE, OTHER = "noun", "verb", "adjective", "other"


def is_derived_verb(german, english):
    """The verb rule, in one place so the generator and validator agree."""
    return (
        english.startswith("to ")
        and " " not in german
        and german[:1].islower()
    )


def classify(german, english, gender):
    """Return the Wortart for one card. `other` is a valid answer, not a failure.

    Precondition: `german` and `english` are strings (possibly empty), never
    None — `validate_vocab.py` rejects a card with a missing side before
    anything calls this. Empty strings are handled and classify as `other`.
    """
    if gender in ("m", "f", "n"):
        return NOUN
    if german in EXTRA_NOUNS:
        return NOUN
    if is_derived_verb(german, english):
        return VERB
    if german in ADJECTIVES:
        return ADJECTIVE
    return OTHER
