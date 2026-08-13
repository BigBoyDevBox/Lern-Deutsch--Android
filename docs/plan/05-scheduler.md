# 05 — The scheduler: what to ask next

Built in **roadmap step 3**. Pure Kotlin, `learn/Leitner.kt` and
`learn/Selection.kt`.

## Leitner boxes, not SM-2

Five boxes. A card you get right moves up one; a card you get wrong falls all
the way back to box 0. A box has a fixed interval, so "what is due" is one
comparison.

| Box | Comes back after | Meaning |
|---:|---:|---|
| 0 | same day | new, or just missed |
| 1 | 1 day | shaky |
| 2 | 2 days | getting there |
| 3 | 4 days | solid |
| 4 | 8 days | **mastered** |

SM-2 and friends were considered and rejected: they need a 1–5 self-rating the
learner has to think about, their ease factors are invisible and hard to
debug, and their benefit over Leitner shows up over years of daily use. Boxes
are explainable to a 14-year-old in one sentence, which is also a feature — the
app can *show* the box.

```kotlin
object Leitner {
    const val BOXES = 5
    const val MASTERED = BOXES - 1
    /** Days until a card in box i comes back. Index = box. */
    val INTERVAL_DAYS = intArrayOf(0, 1, 2, 4, 8)

    fun promote(p: CardProgress, today: Long): CardProgress
    fun demote(p: CardProgress, today: Long): CardProgress
    fun isDue(p: CardProgress, today: Long): Boolean   // p.dueDay <= today
    fun isMastered(p: CardProgress): Boolean           // p.box == MASTERED
}
```

`today` is an **epoch day** (`LocalDate.toEpochDay()`), passed in — `learn/`
never reads a clock ([02](02-architecture.md)).

`promote`: `box = min(box + 1, MASTERED)`, `dueDay = today + INTERVAL_DAYS[box]`,
`seen + 1`, `correct + 1`.
`demote`: `box = 0`, `dueDay = today`, `seen + 1`, `lapses + 1`.

A card demoted to box 0 is due *today*, which is what makes a missed card come
back inside the same round.

## Choosing a round's cards

```kotlin
object Selection {
    fun forDeck(
        deck: Deck,
        progress: Progress,
        today: Long,
        size: Int = DEFAULT_ROUND,
        random: Random,
    ): List<Card>

    fun forMode(
        vocabulary: Vocabulary,
        filter: (Card) -> Boolean,
        progress: Progress,
        today: Long,
        size: Int = DEFAULT_ROUND,
        random: Random,
    ): List<Card>

    const val DEFAULT_ROUND = 16
}
```

Both build the pool the same way, then take `size` from it:

1. **Due** — seen cards whose `dueDay <= today`, oldest due first. Ties broken
   by lowest box, then by card id so the order is deterministic.
2. **New** — unseen cards, in deck order (the word list is ordered by
   usefulness, so the first cards of a deck are the ones worth learning first).
3. **Filler** — if still short, the lowest-box seen cards not already picked.

Then shuffle the chosen list with the supplied `random`, so the round's
*content* is chosen by merit but its *order* is not predictable.

`forDeck` is what "study this deck" uses. `forMode` is what the Artikel,
Wortarten and Blitz modes use: they are not tied to a deck, so they filter the
whole vocabulary (`{ it.gender != Gender.NONE }`,
`{ it.pos in Wortart.PLAYABLE }`, …).

Rules that matter:

* **Never return more than `size` cards**, and never fewer than
  `min(size, pool.size)`.
* **Never return the same card twice** in one round.
* If the filter matches fewer than 4 cards, return what there is — the caller
  decides whether that is playable. (Quiz needs 4 for its choices; see
  [15](15-mode-quiz.md).)

## Tests (step 3 is not done without these)

`LeitnerTest`:

* promote walks 0→1→2→3→4 and stops at 4
* promote sets `dueDay` to `today + INTERVAL_DAYS[newBox]` at every box
* demote from any box lands on box 0, due today, with `lapses` incremented
* `isDue` is true on the due day, not just after it
* a brand-new `CardProgress()` is due immediately

`SelectionTest`:

* due cards come before new cards
* the oldest-due card comes first
* an all-new deck returns the first `size` cards of the deck, not a random
  sample
* the same seed produces the same round; a different seed does not
* no duplicates, never more than `size`
* a deck smaller than `size` returns the whole deck
* `forMode` respects its filter and never returns a card the filter rejects
