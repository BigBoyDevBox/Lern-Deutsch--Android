# Review ledger

Findings from the automated GLM 5.2 review that were **declined** or
**refuted**, with the reasons, so later rounds and later agents don't
re-litigate them. Applied findings need no entry — the diff is the record.

Add an entry when you decline or refute something. Keep it short: what was
claimed, what was decided, and the evidence.

Policy: [CLAUDE.md](CLAUDE.md). Triage is apply / decline with recorded
reasons / refute with evidence, and never flip-flop on an entry here without
genuinely new evidence.

---

## PR #1 — Plan and scaffolding

### Refuted — the claim is factually wrong

**„`Mood.NEUTral` is a compile error" (labelled BLOCKER).**
`docs/plan/11-home-screen.md:118` reads `val mascotMood: Mood = Mood.NEUTRAL,`
and always has. The lower-case spelling the finding quotes does not appear
anywhere in the repository (`grep -n NEUT docs/plan/`). Nothing to fix.

**„KSP 2.3.10 is incompatible with Kotlin 2.4.10 — blocking all builds."**
The build runs. `:app:kspDebugKotlin` executes the Hilt processor and
`testDebugUnitTest lintDebug assembleDebug` is green locally and in CI on every
commit of this PR. The finding's premise — that a KSP version must embed the
Kotlin version (`2.0.21-1.0.28`) — describes KSP 1; KSP 2 versions
independently. The pair is also the one the sibling repo Meltorama 2000 ships.

**„Add „groß"/„weiß"/„heiß"/„großzügig" to `ADJECTIVES` in `vocab_pos.py`."**
Applying this would break the build, twice over. `validate_vocab.py` errors on
any `ADJECTIVES` entry that is not in the word list (`ADJECTIVES: %r is not in
the word list`), and separately errors on any `ß` in the word list at all. The
suggested entries satisfy neither check.

**„`Blitz.kt (step 14)` should be step 16" and „`Distractors.kt (step 12)`
should be step 15."**
Both annotations were already correct. `docs/plan/23-roadmap-modes.md` defines
step 12 as Quiz and step 14 as Blitz; the finding read the *plan-file* numbers
(`15-mode-quiz.md`, `16-mode-blitz.md`) as step numbers. The confusion was real
enough to happen twice, so the two sequences are now disambiguated in
`02-architecture.md` and the annotations name their mode — but the numbers
stand.

**„`enableEdgeToEdge()` should be called after `super.onCreate()`."**
The opposite of the documented order. Android's edge-to-edge guide and the
`androidx.activity` samples call it *before* `super.onCreate(savedInstanceState)`,
which is what `MainActivity` does. Left as is.

### Declined — correct observation, deliberate choice

