# 15 — Quiz · 四选一

Built in **roadmap step 12**. Four choices, and the mode where English earns
its keep.

## Why

Karten tests recall; this tests recognition. Recognition is easier, which makes
it the mode for a tired evening — and four choices are tappable in a second,
which makes it the mode that generates the most answers per minute, which is
what the daily goal and the Leitner scheduler both want.

## The English bridge

Quiz runs in all four directions ([06](06-round-engine.md)), and two of them
involve English:

| Direction | Prompt | Choices |
|---|---|---|
| `DE_ZH` | Hallo | 你好 / 再见 / 谢谢 / 请 |
| `ZH_DE` | 你好 | Hallo / Tschüss / Danke / Bitte |
| `DE_EN` | Hallo | hello / bye / thank you / please |
| `EN_DE` | hello | Hallo / Tschüss / Danke / Bitte |

`DE_EN`/`EN_DE` are how she practises English without a separate app: the same
1204 items, the same scheduler, the same stars. And German ⇄ English is
genuinely useful to her — the two are far closer to each other than either is
to Chinese, so the pairs are learnable together in a way German ⇄ Chinese never
is.

The direction chip is at the top and switchable mid-round, same as Karten.
Default `DE_ZH`; remembered in `Settings`.

## Distractors

The wrong answers are the whole quality of this mode. Three rules:

1. **Same deck first.** Distractors come from the same deck as the answer, so a
   Food round offers food. Cross-deck distractors are trivially eliminable and
   turn a vocabulary test into a category test.
2. **Never a duplicate of the answer text.** Two cards can share a translation
   („gross" appears in two decks); compare the *rendered answer strings*, not
   the card ids.
3. **Widen only when forced.** If the deck cannot supply three distinct
   distractors, widen to the tier, then to the whole vocabulary. Decks in this
   word list are 12–22 cards, so widening is rare — but Blitz filters can make
   a pool small, so the fallback has to exist.

```kotlin
object Distractors {
    const val CHOICES = 4

    /**
     * Three wrong answers for [card] in [lang], drawn from [pool] then
     * [fallback]. Returns fewer than 3 only if both are exhausted, which the
     * caller must handle by not offering the question.
     */
    fun pick(
        card: Card,
        lang: Lang,
        pool: List<Card>,
        fallback: List<Card>,
        random: Random,
        count: Int = CHOICES - 1,
    ): List<String>
}
```

Deterministic given `random`. The choice list is then assembled and **shuffled**
with the same `random`, so the correct answer is not always in the same slot.

A prompt whose card cannot raise three distractors is skipped by the round
builder rather than shown with two — `quizQuestion` returns `null` and the
builder draws another card.

## The screen

* Prompt card on top, `displayLarge`, on a `StickerCard`. A **🔊** button for
  German prompts ([18](18-audio-haptics.md)).
* Four choice buttons in a 2×2 grid, each a `StickerCard` with 56dp minimum
  height, `titleLarge` text, wrapping to two lines if needed.
* Tap: the chosen button springs and turns `Correct` green or `Wrong` coral.
  On a wrong tap the **correct** button also lights green — she must see the
  right answer, always.
* 500 ms later, next question. On a wrong answer, wait for a tap instead: a
  learner reading the right answer must not be interrupted by the next one.

## Files

```
ui/round/quiz/QuizFace.kt
learn/round/Distractors.kt
learn/round/QuizQuestion.kt
```

## Done when

* `DistractorsTest`: exactly 3 by default; never equal to the answer text;
  never duplicated among themselves; prefers the deck pool; widens to the
  fallback when the pool is too small; returns fewer than 3 only when both are
  exhausted; same seed → same distractors.
* `QuizQuestionTest`: `correctIndex` always points at the correct string after
  shuffling; all four directions produce prompts and answers from the right
  fields; a card in a 2-card pool with no fallback yields `null`.
* Playing a round in `DE_EN` shows English choices and German prompts.
