# 13 — Der Die Das · 冠词

Built in **roadmap step 11**. The gender drill.

## Why this mode exists

Chinese has no grammatical gender at all. There is no rule in German that
predicts it reliably, no cognate to lean on, and no way to reason it out — it
has to become a reflex. A reflex is built by high-frequency, low-latency,
immediately-corrected repetition, which is exactly what this mode is and what a
flashcard is not.

662 cards carry an article, so the pool is large enough that a round never
repeats.

## The loop

1. A noun appears **without its article** — `Card.withoutArticle`.
2. Three big buttons: **der** (blue), **die** (pink), **das** (green).
3. Tap. Correct → the article snaps into place in front of the noun in its
   colour, confetti, next. Wrong → the wrong button dims, the right one lights
   up and the full „**die** Katze" is shown until she taps on.
4. The card comes back later in the round if missed ([06](06-round-engine.md)).

Latency is the whole design: from tap to next question should be under 700 ms.

## The three buttons

Bottom third, equal width, 72dp tall, `extraLarge` corners, filled with
`Candy.Der` / `Candy.Die` / `Candy.Das`, label in white `displayMedium`.

**Always in the order der, die, das** — never shuffled. The colours and the
positions are both memory hooks, and shuffling would destroy them to prevent a
kind of cheating that cannot happen (there is no positional pattern to learn;
the answer is the word).

Colour is never the only signal ([20](20-accessibility.md)): each button says
its article. Under each, in small type, the localized gender name — „männlich ·
阳性", „weiblich · 阴性", „sächlich · 中性" — which quietly teaches the three
grammar words as well.

## The prompt card

The noun in `displayLarge`, centred, with a visible gap in front of it where
the article will go. On a correct answer the article **flies into that gap**
with a spring — the single most satisfying thing in the app, and worth building
carefully.

Under the noun: the Chinese, in `headlineMedium`. The learner should be
learning gender *and* meaning at once, not gender in a vacuum.

## Difficulty

The round is built by `Selection.forMode(vocabulary, { it.gender != Gender.NONE },
…)`, so due cards come first and struggled-with nouns come back
([05](05-scheduler.md)).

Do **not** add a "hard mode" that hides the Chinese, and do not weight the pool
toward one gender to "balance" it. German's real distribution (240 der / 266
die / 156 das in this word list) is the distribution worth internalizing.

## Files

```
ui/round/artikel/ArtikelFace.kt
learn/round/ArtikelQuestion.kt
```

```kotlin
fun artikelQuestion(card: Card, repeat: Boolean): Question {
    require(card.gender != Gender.NONE) { "artikelQuestion needs a noun: ${card.id}" }
    val choices = listOf("der", "die", "das")
    return Question(
        card = card,
        prompt = card.withoutArticle,
        promptLang = Lang.DE,
        answer = card.gender.article,       // "der" | "die" | "das"
        answerLang = Lang.DE,
        choices = choices,
        correctIndex = choices.indexOf(card.gender.article),
        repeat = repeat,
    )
}
```

Add `val Gender.article: String` to `learn/Vocabulary.kt` — `DER -> "der"` and
so on, `NONE -> ""`.

## Done when

* `ArtikelQuestionTest`: the prompt never contains an article; `correctIndex`
  points at the right one for all three genders; a non-noun card throws.
* `SelectionTest` covers the noun filter returning only gendered cards.
* Playing a round: tapping the right article animates it into the gap; tapping
  a wrong one shows the right answer and does not advance until she taps on.
