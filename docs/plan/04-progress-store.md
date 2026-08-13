# 04 — Progress: what is remembered

Built in **roadmap step 2**.

Everything the app remembers lives in one serializable document. There is no
second source of truth, no scattered SharedPreferences, and no database.

## The document

`learn/Progress.kt` — pure Kotlin, `@Serializable`, no Android imports.

```kotlin
@Serializable
data class Progress(
    val schema: Int = SCHEMA,
    /** Card id -> what we know about it. Sparse: unseen cards are absent. */
    val cards: Map<String, CardProgress> = emptyMap(),
    /** Deck key -> best stars ever earned, 0..3. */
    val deckStars: Map<String, Int> = emptyMap(),
    val streakDays: Int = 0,
    /** ISO-8601, e.g. "2026-08-13". Null until the first finished round. */
    val streakLastDay: String? = null,
    val xp: Int = 0,
    val stickers: Set<String> = emptySet(),
    val dailyGoal: Int = 20,
    val goalDay: String? = null,
    val goalAnswered: Int = 0,
    val settings: Settings = Settings(),
) {
    companion object { const val SCHEMA = 1 }
}

@Serializable
data class CardProgress(
    /** Leitner box, 0..4. See 05-scheduler.md. */
    val box: Int = 0,
    /** Epoch day this card next becomes due. */
    val dueDay: Long = 0,
    val seen: Int = 0,
    val correct: Int = 0,
    val lapses: Int = 0,
)

@Serializable
data class Settings(
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
    val reduceMotion: Boolean = false,
    /** Show the English gloss on card backs and in Quiz. */
    val englishOn: Boolean = true,
)
```

`Streak` ([`learn/Streak.kt`](../../app/src/main/java/ch/lkmc/wortkatze/learn/Streak.kt),
already written) is the arithmetic; `streakDays`/`streakLastDay` are its two
fields flattened for storage. Convert with:

```kotlin
fun Progress.streak(): Streak = Streak(streakDays, streakLastDay?.let(LocalDate::parse))
fun Progress.withStreak(s: Streak): Progress =
    copy(streakDays = s.days, streakLastDay = s.lastDay?.toString())
```

## Pure updates

Every mutation is a pure function on `Progress` returning a new `Progress`, so
every one of them is unit-testable without a device. Put them in `Progress.kt`
as extension functions:

```kotlin
fun Progress.afterAnswer(cardId: String, correct: Boolean, today: LocalDate): Progress
fun Progress.afterRound(deckKey: String?, stars: Int, xpEarned: Int, today: LocalDate): Progress
fun Progress.withSettings(settings: Settings): Progress
fun Progress.masteredCount(): Int      // cards in the top Leitner box
fun Progress.seenCount(): Int          // cards with seen > 0
fun Progress.decksCompleted(): Int     // deckStars entries > 0
```

`afterAnswer` updates the card's Leitner state (via [05](05-scheduler.md)),
bumps `seen`/`correct`/`lapses`, and advances `goalAnswered` (resetting it
first when `goalDay` is not today).

`afterRound` submits stars (keeping the maximum, never lowering a record), adds
XP, bumps the streak, and recomputes unlocked stickers ([17](17-rewards.md)).
`deckKey` is null for modes that are not tied to one deck (Blitz).

## Storage

`data/ProgressStore.kt` — the only Android-facing part.

```kotlin
@Singleton
class ProgressStore @Inject constructor(@ApplicationContext context: Context) {
    val progress: Flow<Progress>
    suspend fun update(transform: (Progress) -> Progress)
}
```

Backed by `androidx.datastore.core.DataStore<Progress>` with a
`Serializer<Progress>` that uses `kotlinx.serialization`, file name
`progress.json`, in the app's own files directory.

**Corruption policy: fall back to `Progress()` and carry on.** A learner who
loses her streak to a half-written file is annoyed; a learner who cannot open
the app at all is gone. Use `ReplaceFileCorruptionHandler { Progress() }`, and
do the same on an unknown `schema`.

### Why DataStore and not Room

Recorded in `docs/decisions/0005`. Short version: 1204 cards × a five-field
record is a few tens of kilobytes, DataStore rewrites the whole file per write
and that is fine at this size, and the serializer is pure Kotlin so the format
is unit-testable. Room would add KSP entities, DAOs, migrations and an
`androidTest` job for a data set that fits in a text file.

The revisit trigger is written into the ADR: if the document exceeds ~1 MB, or
writes become per-answer rather than per-round, move to Room.

### Write cadence

**Write once per finished round, not once per answer.** A round is 15–20
answers; DataStore serializes the whole document each time, and rewriting
60 KB twenty times a minute is real battery for no benefit.

The ViewModel therefore holds the round's outcomes in memory and calls
`ProgressStore.update` when the round ends — plus once from `onStop`, so a
round abandoned by pressing Home is not lost.

## Backup

`progress.json` is in the DataStore directory, which both backup allowlists
include (`res/xml/backup_rules.xml`, `res/xml/data_extraction_rules.xml`). A
lost streak after a phone swap is the one data loss this app can inflict, and
the document holds nothing sensitive — no account, no name, just which words
have been practised.

## Tests (step 2 is not done without these)

`ProgressTest`:

* a new `Progress` is all zeroes and empty maps
* `afterAnswer` on an unseen card creates its entry with `seen = 1`
* a correct answer promotes the box; a wrong answer resets it and bumps
  `lapses`
* `afterRound` keeps the higher of old and new stars, never lowers a record
* `afterRound` bumps the streak exactly once per day
* the daily goal counter resets when `goalDay` is not today
* `masteredCount`/`seenCount`/`decksCompleted` count what they say

`ProgressSerializerTest`:

* round-trips a fully populated document byte-identically
* an older document missing a field deserializes to that field's default
* an unparseable document yields `Progress()` rather than throwing
