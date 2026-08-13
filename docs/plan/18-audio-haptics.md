# 18 — Sound, speech and haptics

Built in **roadmap step 8** (haptics + tones) and **step 16** (speech).

## Pronunciation: German text-to-speech

The highest-value feature in this file. A learner who has only ever *read*
„Eichhörnchen" cannot say it, and a Chinese speaker has no intuition for German
vowel length or the ch sounds.

Android ships `android.speech.tts.TextToSpeech`, on-device, free, no
permission, no network. Use it.

```kotlin
// data/Speaker.kt — the Android side
@Singleton
class Speaker @Inject constructor(@ApplicationContext context: Context) {
    /** True once an engine reported a usable German voice. */
    val germanAvailable: StateFlow<Boolean>
    fun say(text: String)
    fun stop()
    fun shutdown()
}
```

Rules:

* **Locale `Locale.GERMANY`.** Check `isLanguageAvailable` and keep
  `germanAvailable` false on `LANG_MISSING_DATA` / `LANG_NOT_SUPPORTED`.
* **Degrade silently.** No German voice → the 🔊 buttons are simply not shown.
  Never show a dialog telling a 14-year-old to install a TTS voice; never link
  out to Play.
* **Strip the article** before speaking a noun? **No** — speak „der Hund" in
  full. Hearing the article with the noun is the entire point.
* Speak on tap only. Never auto-speak on card reveal: she may be in class, on a
  bus, or next to a sleeping sibling.
* `shutdown()` from the Activity's `onDestroy`. A leaked TTS engine keeps a
  service bound.
* Queue mode `QUEUE_FLUSH` — tapping twice means "say it again", not "say it
  twice".

Where the 🔊 appears: the Karten front (German directions) and back (German
answers), the Quiz prompt when it is German, and the Artikel prompt card.

## Tones

No audio assets. Tones are generated at runtime from a pure spec, which keeps
the APK free of licensing questions and makes the "sound design" testable.

```kotlin
// learn/audio/ToneSpec.kt — pure
data class ToneSpec(val steps: List<Step>) {
    data class Step(val hz: Float, val ms: Int)
    companion object {
        val CORRECT = ToneSpec(listOf(Step(880f, 70), Step(1318f, 90)))   // rising
        val WRONG   = ToneSpec(listOf(Step(440f, 90), Step(370f, 110)))   // falling
        val LEVEL_UP = ToneSpec(...)   // an arpeggio
        val COMBO_5 / COMBO_10 / COMBO_20
        val GOAL_DONE
    }
    /** 16-bit mono PCM at [sampleRate], with a 5 ms fade in/out per step. */
    fun pcm(sampleRate: Int = 22_050): ShortArray
}
```

`data/TonePlayer.kt` writes the `ShortArray` to an `AudioTrack` with
`USAGE_GAME` / `CONTENT_TYPE_SONIFICATION`, so the tones respect the *media*
volume and duck properly.

The fade is not optional: a raw sine cut mid-cycle clicks, and a click on every
correct answer is the fastest way to make a learner mute the app.

Tests (`ToneSpecTest`): the PCM length matches the summed durations at the
sample rate; the first and last samples are 0 (proving the fade); no sample
clips at ±32767; an empty spec yields an empty array.

## Haptics

Ported from the Pebble app, where distinguishable haptics let a learner grade a
card without looking. On a phone they carry the same load.

| Event | Haptic |
|---|---|
| Correct | `HapticFeedbackType.Confirm` — one crisp tick |
| Wrong | a soft double tick, ~40 ms / 70 ms / 40 ms |
| Combo milestone | a triple ascending tick |
| Level up / sticker | a longer celebratory pattern |
| Blitz under 10 s | one soft tick per second, like a heartbeat |
| Petting Mimi | a purr — two long soft buzzes |

Use `VibratorManager` + `VibrationEffect.createWaveform` for the patterns and
Compose's `LocalHapticFeedback` for the simple ones. Guard every call on
`Settings.hapticsOn` and on `Vibrator.hasVibrator()`.

## Settings

Three switches ([step 16](23-roadmap-modes.md)): **Ton · 声音**, **Vibration ·
震动**, **Aussprache · 发音** (hidden entirely when no German voice exists).

All three default **on**. A silent educational app is a worse educational app,
and the switches exist for the classroom, not as the default posture.

## Never

* Never play background music. There is no music budget, no licence, and a
  drill app with a loop is unbearable by day three.
* Never make sound the only feedback — the app must be fully playable muted.
* Never vibrate for longer than 300 ms in one event.
