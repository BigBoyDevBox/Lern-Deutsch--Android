# 09 — Mimi, the mascot

Built in **roadmap step 6**. `ui/components/MascotView.kt`.

## Who she is

**Mimi · 咪咪** — a small pink cat. 咪咪 is the generic Chinese cat name, the
way "Kitty" is in English, so it reads as instantly familiar to the learner and
is pronounceable in German. She is the app's face: she is on the launcher icon,
on the home screen, and beside every answer.

She is the descendant of the unnamed cat in the Pebble app, and she keeps that
cat's job: reacting, not narrating. **Mimi never speaks.** No speech bubbles,
no encouraging sentences. She changes expression, and that is all.

## How she is drawn

A Compose `Canvas`, vector maths only — no bitmaps, no Lottie, no dependency.
She has to scale from a 32dp corner badge to a 160dp home-screen banner without
an asset per size.

```kotlin
@Composable
fun MascotView(
    mood: Mood,
    modifier: Modifier = Modifier,
    fur: Color = Candy.Bubblegum,
)

enum class Mood { NEUTRAL, HAPPY, SAD, SLEEPY, PARTY }
```

Geometry, all proportional to the radius `r` of the head:

* **Head** — a circle of radius `r`.
* **Ears** — triangles rooted **inside** the head outline, drawn *before* the
  head so the head circle swallows their bases. The Pebble app learned this the
  hard way: ears anchored at the head's equator float free once the circle
  narrows toward the top, and the bug got worse the bigger the cat
  (`awesome.md` 1.7). Inner ears are a lighter pink triangle inset by `0.25r`.
* **Eyes** — two filled circles at `(±0.42r, -0.1r)`, radius `0.16r`, with a
  white catchlight at the upper left. This one dot is most of the cuteness.
* **Nose** — a small downward triangle at `(0, 0.15r)`.
* **Whiskers** — three strokes each side, `0.9r` long, rounded caps.
* **Blink** — every 3–5 s the eyes become 2dp-thick horizontal lines for
  120 ms. Drive it from a single coroutine with a seeded delay, and **stop it
  when the composable leaves composition**.

## Moods

| Mood | Eyes | Mouth | Ears | Extra |
|---|---|---|---|---|
| `NEUTRAL` | open circles | small `ω` | up | idle blink |
| `HAPPY` | happy arcs (`⌒ ⌒`) | open smile | up, wiggling ±6° | three hearts float up and fade |
| `SAD` | open, lowered | small downward arc | flattened outward | one sweat drop |
| `SLEEPY` | closed lines | tiny `o` | drooping | a `Zzz` drifting up |
| `PARTY` | star-shaped | wide open | up | a party hat, confetti behind |

Mood is driven by `RoundUiState.mascotMood` and returns to `NEUTRAL` after
900 ms — the ViewModel owns that timer, not the composable.

`SLEEPY` appears on the home screen when the daily goal has not been started
and the last round was over 24 h ago. It is the app's only nag, and it does not
say anything.

`PARTY` is for level-ups, a new sticker, and a completed deck.

## Where she appears

| Screen | Size | Mood |
|---|---|---|
| Home banner | 120dp | `SLEEPY` if idle today, else `NEUTRAL` |
| Round feedback | 64dp, beside the verdict | `HAPPY` / `SAD` |
| Round summary | 140dp | `PARTY` at 3 stars, else `HAPPY` |
| Sticker album header | 96dp | `NEUTRAL`, blinking |
| Empty states | 88dp | `SLEEPY` |

## Petting her

On the home screen and in the album, tapping Mimi pets her: she closes her eyes
happily, purrs (a soft double haptic), and a ring of hearts appears. Nothing is
scored and nothing is unlocked.

This is undocumented on purpose — the best easter eggs are found, not
announced — and it is ported from the Pebble app, where it was the single
most-liked addition.

## Elfie, the guest cat

Pet Mimi **three times in a row** and Elfie pads in to claim the rest of the
pets, then slips away when the petting stops.

Elfie is a real tortoiseshell Devon Rex and belongs to the repo owner. She is
drawn from the Pebble app's `lg_draw_elfie` (port the proportions rather than
inventing them): a wide wedge face — an ellipse a quarter wider than tall —
chrome-orange tortie coat, a black forehead blaze, a black mask around her
**right** eye, huge chartreuse slit-pupil eyes with dark eyeliner,
satellite-dish Devon ears that flatten when she is sad, a white chin, a salmon
nose, and no collar, because she doesn't wear one.

She does not replace Mimi and never appears anywhere else.

## Rules

* Mimi is drawn, never photographed, and never animated with a video.
* She reacts within 100 ms of an answer. A late reaction reads as a bug.
* She is never on screen during a Blitz round — a timed mode has no room for
  something to look at ([16](16-mode-blitz.md)).
* Reduce-motion: no wiggle, no floating hearts, no blink. The expressions stay.

## Tests

Mood selection is decision logic and therefore lives outside the composable, in
`learn/` or in the ViewModel: `moodFor(correct: Boolean?, stars: Int?, idle:
Boolean): Mood`. Test that table. The drawing itself is not unit-tested — use
`@Preview` composables, one per mood, and look at them.
