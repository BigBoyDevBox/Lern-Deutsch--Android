# 11 — Home

Built in **roadmap step 4** (replacing the placeholder that ships today).
`ui/home/`.

The first thing she sees. It has to answer three questions in under a second:
*am I still on a streak*, *what should I do now*, and *how far have I got*.

## Layout, top to bottom

```
┌──────────────────────────────────────────┐
│  Wortkatze 词猫              🔥 12       │   banner, Blush ground
│  ( Mimi, 120dp )      Level 7  ▓▓▓▓▁▁    │
├──────────────────────────────────────────┤
│  Heute · 今天         14 / 20  ◕         │   daily goal ring
├──────────────────────────────────────────┤
│  ┌────────┐ ┌────────┐                   │
│  │ Karten │ │  Der   │                   │   the five mode buttons,
│  │  卡片  │ │Die Das │                   │   two per row, big
│  └────────┘ └────────┘                   │
│  ┌────────┐ ┌────────┐                   │
│  │Wortart.│ │  Quiz  │                   │
│  └────────┘ └────────┘                   │
│  ┌─────────────────────┐                 │
│  │      ⚡ Blitz        │                 │   full width, the loud one
│  └─────────────────────┘                 │
├──────────────────────────────────────────┤
│  Wörter 210/1204   Sets 12/81   ⭐ 26     │   the stats strip
├──────────────────────────────────────────┤
│  🏅 Aufkleber · 贴纸          ⚙︎          │
└──────────────────────────────────────────┘
```

Scrolls as one `LazyColumn`. Everything above the stats strip should fit on a
small phone without scrolling.

## The banner

* App name in `displayMedium` `Bubblegum`, the Chinese name beside it in
  `headlineMedium` `Der` blue.
* **Mimi** at 120dp ([09](09-mascot.md)), `SLEEPY` if no round has been
  finished today and the last one was over 24 h ago, `NEUTRAL` otherwise.
  Tappable — petting her is the easter egg.
* **Streak flame** top right: the flame glyph plus the day count, from
  `Streak.displayDays(today)`. Hidden entirely at 0 rather than showing a sad
  zero.
* **Level bar**: the current level and a thin progress pill toward the next
  ([17](17-rewards.md)).

## The daily goal

One row: „Heute · 今天", `answered / goal`, and a ring that fills. Default goal
20 answers, changeable in Settings.

When it completes: the ring bursts into confetti, Mimi goes `PARTY`, and the
row swaps to „Geschafft! · 完成了！" with a tick. It fires **once** per day —
guard on `goalDay`, not on the count, or every answer past 20 re-celebrates.

## The five mode buttons

Each is a `StickerCard` with:

* the emoji, large
* the German name in `titleLarge`
* the localized name in `bodyMedium` `InkSoft` — so it reads „Karten · 卡片" in
  Chinese and „Karten · Cards" in English ([19](19-localization.md))
* its own accent colour as a soft wash: Karten `sky`, Der Die Das `rose`,
  Wortarten `mint`, Quiz `grape`, Blitz `tangerine`

Blitz is full width, taller, with a lightning emoji and a subtle animated
gradient — it is the "I only have a minute" button and should look like it.

Tapping goes to the deck picker or straight into a round
([10](10-navigation.md)).

## The stats strip

Three numbers, from `Progress`:

* **Wörter** — `seenCount()` / total cards. The Pebble app's version of this
  counter inflated forever because it added the deck size on *every*
  completion including replays (`awesome.md` 1.3). Counting distinct card ids
  makes it honest by construction; do not "optimize" it into an accumulator.
* **Sets** — `decksCompleted()` / deck count.
* **⭐** — the sum of `deckStars`.

Tapping the strip opens a stats sheet (later — step 15) with the Leitner box
distribution. Until then it is not tappable.

## Deck picker

`ui/home/DeckPickerScreen.kt`, used by Karten and Quiz. Tier headings with the
tier accent dot, then deck rows: emoji in an accent circle, deck name, the
Chinese and English names, a `StarRow` showing the best result, and a small
"12/18" showing how many of its cards are mastered.

A deck at 3 stars gets a gold border. A deck never played is not greyed out —
nothing here is locked.

## HomeUiState

```kotlin
data class HomeUiState(
    val loading: Boolean = true,
    val vocabulary: Vocabulary? = null,
    val streakDays: Int = 0,
    val level: Int = 1,
    val xpInLevel: Float = 0f,          // 0..1
    val goalAnswered: Int = 0,
    val goalTarget: Int = 20,
    val goalJustCompleted: Boolean = false,
    val wordsSeen: Int = 0,
    val wordsTotal: Int = 0,
    val decksDone: Int = 0,
    val decksTotal: Int = 0,
    val stars: Int = 0,
    val mascotMood: Mood = Mood.NEUTRAL,
    val failed: Boolean = false,
)
```

Built by combining `VocabRepository.vocabulary()` with
`ProgressStore.progress`. The "today" it needs comes from a `Clock` injected
into the ViewModel, so `HomeViewModelTest` can move time.

## Tests

The derivations are pure and testable — put them in `learn/HomeStats.kt` rather
than inside the ViewModel:

* `wordsSeen` counts distinct seen cards, and does not grow on a deck replay
* `decksDone` counts decks with stars > 0
* `goalJustCompleted` is true exactly once per day
* the level bar is 0 at a level boundary and approaches 1 before the next
