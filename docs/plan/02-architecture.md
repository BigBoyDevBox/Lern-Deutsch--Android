# 02 — Architecture

## Stack

Everything is pinned in `gradle/libs.versions.toml`. Never write a version
number in a build file or in a doc.

* Kotlin, Jetpack Compose, Material 3
* Single Gradle module `:app`, `minSdk 26`, JDK 17
* MVVM: one immutable `UiState` per screen, exposed as a `StateFlow` from a
  `ViewModel`
* Hilt for DI, Navigation Compose for the screen graph
* `kotlinx.serialization` for the vocabulary asset and the progress document
* `androidx.datastore` for persistence
* **JVM unit tests only** (`testDebugUnitTest`). There is no `androidTest`
  directory; adding one means adding an emulator CI job, which is a decision,
  not a detail.

## The purity rule

```
ch.lkmc.wortkatze
├── learn/      PURE KOTLIN. No android.* imports. Ever.
├── data/       The only place that touches Context, assets and disk.
├── ui/         Compose. Thin. Renders state, emits events.
└── di/         Hilt modules.
```

**`learn/` may not import anything from `android.*`, may not take a `Context`,
and may not read the clock or call `Random()` without being given them.** Time
arrives as a `LocalDate` or a `Long` millis parameter; randomness arrives as a
seeded `kotlin.random.Random`.

This is the single most important structural decision in the app, and it buys
three things:

1. The learning logic — scheduling, grading, star maths, distractor choice,
   combo maths, the particle simulation — is covered by fast JVM tests. The
   whole suite runs in seconds on a laptop and in CI.
2. Bugs in that logic are *reproducible*: a seed and a date reproduce a session
   exactly.
3. A weaker implementer can be handed a signature and a list of test names and
   produce something correct without ever launching an emulator.

If you find yourself wanting `System.currentTimeMillis()` inside `learn/`, add
a parameter instead. If you find yourself wanting a `Context` there, the code
belongs in `data/`.

## Package layout

```
ch.lkmc.wortkatze
├── WortkatzeApp.kt          @HiltAndroidApp
├── MainActivity.kt          single activity, sets the theme + nav host
├── learn/
│   ├── Vocabulary.kt        Card, Deck, Tier, Gender, Wortart  (also the
│   │                        @Serializable wire format — one shape, no mapper)
│   ├── VocabParser.kt       String -> Vocabulary
│   ├── Stars.kt             first-try accuracy -> 1..3 stars
│   ├── Streak.kt            daily streak arithmetic
│   ├── Progress.kt          the saved document + its pure updates      (step 2)
│   ├── Leitner.kt           box promotion/demotion, what is due        (step 3)
│   ├── Selection.kt         which cards a round should contain         (step 3)
│   ├── Levels.kt            XP -> level                                (step 9)
│   ├── Stickers.kt          milestone unlock rules                     (step 9)
│   ├── round/
│   │   ├── Round.kt         the shared session loop                    (step 5)
│   │   ├── Question.kt      prompt/answer/choices for one question     (step 5)
│   │   ├── Direction.kt     DE_ZH, ZH_DE, DE_EN, EN_DE                 (step 5)
│   │   ├── Distractors.kt   wrong-answer choice, seeded          (step 12 Quiz)
│   │   └── Blitz.kt         the timed variant + combo maths     (step 14 Blitz)
│   └── juice/
│       └── Confetti.kt      pure particle simulation                   (step 7)
├── data/
│   ├── VocabRepository.kt   loads assets/vocab.json, caches it
│   └── ProgressStore.kt     DataStore<Progress>, debounced writes      (step 2)
├── ui/
│   ├── theme/               Color.kt (Candy), Theme.kt
│   ├── components/          CandyButton, StickerCard, ArticleChip, …
│   ├── home/                HomeScreen + HomeViewModel
│   ├── round/               the shared round scaffolding + per-mode faces
│   ├── album/               the sticker album                          (step 15)
│   ├── settings/            SettingsScreen                             (step 16)
│   └── navigation/          the NavHost and routes
└── di/AppModule.kt
```

Steps in brackets are the **roadmap step** that creates the file
([22](22-roadmap-foundations.md), [23](23-roadmap-modes.md)). Files without a
step number already exist.

Careful: roadmap steps and plan-file numbers are two different sequences that
overlap in range. Quiz is **roadmap step 12** and is specified in **plan file
15**; Blitz is **roadmap step 14** and is specified in **plan file 16**. Where
it could be misread, the step number above names its mode as a check. Roadmap
steps are the order you build in; file numbers are only where things are
written down.

## MVVM, precisely

```kotlin
data class SomeUiState(...)                     // immutable, one per screen

@HiltViewModel
class SomeViewModel @Inject constructor(...) : ViewModel() {
    private val _state = MutableStateFlow(SomeUiState())
    val state: StateFlow<SomeUiState> = _state.asStateFlow()
    fun onSomething() { _state.update { ... } }
}

@Composable
fun SomeScreen(viewModel: SomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ...
}
```

Rules:

* A composable never reaches for a repository. It reads state and calls
  ViewModel functions.
* A ViewModel never formats user-visible text. It puts a `@StringRes Int` (or
  a domain object) in the state and the composable resolves it.
* Decision logic never lives in a composable. If a `when` decides *what the app
  does* rather than *what it draws*, it belongs in `learn/` where a test can
  reach it.

## Threading

* Vocabulary parse: `Dispatchers.IO`, once, cached for the process
  (`VocabRepository`).
* Progress writes: DataStore, debounced — see [04](04-progress-store.md).
* Everything in `learn/` is synchronous and fast enough to call from the main
  thread. If something there ever isn't, that is a bug in the algorithm, not a
  reason to add a coroutine.

## Deviations

When reality forces a change to any of the above, record it in
[AGENTS.md](../../AGENTS.md) under "Conventions and footguns" **in the same
PR**, and fix the plan file. The plan is only useful while it is true.
