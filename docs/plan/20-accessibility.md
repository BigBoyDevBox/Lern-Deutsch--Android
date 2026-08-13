# 20 — Accessibility and performance

Not a compliance exercise. Every item here is something that would stop this
particular learner using the app on a particular day.

## Reduce motion

The app is built out of confetti and springs. Some people get motion sick, some
people find it distracting when tired, and Android has a system setting that
says so.

```kotlin
// data/MotionSettings.kt
fun reduceMotionEnabled(context: Context): Boolean =
    Settings.Global.getFloat(context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE, 1f) < 0.01f
```

A threshold rather than `== 0f`: the setting is a float, some OEM skins write a
near-zero value instead of a true zero, and an exact comparison that misses by
one ulp silently denies the feature to the exact person it exists for. Erring
toward "reduce motion" is the safe direction.

Combine it with `Settings.reduceMotion` from the progress document (the
in-app switch) and put the result in a `CompositionLocal`:

```kotlin
val LocalReduceMotion = staticCompositionLocalOf { false }
```

**Every animation checks it.** The rule is *replace, never remove*:

| Normally | With reduce-motion |
|---|---|
| Confetti burst | a single expanding ring, 200 ms |
| Spring scale | an instant colour change |
| Card flip | a cross-fade, 150 ms |
| Wrong-answer wobble | a border flash |
| Combo pulse | a static badge |
| Blitz clock pulse | a colour change only |

A learner with reduce-motion on must still be able to tell right from wrong
instantly. Never answer it with "no feedback".

## Colour is never the only signal

Roughly 1 in 200 girls has a colour-vision deficiency, and the der/die/das
colours are load-bearing.

* Article chips always carry the word „der"/„die"/„das", never only the colour.
* Correct/wrong always carry a shape (✓ / ✗) as well as a hue, and the wrong
  answer additionally shows the right one.
* The `Correct` green and `Wrong` coral are distinguishable in deuteranopia by
  lightness as well as hue — check any change to them with a simulator.
* Deck accents are decorative only. Nothing is ever identified by its accent
  alone.

## Contrast

`Ink #3A2F4A` on `Cream #FFF8F3` is about 11:1 — fine. The two to watch:

* `InkSoft #7A6E8C` on `Card #FFFFFF` is about 4.9:1 — passes AA for body text,
  and must not be lightened.
* White on `Gold #FFC53D` **fails**. Star counts and record tags use `Ink` on
  gold, never white.

Any new colour pair carrying text must reach 4.5:1, and 3:1 for text above
24sp.

## Touch, text and screen readers

* Minimum touch target **48dp**, 8dp between adjacent targets. The three
  article buttons are 72dp tall — they are tapped hundreds of times.
* The app must work at **200 % font scale**. Card text auto-shrinks; buttons
  grow. Test with `Settings > Display > Font size` at maximum, on a small
  phone: if a mode's answer buttons stop fitting, the layout is wrong, not the
  setting.
* Every interactive composable has a `contentDescription` or a text label.
  Emoji-only buttons **must** have one — TalkBack reads "🔊" as nothing useful.
* Mimi is decorative: `contentDescription = null` on the canvas, so TalkBack
  does not announce a cat between every question.
* Set `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` on the
  verdict text so a screen reader announces right/wrong without the learner
  hunting for it.
* Language on text: mark German text `Locale("de")` and Chinese
  `Locale("zh-Hans")` in `AnnotatedString` so TalkBack switches voices instead
  of reading German with a Chinese engine.

## Performance

* **No animation runs when nothing is animating.** Stop the confetti loop when
  the burst ends and the Blitz tick when the round ends, and cancel both in
  `onDispose`. The Pebble app shipped exactly this bug as battery drain
  (`awesome.md` 2.1); on a phone it shows up as a warm device and a flat
  battery by lunchtime.
* The vocabulary is parsed **once** per process, on IO, and cached
  (`VocabRepository`).
* Progress is written **once per round**, not per answer ([04](04-progress-store.md)).
* `LazyColumn` items always get stable `key`s, so a progress update does not
  recompose the whole deck list.
* Nothing in `learn/` allocates per frame. The confetti simulation returns a
  list per frame by design — keep `count` at 24 (48 on a combo milestone) and
  do not raise it "because it looks better".
* Target 60 fps on a mid-range phone. If a screen drops frames, the fix is
  fewer simultaneous effects, not a shorter duration.

## Battery and data

* No network, no background work, no `WorkManager`, no notifications, no
  wake locks. The app does nothing at all when it is not on screen.
* No analytics. Nothing to send and nowhere to send it
  (`docs/decisions/0004`).
