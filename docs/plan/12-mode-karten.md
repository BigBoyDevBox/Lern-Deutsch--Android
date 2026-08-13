# 12 — Karten · 卡片

Built in **roadmap step 10**. The classic flip card, self-graded. The mode the
Pebble app is, done on a big screen.

## The loop

1. The front shows the prompt word, big.
2. Tap the card (or the **Umdrehen · 翻** button) — it flips.
3. The back shows the answer, the English gloss, and for a noun the article in
   its colour.
4. Two buttons: **✓ Gewusst · 会了** and **✗ Noch nicht · 还不会**.
5. Correct leaves the queue; wrong comes back later
   ([06](06-round-engine.md)).

Self-grading rather than multiple choice, because recall and recognition are
different skills and this is the recall one. Quiz ([15](15-mode-quiz.md)) is
the recognition mode.

## Direction

Four, chosen on the deck picker and remembered in `Settings`:

| Direction | Front | Back |
|---|---|---|
| `DE_ZH` | Hallo | 你好 |
| `ZH_DE` | 你好 | Hallo |
| `DE_EN` | Hallo | hello |
| `EN_DE` | hello | Hallo |

The direction chip sits at the top of the round screen and is tappable — a
learner who finds a direction too easy can flip it without leaving the round.
Changing it mid-round applies from the next question and does not reshuffle.

## The card

Full-width `StickerCard`, roughly 2:3, centred, `36dp` corners, 8dp shadow.

**Front**

* The word, `displayLarge` (52sp), auto-shrinking to fit — some cards are whole
  sentences („Können Sie mir helfen?"). Never wrap to three lines; shrink
  instead.
* For a German noun, the article is drawn in its gender colour and the noun in
  `Ink`. This is the whole gender-teaching mechanism in this mode, and it is
  free.

  Do not split `Question.prompt` on a space to do this. `Question` carries the
  whole `Card`, so the face reads `question.card.gender` and
  `question.card.withoutArticle` and never parses a string — which also means
  „die Speisekarte, bitte" (a phrase that merely starts with an article, gender
  `NONE`) renders as plain text, exactly as it should.
* Nothing else. No hint, no gloss, no button labels on the card itself.
* A small **🔊** in the corner for German fronts ([18](18-audio-haptics.md)).
* A **↻** in the opposite corner when `Question.repeat` is true — "you've seen
  this one, it bit you". Ported from the Pebble app.

**Back**

* The answer, `headlineLarge` (Chinese renders larger than Latin at the same
  sp, so 32sp Chinese ≈ 40sp German — that is intended).
* Under it, the English gloss in `bodyLarge` `InkSoft`, when `settings.englishOn`.
  On `DE_EN`/`EN_DE` the *Chinese* takes that slot instead — the third language
  is always present, whichever two are being tested. This is how English rides
  along without being a separate mode.
* For nouns, an `ArticleChip` — the article, in its colour, with the word
  „männlich · 阳性" etc. underneath in small type.

## The flip

A real Y-rotation ([08](08-juice.md)): 380 ms, `graphicsLayer { rotationY =
angle; cameraDistance = 12 * density }`, back-face content drawn only past 90°
and mirrored back with `rotationY = 180f`. Tapping again flips back — peeking
is allowed and costs nothing.

## Grading

The two buttons only appear on the back, and they appear *immediately* rather
than after the flip completes — waiting for an animation to grade a card you
already know is the fastest way to make a drill feel slow.

`✓` is `Candy.Correct`, `✗` is `Candy.Wrong`, both `CandyButton`s, both at
least 56dp tall, side by side in the bottom third.

Honesty is the learner's business. There is no "are you sure", no timer forcing
a grade, and no penalty for peeking.

## Files

```
ui/round/karten/KartenFace.kt      the front/back composables + the flip
learn/round/KartenQuestion.kt      kartenQuestion(card, direction, repeat)
```

`kartenQuestion` is a pure function:

```kotlin
fun kartenQuestion(card: Card, direction: Direction, repeat: Boolean) = Question(
    card = card,
    prompt = card.text(direction.prompt),
    promptLang = direction.prompt,
    answer = card.text(direction.answer),
    answerLang = direction.answer,
    repeat = repeat,
)

fun Card.text(lang: Lang) = when (lang) { Lang.DE -> de; Lang.ZH -> zh; Lang.EN -> en }
```

## Done when

* `KartenQuestionTest`: all four directions map the right field to prompt and
  answer; a noun's prompt keeps its article on `DE_*` and the answer keeps it
  on `*_DE`.
* A round of a real deck can be played start to finish, the summary appears,
  and the stars match `Stars.forRound`.
* Rotating the phone mid-round keeps the same card and the same face.
