# 17 — Rewards and progression

Built in **roadmap step 9** (XP, levels, stickers) and **step 15** (the album).

Five overlapping loops, on five different timescales. Overlapping is the point:
when one has just paid out, another is close.

| Loop | Timescale | Mechanism |
|---|---|---|
| Answer | seconds | confetti, combo ([08](08-juice.md)) |
| Round | minutes | stars, XP |
| Day | daily | the goal ring, the streak flame |
| Week | days | levels |
| Collection | weeks | stickers, decks completed |

## Stars — per round

1–3, from first-try accuracy, `Stars.forRound` (already built, ported from the
watch). ≥90 % → 3, ≥60 % → 2, finishing at all → 1.

`Progress.deckStars` keeps the **best ever** per deck; a worse round never
lowers it. Beating it shows a gold **„Rekord!"** tag on the summary.

## XP and levels

```kotlin
object Levels {
    /** Cumulative XP needed to *reach* level n (n >= 1). Level 1 costs 0. */
    fun xpForLevel(n: Int): Int = 20 * n * (n - 1)      // 0, 40, 120, 240, 400, …

    fun forXp(xp: Int): Int                             // highest n with xpForLevel(n) <= xp
    fun progressInLevel(xp: Int): Float                 // 0f..1f toward the next
}
```

Earning, all in `learn/Xp.kt` as a pure function of a round's outcome:

| Event | XP |
|---|---|
| Correct answer | 1 |
| Correct answer at combo ≥ 5 | 2 |
| Correct answer at combo ≥ 10 | 3 |
| Finishing a round | 10 |
| Each star earned | 5 |
| Beating a deck record | 15 |
| Completing the daily goal | 25 |

A typical 16-card round is ~45 XP, so early levels come every round or two and
level 10 (1800 XP) is a few weeks of daily use. That is the intended curve:
fast at first, then slow enough that the sticker album takes over as the driver.

**A level-up is a moment.** The summary pauses, Mimi goes `PARTY`, the level
badge scales in with confetti, and a tone plays. It never happens silently.

## The daily goal

Default 20 answers, adjustable 10/20/40 in Settings. Tracked in
`Progress.goalAnswered` against `Progress.goalDay`; when `goalDay` is not
today, reset the count to 0 and set `goalDay` to today.

Completing it fires **once**, and that needs a *second* field,
`goalCompletedDay`. One field cannot do both jobs: `goalDay` is set to today
the moment the first answer of the day lands, so by the time the goal is
reached it already equals today and can no longer distinguish "counted today"
from "celebrated today". Celebrate when
`goalAnswered >= dailyGoal && goalCompletedDay != today`, then set
`goalCompletedDay = today`. Never guard on the count alone, or every answer
past 20 re-celebrates.

## The streak

`learn/Streak.kt`, already built and ported from the watch. One round finished
per day keeps it. The flame is on the home banner
([11](11-home-screen.md)) and on every summary.

Deliberately forgiving, and stated in the code: a second round on the same day
never resets it, a backwards clock never breaks it, and the flame stays lit
through the whole day after the last round — you have until bedtime tomorrow,
not until midnight tonight. Streak anxiety is a real thing and this app is for
a 14-year-old; the streak is a nudge, not a debt.

There are no streak freezes, no repair purchases, no notifications begging her
to come back. If the streak breaks, it breaks, and the next round starts a new
one.

## Stickers · Aufkleber · 贴纸

The long loop. ~24 collectible stickers, each an emoji plus a bilingual name,
unlocked by a milestone. Unlock rules are a **pure function of `Progress`**, so
they are recomputed after every round and can never drift out of sync with the
data:

```kotlin
data class Sticker(val id: String, val emoji: String, val nameRes: Int)

object Stickers {
    val ALL: List<Sticker>
    /** Every sticker earned by this progress. Pure, total, idempotent. */
    fun earned(progress: Progress, vocabulary: Vocabulary): Set<String>
}
```

Starter set — extend freely, but keep every rule checkable from `Progress`:

| id | | Unlocked by |
|---|---|---|
| `first-round` | 🌱 | finish any round |
| `streak-3` `streak-7` `streak-30` `streak-100` | 🔥 | streak days |
| `words-50` `words-200` `words-500` `words-1204` | 📚 | distinct cards seen |
| `mastered-100` | 🧠 | cards in the top Leitner box |
| `deck-perfect` | ⭐ | any deck at 3 stars |
| `tier-beginner` … `tier-advanced` | 🎓 | every deck in a tier finished |
| `artikel-100` | 🎨 | 100 correct articles |
| `wortarten-100` | 🔤 | 100 correct Wortarten |
| `english-100` | 🇬🇧 | 100 correct answers in `DE_EN`/`EN_DE` |
| `blitz-500` `blitz-1000` | ⚡ | a Blitz score |
| `combo-20` | 💥 | a 20 combo |
| `level-5` `level-10` `level-20` | 🏅 | a level |
| `night-owl` | 🦉 | a round finished after 23:00 |
| `early-bird` | 🐤 | a round finished before 07:00 |

The last two need the round's local time recorded on the round outcome — pass
it in, do not read a clock inside `learn/`.

**A sticker is never lost**, and a new one is announced on the summary as a
card that flips over to reveal itself.

Counters the rules need (`artikelCorrect`, `wortartenCorrect`,
`englishCorrect`, `nightRound`, `earlyRound`) are added to `Progress` in this
step. Bump `Progress.SCHEMA` — old documents deserialize with the defaults,
which is exactly right: the counters start at 0 and the stickers unlock again
from real play.

## The album (step 15)

`ui/album/AlbumScreen.kt`. A grid of every sticker: earned ones in full colour
on a white sticker card, unearned ones as a grey silhouette with their unlock
condition underneath. Seeing what is *not* yet earned is most of the motivation
— do not hide locked stickers.

Header: Mimi at 96dp, "12 / 24", and the level badge. Tapping an earned sticker
makes it wobble and play its tone.

## Tests

`LevelsTest`: `xpForLevel` is 0/40/120/240/400 for 1–5; `forXp` is exact at
every boundary (39→1, 40→2, 41→2); `progressInLevel` is 0 at a boundary and
approaches 1; XP of 0 is level 1; a huge XP does not overflow.

`XpTest`: the table above, including the combo tiers.

`StickersTest`: every sticker in `ALL` has a unique id and a name resource; a
fresh `Progress` earns nothing; each rule fires at exactly its threshold and
not one below; `earned` is idempotent; earned stickers are never removed by a
later, worse round.
