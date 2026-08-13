# 19 — Three languages on one screen

The app's UI runs in three locales, and its *content* is in three languages at
once. Those are different problems.

## Locales

| Folder | Language | Role |
|---|---|---|
| `values/` | English | the default and universal fallback |
| `values-b+zh+Hans/` | Chinese (Simplified) | what the learner will actually see |
| `values-de/` | German | immersion mode, and the developer's language |

All three are listed in `res/xml/locales_config.xml`, which is what powers the
Android 13+ per-app language picker. **Add a locale to that file in the same
change that adds its `values-*` folder**, or the picker will not offer it.

`values/` is English rather than Chinese because it is the fallback for every
device set to a fourth language, and because lint's `MissingTranslation`
compares against the default — a gate that only works if the default is
complete. It is a hard CI gate here.

## Swiss spelling

Always *ss*, never *ß* — in German UI strings exactly as in the word list. The
learner should never see the app spell a word one way and a card the other.

## The bilingual label rule

Screens read „Karten · 卡片", „Der Die Das · 冠词", „Aufkleber · 贴纸". That
looks like a translated string but must not be one, because the German half is
**content** — it is a word she is supposed to learn — and translating it away
in the German locale would be a bug.

So:

```xml
<!-- values/strings.xml -->
<string name="mode_karten_de" translatable="false">Karten</string>
<string name="mode_karten">Cards</string>
```

```kotlin
BilingualLabel(
    german = stringResource(R.string.mode_karten_de),
    local  = stringResource(R.string.mode_karten),
)
```

which renders „Karten · 卡片" on a Chinese device, „Karten · Cards" in English,
and just „Karten" in German (the composable drops the second half when the two
strings are equal — the German locale defines `mode_karten` as `Karten`).

`translatable="false"` on the German half is what keeps `MissingTranslation`
quiet and states the intent in the file.

Everything gets this treatment: mode names, the three Wortarten, the three
gender names, screen titles, the summary's verdicts.

## What is never translated

* German vocabulary, obviously.
* The article words *der/die/das* — they are the answer.
* `Wortart` names as shown on the buttons: „Nomen", „Verb", „Adjektiv" stay
  German, with the localized name underneath. She needs the German terms.
* The app name and the mascot's name.
* Praise and nudge lines — they come from the word list (`ui.praise`) and are
  Chinese by design, in all locales. They are a wink from the Pebble app, not
  UI chrome.

## CJK layout rules

* **Line height.** Chinese glyphs are square and need more leading than Latin.
  The typography scale already sets generous line heights; never tighten them
  for a "cleaner" look.
* **Optical size.** Chinese at the same `sp` reads smaller and denser than
  Latin. The answer face uses `headlineLarge` (32sp) for Chinese against
  `displayLarge` (52sp) for German — that is intentional, not an inconsistency.
* **No letter-spacing** on Chinese. It looks broken.
* **Never truncate mid-word.** Chinese has no spaces, so an ellipsis can cut a
  compound in half. Shrink the type instead.
* **Punctuation.** Use the Chinese full-width forms in Chinese strings (，。！
  ？), and `·` as the separator in bilingual labels in every locale.

## String rules

* Every user-visible string is a resource, in all three files. No exceptions
  and no string concatenation in Kotlin to build a sentence — plurals and word
  order differ.
* ViewModels and `learn/` have no `Context`, so failures and labels travel as
  `@StringRes Int` and the composable resolves them.
* Use `<plurals>` for anything counted, even where English and German look
  regular. Chinese has no plural forms and will simply define one `other` case.
* Format with positional arguments (`%1$s`) so a translator can reorder.

## Tests

Lint is the test: `MissingTranslation` fails the build, and every string in
`values/` must exist in the other two. When adding a string, add all three at
once — a "TODO translate" that reaches `main` never gets done.
