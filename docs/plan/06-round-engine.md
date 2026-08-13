# 06 — The round engine

Built in **roadmap step 5**. Pure Kotlin, `learn/round/`.

One engine runs Karten, Der Die Das, Wortarten and Quiz. Blitz uses a variant
([16](16-mode-blitz.md)). Writing four session loops would mean four places for
the star rule to drift.

## The loop, inherited from the watch

Ported from the Pebble app's `study.c`, because it is good and because a
learner who used the watch already knows it:

> The round's cards are shuffled into a queue. The front card is asked. A
> correct answer leaves the queue; a wrong one goes to the **back** and comes
> round again. The round ends when the queue is empty — i.e. when every card
> has been answered correctly at least once. Stars come from how many were
> right on the **first** try.

Consequences worth stating out loud, because they are the design:

* **You always finish.** A round cannot be failed, only finished less well.
* **A miss costs a star, not the round.** The card comes back a few questions
  later, which is exactly when it is worth seeing again.
* **The round length is not the question count.** Sixteen cards with four
  misses is twenty questions. The progress bar therefore tracks *cards
  mastered*, not questions asked ([08](08-juice.md)).

## Types

```kotlin
enum class Lang { DE, ZH, EN }

enum class Direction(val prompt: Lang, val answer: Lang) {
    DE_ZH(Lang.DE, Lang.ZH),
    ZH_DE(Lang.ZH, Lang.DE),
    DE_EN(Lang.DE, Lang.EN),
    EN_DE(Lang.EN, Lang.DE),
}

enum class Mode { KARTEN, ARTIKEL, WORTARTEN, QUIZ, BLITZ }

/** One question, fully resolved — the UI renders it and nothing more. */
data class Question(
    val card: Card,
    val prompt: String,
    val promptLang: Lang,
    /** The correct answer as text (also what a Karten back face shows). */
    val answer: String,
    val answerLang: Lang,
    /** Tappable choices. Empty for Karten, which is self-graded. */
    val choices: List<String> = emptyList(),
    /** Index into [choices]; -1 when self-graded. */
    val correctIndex: Int = -1,
    /** True the second and later times this card is asked in this round. */
    val repeat: Boolean = false,
)
```

`Card.zh` / `Card.de` / `Card.en` supply the text; `Card.withoutArticle` is
what Artikel mode prompts with.

## The engine

```kotlin
class Round(
    val mode: Mode,
    private val cards: List<Card>,
    private val random: Random,
    private val questionFor: (Card, Boolean) -> Question,
) {
    val total: Int                 // distinct cards, fixed at construction
    val mastered: Int              // answered correctly at least once
    val asked: Int                 // questions asked, including repeats
    val firstTryCorrect: Int
    val finished: Boolean

    fun current(): Question?       // null once finished
    fun answer(correct: Boolean)   // grade the current question, advance
    fun stars(): Int               // Stars.forRound(firstTryCorrect, total)
}
```

* `Round` shuffles `cards` with `random` at construction. Same seed, same
  round — which is what lets a failing session be reproduced in a test.
* `questionFor(card, repeat)` is the per-mode part. It is a parameter, not a
  subclass hierarchy: four modes, four small builders, all pure functions the
  tests can call directly.
* `answer(correct)` is called by the ViewModel *after* the UI has shown its
  feedback. The engine does not know about animations.
* Calling `answer` when `finished` is a programming error — throw
  `IllegalStateException`, do not silently no-op.

### Question builders

One per mode, each a pure function in its mode's file:

```kotlin
fun kartenQuestion(card: Card, direction: Direction, repeat: Boolean): Question
fun artikelQuestion(card: Card, repeat: Boolean): Question
fun wortartenQuestion(card: Card, repeat: Boolean): Question
fun quizQuestion(card: Card, direction: Direction, pool: List<Card>,
                 random: Random, repeat: Boolean): Question
```

Only `quizQuestion` needs the pool and the random — it has to invent three
wrong answers ([15](15-mode-quiz.md)).

## What the ViewModel adds

`ui/round/RoundViewModel.kt` owns everything the engine deliberately doesn't:

* building the card list via `Selection` ([05](05-scheduler.md))
* the phase machine — `ASKING → REVEALED → FEEDBACK → next`
* accumulating outcomes in memory and writing `Progress` once at the end
  ([04](04-progress-store.md))
* firing juice events: confetti, shake, combo, mascot mood ([08](08-juice.md))
* the summary state: stars, whether it beat the record, XP earned, streak

```kotlin
data class RoundUiState(
    val question: Question? = null,
    val phase: Phase = Phase.ASKING,
    val revealedAnswer: String? = null,
    val progress: Float = 0f,          // mastered / total — NOT asked/total
    val combo: Int = 0,
    val mascotMood: Mood = Mood.NEUTRAL,
    val summary: RoundSummary? = null,
)
```

## The summary screen

Shared by all modes, one composable. It shows:

* stars earned (1–3), with a gold **„Rekord!"** tag when it beats the stored
  best — ported from the Pebble app, where it is the most-noticed feature
* XP earned, and a level-up celebration if one happened ([17](17-rewards.md))
* the streak flame and its day count
* any sticker unlocked by this round, presented as a card that flips over
* **nochmal · 再来** and **Menü · 菜单**

## Tests (step 5 is not done without these)

`RoundTest`:

* a round of N cards all answered correctly asks exactly N questions
* a wrong answer puts the card back and the round asks it again
* a card missed twice is asked three times
* `finished` only becomes true when every card has been correct once
* `firstTryCorrect` counts only first attempts — a card missed then correct
  does not count
* `stars()` matches `Stars.forRound(firstTryCorrect, total)`
* `mastered` never exceeds `total`; `progress` is monotonic
* the same seed produces the same question order
* `answer()` after `finished` throws
* `repeat` is false on a card's first appearance and true afterwards
