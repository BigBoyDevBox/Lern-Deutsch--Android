# 08 — Juice: how right and wrong feel

Built in **roadmap steps 7 and 8**. "Juice" is the game-design word for the
feedback that makes an input feel like it did something. It is the difference
between a quiz and a game, and it is not optional polish here — see
[01](01-product.md).

Rule zero: **every effect respects reduce-motion** ([20](20-accessibility.md)).
When it is on, motion is replaced by a state change, never by nothing.

## The correct-answer sequence (~700 ms)

Fired in parallel the moment a correct answer lands:

1. **Confetti burst** from the tapped control, 24 particles.
2. **Spring scale** on the answered card: 1.0 → 1.06 → 1.0, `spring(dampingRatio
   = 0.45f, stiffness = 500f)`. Overshoot is the point; a linear tween reads as
   a progress bar.
3. **Colour flash**: the card's border goes `Candy.Correct` and fades over
   400 ms.
4. **A tick** — a short rising two-note blip, and a light haptic
   ([18](18-audio-haptics.md)).
5. **Mimi goes happy** for 900 ms ([09](09-mascot.md)).
6. **Combo** — if the combo is now ≥ 3, the counter pops in with a scale
   overshoot and its colour warms with the streak (see below).

Reduce-motion: no confetti, no scale. The border flash and the colour change
stay, and the tick still plays.

## The wrong-answer sequence (~600 ms)

Sympathetic, not punishing. Nothing shakes hard, nothing goes red-red.

1. **A short horizontal wobble** on the card: ±8dp, 3 cycles, 240 ms. Not a
   screen shake — shaking the whole screen implies the app is upset.
2. **The right answer appears** immediately beneath the wrong choice, in
   `Candy.Correct`, and stays until the learner advances. It is the only part
   of this sequence that actually teaches; do not put it on a timer that can
   expire before it is read.
3. **A soft double-tick** haptic, distinct from the correct buzz — copied from
   the Pebble app, where distinguishable haptics let a learner grade without
   looking.
4. **Mimi goes sad** for 900 ms, then returns to neutral.
5. **The combo resets to 0**, and the counter deflates rather than vanishing.

Reduce-motion: no wobble. Everything else stays.

## Confetti (`learn/juice/Confetti.kt`, pure)

A particle simulation with no Android types, so it can be tested.

```kotlin
data class Particle(
    val x: Float, val y: Float,          // normalized to the burst origin
    val rotation: Float,
    val colorIndex: Int,                 // index into a palette the UI supplies
    val scale: Float,
    val alpha: Float,
)

class Burst(seed: Long, val count: Int = 24) {
    /** State of every particle [tMillis] after the burst. */
    fun at(tMillis: Long): List<Particle>
    val durationMillis: Long              // 900
}
```

Model: each particle gets a seeded initial speed (200–520 px/s), an angle in a
120° upward fan, a spin rate and a colour index. Then
`x = vx·t`, `y = vy·t + ½·g·t²` with `g = 1400 px/s²`, alpha fading over the
last 300 ms, scale shrinking to 0.6.

Draw it in a `Canvas` inside the round screen, driven by a
`withInfiniteAnimationFrameMillis`-style loop that stops when the burst ends —
**never leave an animation running after its effect is over**. The Pebble app
learned this one as a battery bug (`awesome.md` 2.1); on a phone it shows up as
a hot device.

Tests: particle count is exactly `count`; the same seed gives identical output;
positions are continuous in `t`; every particle has `alpha == 0f` at
`durationMillis`; no `NaN` at any `t` in `0..durationMillis`.

## Combo

The combo counter is the strongest single "one more round" mechanic and costs
almost nothing. Maintained by the ViewModel, displayed from `RoundUiState`.

* +1 per correct answer, reset to 0 on a wrong one.
* Hidden below 3. At 3 it pops in.
* Colour warms with height: 3–4 `Candy.Gold`, 5–9 `Candy.Flame`, 10+
  `Candy.Bubblegum` with a gentle pulse.
* At every multiple of 5 the confetti burst doubles to 48 particles and the
  haptic gets a second beat.
* XP consequences live in [17](17-rewards.md); the counter itself is pure
  feedback.

## The progress pill

Tracks **cards mastered / total**, not questions asked
([06](06-round-engine.md)). It therefore only ever moves forward — a missed
card does not rewind it, it simply doesn't advance. Animate the fill with a
`spring`, and let a small highlight sweep across it each time it grows.

## Transitions

* Screen to screen: shared-axis slide + fade, 300 ms.
* Card to card within a round: the answered card slides out left while the next
  scales up from 0.92 with a spring. 280 ms, overlapping.
* The flashcard flip: a real Y-rotation with a 12dp camera distance, 380 ms,
  with the back face's text drawn only past 90°.
* Summary entrance: stars land one at a time, 120 ms apart, each with a scale
  overshoot and a tick. This is the app's biggest single moment — do not
  fade all three in at once.

## Never

* Never block input during an effect. A learner who already knows the next
  answer must be able to answer it. Effects run over the top; the next question
  is live as soon as it is on screen.
* Never animate for longer than 900 ms. Anything longer gets skipped by the
  second week.
* Never celebrate nothing. Confetti for opening a screen devalues confetti for
  a right answer.
