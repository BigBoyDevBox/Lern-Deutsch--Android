# 16 — Blitz · 闪电

Built in **roadmap step 14**. Sixty seconds, everything mixed, combos.

## Why

Every other mode is a round of fixed length that ends when it ends. Blitz is
the one that ends *now*, and that is the difference between "I'll do this
later" and "I have one minute". It is also the only mode with a score to beat,
which is the strongest replay hook the app has.

## The loop

* **60 seconds**, counting down from the first question.
* Questions are drawn endlessly from a mixed pool: Artikel, Wortarten and Quiz
  (`DE_ZH`), roughly 1:1:1. No Karten — self-grading has no place in a timed
  mode.
* Correct: **+1 combo**, points, and **+1 second** on the clock. Wrong: combo
  resets, **−2 seconds**, and the right answer is shown for 800 ms.
* The round ends when the clock reaches zero. There is no other ending.
* The summary shows the score, the best score, the longest combo, and whether
  it is a new record.

The time bonus is what makes a good run *feel* like a good run: playing well
literally buys more play. Cap the clock at 90 s so a very strong run cannot
become unbounded.

## Scoring

```kotlin
object BlitzScore {
    const val BASE = 10
    const val TIME_BONUS_MS = 1_000L
    const val TIME_PENALTY_MS = 2_000L
    const val MAX_CLOCK_MS = 90_000L
    const val START_CLOCK_MS = 60_000L

    /** Points for one correct answer at the combo it produced (combo >= 1). */
    fun points(combo: Int): Int = BASE * multiplier(combo)

    /** 1× below 5, 2× at 5–9, 3× at 10–19, 4× at 20+. */
    fun multiplier(combo: Int): Int = when {
        combo >= 20 -> 4
        combo >= 10 -> 3
        combo >= 5 -> 2
        else -> 1
    }
}
```

Step multipliers rather than a continuous curve, so the jumps are events worth
animating: hitting 5, 10 and 20 each get a bigger confetti burst and a rising
tone ([08](08-juice.md)).

## The clock

```kotlin
class BlitzClock(private val startMs: Long = BlitzScore.START_CLOCK_MS) {
    fun remaining(elapsedMs: Long): Long
    fun reward(): Unit    // adds TIME_BONUS_MS, capped at MAX_CLOCK_MS
    fun penalty(): Unit
    val expired: Boolean
}
```

Pure: it is told the elapsed time, it never reads a clock ([02](02-architecture.md)).
The ViewModel drives it from a coroutine ticking every 100 ms and stops that
coroutine the moment the round ends or the composable leaves composition.

Display: a horizontal bar that drains, plus the seconds as a number. Under 10 s
it turns `Candy.Wrong`, pulses once a second, and the ticks get a heartbeat
haptic. Do **not** play a continuous ticking sound — one minute of ticking is
one minute of stress, and this is meant to be fun.

## The screen

Different from the other modes on purpose — this one is loud.

* No mascot. A timed mode has no room for something to look at
  ([09](09-mascot.md)).
* The clock bar is pinned to the top, full width, and it is the only thing
  above the question.
* Score top-left, combo top-right. The combo counter is large and gets larger
  with the multiplier.
* Question and answer buttons fill the rest, laid out exactly as in that
  question's own mode, so nothing has to be re-learned mid-round.
* On a mixed pool, the *kind* of question must be obvious at a glance: a small
  label above the prompt („Artikel · 冠词" / „Wortart · 词性" / „Wort · 单词")
  and the mode's accent colour on the buttons.

## Records

`Progress` gains two fields in this step:

```kotlin
val blitzBest: Int = 0,
val blitzBestCombo: Int = 0,
```

Beating either shows the gold **„Rekord!"** tag on the summary, the same tag
Karten uses for stars.

## Files

```
ui/round/blitz/BlitzFace.kt
ui/round/blitz/BlitzViewModel.kt
learn/round/Blitz.kt          BlitzScore, BlitzClock, the mixed pool builder
```

The mixed pool builder walks the three sub-modes round-robin and asks each for
a question; a sub-mode that cannot produce one (Quiz with too few distractors)
is skipped and the next takes its turn.

## Done when

* `BlitzScoreTest`: multiplier boundaries at exactly 5, 10 and 20; points at
  combo 1, 4, 5, 9, 10, 19, 20, 50.
* `BlitzClockTest`: starts at 60 s; a reward adds 1 s; the cap holds at 90 s
  after many rewards; a penalty subtracts 2 s; the clock cannot go below 0;
  `expired` is true at exactly 0.
* `BlitzPoolTest`: a 60-question walk contains all three kinds; no question is
  ever malformed; the same seed reproduces the run.
* Playing: the clock visibly gains time on a good streak, and the round ends
  by itself.
