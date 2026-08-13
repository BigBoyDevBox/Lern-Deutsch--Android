# Glossary

Terms of art in this repo — German grammar words the app teaches, and the
internal vocabulary the code uses. Both matter: half the identifiers in
`learn/` are German nouns.

## German, as the app uses it

- **Wortart** (pl. *Wortarten*) — word class / part of speech. The app plays
  with three: **Nomen** (noun), **Verb**, **Adjektiv**. Everything else is
  `other` and is never asked about (`docs/decisions/0006`).
- **Artikel** — *der* (masculine), *die* (feminine), *das* (neuter). Colour
  coded blue / pink / green throughout, identical to the Pebble app.
- **Genus** — grammatical gender. The code calls the field `gender` and the
  values `DER`/`DIE`/`DAS`, named after the article the learner has to produce
  rather than after the grammatical term, because the article is what she
  types, taps and remembers.
- **Karte** / **Karten** — a flashcard; also the name of the flip-card mode.
- **Runde** — a round: one session of ~16 cards, ending when every card has
  been answered correctly once.
- **Blitz** — lightning. The 60-second timed mode.
- **Serie** — the daily streak. Shown as a flame.
- **Rekord!** — the gold tag on a round summary that beat a stored best.
  Inherited from the Pebble app, where it is the most-noticed feature.
- **Aufkleber** — sticker. The collectible rewards and their album.
- **Stufe** — a difficulty tier: Anfänger, Grundstufe, Mittelstufe,
  Fortgeschritten.

## Internal vocabulary

- **Card** — one vocabulary item: German, Chinese, English, gender, Wortart.
  Its `id` is `"<deckKey>/<german>"` and every piece of saved progress hangs
  off it.
- **Deck** — a themed set of 12–22 cards ("Farben", "Im Restaurant"). 81 of
  them, each with an accent colour and an emoji.
- **Tier** — one of four difficulty bands grouping the decks.
- **Direction** — which language is asked and which is answered: `DE_ZH`,
  `ZH_DE`, `DE_EN`, `EN_DE`. The two English ones are how English rides along
  without being a separate app.
- **Round engine** — the shared session loop (`learn/round/Round.kt`): a
  shuffled queue, a correct answer leaves it, a wrong one goes to the back.
  Ported from the Pebble app's `study.c`.
- **First-try correct** — a card answered correctly on its first appearance in
  a round. The only input to the star rating; the whole point is that a retry
  finishes the round but does not buy a star.
- **Leitner box** — one of five scheduling buckets (0–4) with fixed intervals
  0/1/2/4/8 days. Box 4 means **mastered**. Right moves a card up one; wrong
  drops it to 0.
- **Due** — a card whose `dueDay` has arrived. Due cards are chosen before new
  ones.
- **Selection** — the pure function that decides which cards a round contains:
  due, then new, then lowest-box filler.
- **Question** — a fully resolved prompt/answer/choices triple. The UI renders
  one and knows nothing about how it was chosen.
- **Distractor** — a wrong answer in Quiz. Drawn from the same deck, never a
  duplicate of the correct answer *text*.
- **Juice** — the game-design word for feedback that makes an input feel like
  it did something: confetti, springs, ticks, combos.
- **Combo** — consecutive correct answers. Drives Blitz's multiplier and the
  size of the confetti burst.
- **Candy** — the palette object (`ui/theme/Color.kt`) and the design language:
  cream ground, white sticker cards, soft pink shadows, no sharp corners.
- **Sticker card** — the app's one surface primitive. Everything sits on one.
- **Mimi · 咪咪** — the mascot, a small pink cat. She reacts; she never speaks.
- **Elfie** — a real tortoiseshell Devon Rex, and an easter egg: pet Mimi three
  times and Elfie claims the rest of the pets.
