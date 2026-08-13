# 14 — Wortarten · 词性

Built in **roadmap step 13**. Nomen, Verben, Adjektive.

## Why this mode exists

School asks for it by name, and the three words are the vocabulary of every
grammar explanation she will read for years. Knowing what „Adjektiv" means is
worth more than knowing any ten nouns.

## The honest problem, and what to do about it

**German capitalizes its nouns.** So a mode that shows „Hund" and asks for the
Wortart is partly solved by the first letter.

Do not hide this. It is the single most useful spelling rule in German, and the
mode should teach it out loud:

> „Nomen schreibt man gross. · 名词首字母大写。"

Show that line on the mode's first screen, once, and then let the capital
letter be a legitimate clue. The real work in this mode is **Verb vs.
Adjektiv**, where capitalization tells you nothing — „tanzen" and „traurig"
look alike and are not.

Two consequences for the round builder:

* **Weight the pool.** Roughly 30 % nouns, 35 % verbs, 35 % adjectives. Nouns
  are the easy third and exist to make the mode feel winnable, not to fill it.
  The raw data is 697/90/115, so an unweighted draw would be 77 % nouns and the
  mode would be trivial and boring.
* **Never draw from `other`.** Pronouns, numerals, particles and phrases are
  genuinely none of the three ([03](03-vocabulary.md)).

The capitalization caveat also means: **do not "helpfully" lower-case the
prompt.** „hund" is not a German word, and teaching a learner to read it is
worse than the clue you removed.

## The loop

1. A word appears, alone, big.
2. Three buttons: **Nomen** (`sky`), **Verb** (`tangerine`), **Adjektiv**
   (`grape`). Each with its localized name and an example underneath in small
   type — „der Hund", „laufen", „schön". The examples are constant, and they
   are half the teaching.
3. Tap. Correct → confetti and the word gains a small badge showing its class.
   Wrong → the right button lights up and a one-line reason appears:

   | Class | Reason line |
   |---|---|
   | Nomen | „**Hund** — man schreibt es gross. · 名词，大写。" |
   | Verb | „**tanzen** — man kann es *tun*. · 动词，表示动作。" |
   | Adjektiv | „**traurig** — es beschreibt etwas. · 形容词，描述性质。" |

   The reason is why this mode teaches instead of merely testing. Build it as
   a string resource per class, not a sentence assembled in Kotlin.

## The weighted draw

```kotlin
object WortartenPool {
    val WEIGHTS = mapOf(
        Wortart.NOUN to 0.30,
        Wortart.VERB to 0.35,
        Wortart.ADJECTIVE to 0.35,
    )

    fun build(
        vocabulary: Vocabulary,
        progress: Progress,
        today: Long,
        size: Int,
        random: Random,
    ): List<Card>
}
```

Take verbs and adjectives first, at `round(size * weight)` each, from
`Selection.forMode(vocabulary, { it.pos == klass }, …)`. Then take nouns as
**whatever is left**: `size - verbs - adjectives`. If a class comes up short,
top up from the others in the order verb → adjective → noun. Shuffle the result
once at the end.

Rounding three weights independently does not sum to `size` (at 16 it gives
5 + 6 + 6 = 17), so exactly one class has to absorb the remainder. Nouns do,
because they are the easy third — a rounding error should cost the mode a
little difficulty, never a card.

## Files

```
ui/round/wortarten/WortartenFace.kt
learn/round/WortartenQuestion.kt   wortartenQuestion() + WortartenPool
```

```kotlin
fun wortartenQuestion(card: Card, repeat: Boolean): Question {
    require(card.pos in Wortart.PLAYABLE) { "not playable: ${card.id} is ${card.pos}" }
    val choices = Wortart.PLAYABLE           // NOUN, VERB, ADJECTIVE — fixed order
    return Question(
        card = card,
        prompt = card.de,                    // full German, article and all
        promptLang = Lang.DE,
        answer = card.pos.name,
        answerLang = Lang.DE,
        choices = choices.map { it.name },
        correctIndex = choices.indexOf(card.pos),
        repeat = repeat,
    )
}
```

Note the prompt is `card.de`, **with** the article for nouns. „der Hund" makes
the Nomen answer obvious, which is fine — see above — and it keeps the learner
seeing correct German.

## Done when

* `WortartenQuestionTest`: `correctIndex` is right for a noun, a verb and an
  adjective; an `other` card throws; choices are always in the fixed order.
* `WortartenPoolTest`: a size-20 round is 6/7/7; a size-16 round totals exactly
  16 (the rounding case above); a filter that starves one class still returns
  the full `size`; the same seed gives the same round; no card appears twice;
  no card has `pos == OTHER`.
* Playing a round: a wrong answer shows the reason line for the *correct*
  class, not for the one she tapped.
