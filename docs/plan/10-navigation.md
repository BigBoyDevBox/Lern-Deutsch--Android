# 10 — Navigation

Built in **roadmap step 4**. `ui/navigation/`.

Single activity, one `NavHost`, type-safe routes as `@Serializable` objects and
data classes (Navigation Compose's Kotlin-serialization routes — no string
building, no manual argument encoding).

## The graph

```
Home ─┬─ ModeSetup(mode)  ─── Round(mode, deckKey?, direction?, seed)  ─── Summary
      ├─ DeckPicker(mode) ─┘
      ├─ Album
      └─ Settings
```

Five screens plus the round, which owns its own summary rather than navigating
to a sixth: the summary is a *phase* of `RoundUiState`, not a destination. A
separate destination would mean the round's result has to survive process death
in `SavedStateHandle`, which is a lot of machinery for a screen you look at for
six seconds.

## Routes

```kotlin
@Serializable object Home
@Serializable data class DeckPicker(val mode: String)
@Serializable data class Round(
    val mode: String,
    val seed: Long,                        // required, so it precedes the defaults
    val deckKey: String? = null,
    val direction: String? = null,
)
@Serializable object Album
@Serializable object Settings
```

Enums travel as their `name` strings, because route arguments are a
serialization boundary and an ordinal is not a stable identity. Parse them back
with `Mode.valueOf(...)` inside the ViewModel.

**The seed is in the route on purpose.** A round is reproducible from its
route: same seed, same cards, same order ([06](06-round-engine.md)). Generate
it at the moment of navigation (`Random.nextLong()`), never inside the
ViewModel — that way rotating the phone re-creates the ViewModel with the
*same* round instead of reshuffling mid-session.

## Which modes need a deck

| Mode | Deck picker? | Direction? |
|---|---|---|
| Karten | yes — pick tier, then deck | yes, four directions |
| Der Die Das | no — draws from every noun | no |
| Wortarten | no — draws from every classified word | no |
| Quiz | yes | yes |
| Blitz | no | no |

So Home → Karten goes to `DeckPicker("KARTEN")`, while Home → Blitz goes
straight to `Round("BLITZ", seed = …)`.

## Back behaviour

* Back from a round **mid-round** shows a confirm dialog: „Runde abbrechen? ·
  放弃这一轮？" Losing fifteen answers to a stray gesture is the worst thing the
  navigation can do.
* Back from the **summary** goes Home, and pops the round off the stack — the
  round must not be revivable by going forward.
* Back from Home exits. Use `enableOnBackInvokedCallback` (already set in the
  manifest) so predictive back animates properly.

## Transitions

Shared-axis horizontal for forward/back within the graph, 300 ms
([08](08-juice.md)). The round entry is special: it scales up from 0.92 with a
spring, so starting a round feels like opening something.

## Deep links

None. The app is offline and has nothing to link to. Do not add an intent
filter without a reason recorded in an ADR.

## Tests

Route serialization round-trips (`RouteTest`): every route object survives
encode → decode with its arguments intact, including a null `deckKey` and a
negative `seed`. Navigation itself is not unit-tested — that is what an
emulator job would be for, and there isn't one ([21](21-testing.md)).
