# 01 — Product: who this is for

## The learner

One real person: a 14-year-old girl whose first language is Chinese
(Simplified), who is learning **German** and already reads some **English**.
She has a phone. She is not a beginner at using apps and will not be charmed by
anything that feels like homework with stickers stapled on.

Everything below follows from that.

## What she needs, in priority order

1. **German vocabulary** — the bulk of the work, and what the Pebble app
   already does well. 1204 words, Chinese on the other side.
2. **Noun gender** — *der / die / das*. The single hardest thing about German
   nouns for a Chinese speaker, because Chinese has no grammatical gender at
   all. There is no rule to learn; it has to become a reflex, which means
   drilling with immediate feedback.
3. **Wortarten** — *Nomen / Verben / Adjektive*. School asks for this
   explicitly. It is also the vocabulary of every grammar explanation she will
   read for the next four years, so knowing the three words is worth more than
   knowing any ten nouns.
4. **English, alongside** — she has to learn it too and already knows some.
   Every card already carries an English gloss, so English is not a fourth
   subject; it is a second bridge to the same German word. Modes that can ask
   in English do.

## Tone: what "cute" has to mean here

Kawaii, not babyish. A 14-year-old can tell the difference instantly and will
delete an app that gets it wrong.

* **Cute** = round shapes, candy colours, a cat with opinions, confetti,
  springy motion, an emoji where an icon would do.
* **Not** = baby talk, gold stars for existing, cartoon voices, anything that
  congratulates her for tapping a button.

The praise lines come from the word list (`ui.praise` — 太棒了 / 真厉害 /
好极了) and are short. The mascot reacts; it does not narrate.

## What "fun" has to mean concretely

Fun is not a coat of paint applied at the end. It is these five things, and
each has a file:

| Feeling | Mechanism | Where |
|---|---|---|
| That felt good | Confetti burst, spring scale, a satisfying tick | [08 juice](08-juice.md) |
| I am getting somewhere | XP, levels, stars per deck, the sticker album | [17 rewards](17-rewards.md) |
| I don't want to break it | The daily streak flame | [17 rewards](17-rewards.md) |
| Someone noticed | Mimi's mood changes with the answer | [09 mascot](09-mascot.md) |
| One more round | Blitz's 60-second timer and combo counter | [16 blitz](16-mode-blitz.md) |

A wrong answer must never feel punishing. It shows the right answer, Mimi looks
sympathetic rather than disappointed, the card comes back later in the same
round, and the streak is untouched. The only thing a miss costs is a star.

## What this app deliberately is not

* **Not a course.** No lessons, no grammar explanations, no units to unlock in
  order. She has a school and a teacher for that. This is the drill.
* **Not social.** No accounts, no leaderboards, no friends, no sharing. It is
  offline (`docs/decisions/0004`), which also means no privacy questions to
  answer and nothing to moderate.
* **Not a Pebble port.** The watch app is the sibling and the source of the
  word list, the star rule, the streak rule and the article colours. It is not
  the design.
* **Not endless.** 1204 words is a finite, completable thing, and the app
  should say so — "Decks 12/81" is a better motivator than an infinite feed.

## Success, stated so it can be checked

She opens it without being asked, on more days than not, for longer than a
month. Everything in the roadmap is in service of that, and anything that
isn't should be argued for on those terms.
