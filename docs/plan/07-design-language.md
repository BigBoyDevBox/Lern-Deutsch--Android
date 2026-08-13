# 07 — The design language: Zuckerwatte

Candy floss. Light, round, soft-shadowed, generous. The opposite of a form.

## Colour

All colour comes from `ui/theme/Color.kt` (`Candy`). **No ad-hoc `Color(0x…)`
in a screen, ever.** If a screen needs a colour the palette lacks, add it to the
palette with a name.

### The article colours are not decoration

```
der  #3E9BFF  blue      die  #FF5FA2  pink      das  #26C6A6  green
```

Identical to the Pebble app, so the association a learner already built on the
watch transfers to the phone. **They are never used for anything else** — not
for a button, not for a tier, not for a deck accent. When these three colours
appear, they mean gender.

### Ground and ink

| Token | Hex | Use |
|---|---|---|
| `Cream` | `#FFF8F3` | the page |
| `Blush` | `#FFE7F0` | banners, the icon backdrop |
| `Lavender` | `#F1EAFF` | secondary surfaces |
| `Card` | `#FFFFFF` | every sticker/card surface |
| `Ink` | `#3A2F4A` | primary text (a warm near-black, never `#000`) |
| `InkSoft` | `#7A6E8C` | secondary text |
| `Shadow` | `#1A3A2F4A` | the one shadow colour |

### Brand and verdicts

`Bubblegum #FF5FA2` (primary), `BubblegumDeep #E03C82` (pressed),
`Correct #2ECC8F`, `Wrong #FF7A6B`, `Gold #FFC53D` (stars, records),
`Flame #FF8A3D` (streak).

`Wrong` is a warm coral, not red. A miss is not an error.

### Deck accents

Fourteen tokens (`rose honey aqua cherry mint clover orchid peach tangerine sky
grape apricot lime lemon`), assigned by `tools/gen_vocab.py`. Screens call
`Candy.accent(deck.accent)`. `CandyPaletteTest` fails on an undefined token or
an unused colour.

## Light-only, deliberately

`docs/decisions/0003`. A candy surface is a light design; the dark version is a
*different* design, not the same one with inverted tokens, and shipping a
mechanical inversion would look broken. `isSystemInDarkTheme()` is deliberately
ignored — do not wire it up without the dark palette the ADR asks for.

## Shape

Nothing in this app has a sharp corner.

```
extraSmall 10dp   small 14dp   medium 20dp   large 28dp   extraLarge 36dp
```

Cards use `large`. Buttons are `extraLarge` or fully round. The flashcard face
is `36dp`. Chips are pills (`CircleShape`).

## Elevation: stickers, not Material

Surfaces are stickers on a page: a white card, a soft offset shadow, sometimes
a 2dp white border. Use `Modifier.shadow(elevation, shape, ambientColor =
Candy.Shadow, spotColor = Candy.Shadow)` rather than Material's tonal
elevation, which tints surfaces and muddies the palette.

Standard elevations: resting card 4dp, pressed 1dp, floating/primary 8dp.

## Type

System fonts only. The app renders German, Simplified Chinese and English side
by side and no bundled Latin font covers CJK — the device font does, in every
weight, for zero bytes. Personality comes from size and weight instead.

| Style | Size / weight | Use |
|---|---|---|
| `displayLarge` | 52sp Black | the word on a flashcard |
| `displayMedium` | 40sp Black | app title, big numbers |
| `headlineLarge` | 32sp ExtraBold | screen titles |
| `headlineMedium` | 26sp Bold | the Chinese answer |
| `titleLarge` | 22sp Bold | tier headings, deck names |
| `titleMedium` | 18sp SemiBold | list rows |
| `bodyLarge` | 17sp | prose |
| `bodyMedium` | 15sp | secondary lines |
| `labelLarge` | 15sp Bold | buttons, chips |

Never below 15sp. Line height is generous — CJK needs it.

## Spacing

A 4dp grid. Screen padding 20dp. Between cards in a list 12dp. Inside a card
16dp. Between a heading and its content 8dp.

## Components (`ui/components/`, built in step 4)

| Component | What it is |
|---|---|
| `CandyButton` | The primary action. Pill, filled, 56dp tall, springs to 0.94 on press. |
| `CandyIconButton` | A round 48dp emoji/icon button. |
| `StickerCard` | The white rounded surface with the soft shadow. Everything sits on one. |
| `ArticleChip` | `der`/`die`/`das` in its own colour, always with its word. |
| `StarRow` | 1–3 stars, gold when earned, faint outline when not. |
| `StreakFlame` | The flame + day count. |
| `ProgressPill` | The round's progress bar — rounded, with a moving highlight. |
| `MascotView` | Mimi ([09](09-mascot.md)). |

Every one takes a `Modifier` as its first optional parameter and hoists its
state. None of them reads a ViewModel.

## Layout rules

* **Thumb-first.** The primary action lives in the bottom third. A learner
  holds a phone one-handed on a bus.
* **One decision per screen.** A round screen shows a prompt and its answers,
  and nothing else competes.
* **Nothing scrolls during a round.** If content does not fit, shrink the type
  (`autoSize` on the word) rather than introducing a scroll — a scrollbar
  during a timed round is a bug.
* **Minimum touch target 48dp**, spacing 8dp between adjacent targets.