**„Hardcoded password in `app/build.gradle.kts:27,29" (labelled BLOCKER, ×2).**
The standard `android`/`androiddebugkey` credentials of the checked-in debug
keystore. They are public by construction and documented in
`docs/decisions/0002`: zero-secret CI, reproducible builds, sideload-only
distribution. A generic secret-scanner pattern, not a secret.

**„`ci.yml` uses floating action tags while `release.yml` pins SHAs."**
Deliberate, and the same split the sibling repo uses. `release.yml` is the
privileged workflow — it receives `contents: write` and publishes artifacts — so
every action in it is pinned to a verified immutable commit. `ci.yml` runs with
`contents: read`, holds no secrets, and pinning it would turn every Dependabot
action bump into a manual SHA lookup for no change in threat model. If the
family ever pins everything, it should change in all repos at once, not here.

**„`tools/vocab.py` should use ß."**
Swiss Standard German has no ß; *ss* is correct orthography, not a corruption,
and the repository owner is in Switzerland. The rule is stated in `README.md`,
`AGENTS.md`, `docs/plan/00-index.md` (rule 4) and
`docs/plan/19-localization.md`, enforced by `validate_vocab.py` with the message
„uses ß — this project spells Swiss (always ss)", and re-checked by
`VocabAssetTest`. The finding's fallback theory — a font/glyph limitation —
does not apply either: the Pebble sibling renders umlauts and ß in system fonts
without trouble.

**„`vocab.py` uses 你 where the German says Sie; should be 您."**
A fair linguistic observation, declined *here* for two reasons. `tools/vocab.py`
is byte-identical to the Pebble app's copy on purpose
(`docs/decisions/0001`), so a content edit belongs upstream and gets copied
across, not made in this repo. And the choice is defensible: 您 is markedly more
formal in Mainland usage than Sie is in German, so 你 is a reasonable register
for a learner's phrasebook. **Deferred:** raise it on
`L-K-M/Lern-Deutsch--Pebble` as a vocabulary question for a human to settle.

### Round 2 — refuted

**„`app/proguard-rules.pro` is missing a `$$serializer` keep rule; release
builds may crash deserializing `vocab.json`" (labelled BLOCKER).**
Checked against a real minified build rather than argued. `./gradlew
assembleRelease` with `isMinifyEnabled = true`, then
`app/build/outputs/mapping/release/mapping.txt`:

```
ch.lkmc.wortkatze.learn.Card$$serializer       -> da:
ch.lkmc.wortkatze.learn.Deck$$serializer       -> bh:
ch.lkmc.wortkatze.learn.Tier$$serializer       -> wr0:
ch.lkmc.wortkatze.learn.UiText$$serializer     -> st0:
ch.lkmc.wortkatze.learn.Vocabulary$$serializer -> yv0:
```

All five survive R8. The specific failure mode the finding describes — stripped
field descriptors — is covered by the consumer rules kotlinx-serialization
1.11.0 ships and AGP applies automatically
(`META-INF/com.android.tools/proguard/kotlinx-serialization-common.pro`), whose
last rule is literally
`-keepclassmembers public class **$$serializer { private ** descriptor; }`.
No rule added; a redundant one would only look like insurance.

CI now runs `assembleRelease` on every PR, so this stays proven rather than
being a claim about one afternoon.

**„Verify the pinned action SHAs and future-looking tags resolve."**
Fair thing to ask; all seven check out against their tags via
`git ls-remote`: `actions/checkout@v7.0.1`, `actions/setup-java@v5.7.0`,
`actions/upload-artifact@v7.0.1`, `actions/download-artifact@v8.0.1`,
`softprops/action-gh-release@v3.0.2`, `gradle/actions@v5.0.2`,
`L-K-M/zai-code-review@v0.0.9`. No change needed.

### Round 2 — re-raised after being declined

`ß`/Swiss spelling, the debug-keystore "hardcoded passwords", and the KSP ↔
Kotlin version pairing all came back unchanged, having been answered above with
evidence. Per [CLAUDE.md](CLAUDE.md) a re-raise of a recorded decline is a
steady-state signal, not a new finding.

### Round 2 — declined

**„`gross` is taught twice with different translations (大 big / 高 tall)."**
Both cards are correct German — *er ist gross* is "he is tall" — so this is not
a data error, and `tools/vocab.py` is byte-identical to the Pebble app's copy
by decision 0001 anyway. The finding did surface a real *downstream* risk the
plan had not covered, though: a Quiz question could offer both translations and
mark a correct one wrong. Fixed where it belongs, in
`docs/plan/15-mode-quiz.md` — distractors now exclude any card sharing the
answer card's German, with the „gross" pair named as the test fixture.

**„`*.svg binary` in `.gitattributes` prevents useful diffs."**
True in general; there are no SVGs in this repository. If hand-edited SVG ever
lands, revisit then.

**„Confirm the Pebble save-slot ordering is safe."**
About the sibling app, not this one. Progress here is keyed by
`deckKey/German`, which is exactly why reordering is free — stated in
`docs/decisions/0001` and `docs/plan/03-vocabulary.md`.

### Noted — no action

**„`Question` cannot colour a noun's article without string parsing."**
It can: `Question` carries the whole `Card`, so a face reads `card.gender` and
`card.withoutArticle`. The plan now says so explicitly in
`docs/plan/12-mode-karten.md` rather than leaving an implementer to work it out.
