package ch.lkmc.wortkatze.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Parses the REAL `assets/vocab.json` the APK ships — not a fixture.
 *
 * `app/build.gradle.kts` puts `src/main/assets` on the unit-test classpath
 * precisely so this test can, which means a bad regeneration fails
 * `./gradlew testDebugUnitTest` on a laptop in two seconds rather than on a
 * phone in front of the learner.
 *
 * Anything checkable about the word list itself lives in
 * `tools/validate_vocab.py` (it runs before generation and in CI). What is
 * checked HERE is only what the Kotlin side depends on: that the file
 * deserializes, that the model's derived properties hold, and that the three
 * game modes each have a pool to draw from.
 */
class VocabAssetTest {

    private val vocabulary: Vocabulary = VocabParser.parse(
        checkNotNull(javaClass.getResourceAsStream("/vocab.json")) {
            "vocab.json is not on the test classpath — check the test sourceSet in app/build.gradle.kts"
        }.bufferedReader().use { it.readText() }
    )

    @Test
    fun `parses the shipped asset`() {
        assertEquals(Vocabulary.SCHEMA, vocabulary.schema)
        assertEquals(4, vocabulary.tiers.size)
        // Exact, like the card count: a tolerance here would let a whole deck
        // vanish unnoticed. Both numbers are meant to change only when someone
        // deliberately edits tools/vocab.py, and then this test is the reminder
        // to update the docs that quote them.
        assertEquals(81, vocabulary.decks.size)
        assertEquals(1204, vocabulary.allCards.size)
    }

    @Test
    fun `every deck belongs to a declared tier and has cards`() {
        val tierIds = vocabulary.tiers.map { it.id }.toSet()
        vocabulary.decks.forEach { deck ->
            assertTrue(deck.tier in tierIds, "deck ${deck.key} has unknown tier ${deck.tier}")
            assertTrue(deck.cards.isNotEmpty(), "deck ${deck.key} is empty")
            assertTrue(deck.icon.isNotBlank(), "deck ${deck.key} has no icon")
        }
        assertEquals(
            vocabulary.decks.size,
            vocabulary.tiers.sumOf { vocabulary.decksIn(it.id).size },
            "every deck should appear under exactly one tier",
        )
    }

    @Test
    fun `card ids are unique — they key all saved progress`() {
        val ids = vocabulary.allCards.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate card id would merge two words' progress")
    }

    @Test
    fun `gender and article agree`() {
        vocabulary.allCards.forEach { card ->
            val expected = when (card.gender) {
                Gender.DER -> "der"
                Gender.DIE -> "die"
                Gender.DAS -> "das"
                Gender.NONE -> null
            }
            if (expected != null) {
                assertTrue(
                    card.de.startsWith("$expected "),
                    "${card.id}: gender ${card.gender} but German is '${card.de}'",
                )
            }
        }
    }

    @Test
    fun `withoutArticle strips exactly the article`() {
        // A literal expectation, not one derived from substringAfter — deriving
        // it would only prove withoutArticle agrees with whatever it is
        // implemented as.
        val mann = vocabulary.allCards.first { it.id == "people/der Mann" }
        assertEquals("Mann", mann.withoutArticle)

        val other = vocabulary.allCards.first { it.gender == Gender.NONE }
        assertEquals(other.de, other.withoutArticle, "non-nouns must pass through untouched")

        vocabulary.allCards.filter { it.gender != Gender.NONE }.forEach {
            assertTrue(it.withoutArticle.isNotBlank(), "${it.id} lost its whole word")
            assertTrue(
                !it.withoutArticle.startsWith("der ") &&
                    !it.withoutArticle.startsWith("die ") &&
                    !it.withoutArticle.startsWith("das "),
                "${it.id} still starts with an article",
            )
        }
    }

    @Test
    fun `every game mode has a pool`() {
        // Artikel mode draws from the three genders.
        Gender.ARTICLES.forEach { gender ->
            val n = vocabulary.allCards.count { it.gender == gender }
            assertTrue(n >= 100, "only $n cards for $gender — Artikel rounds would repeat")
        }
        // Wortarten mode draws from the three playable word classes.
        Wortart.PLAYABLE.forEach { pos ->
            val n = vocabulary.allCards.count { it.pos == pos }
            assertTrue(n >= 30, "only $n cards with pos $pos — Wortarten rounds would repeat")
        }
    }

    @Test
    fun `a noun is exactly a card with a gender`() {
        vocabulary.allCards.forEach { card ->
            if (card.gender != Gender.NONE) {
                assertEquals(Wortart.NOUN, card.pos, "${card.id} has an article but isn't a noun")
            }
        }
    }

    @Test
    fun `no card has an empty side`() {
        vocabulary.allCards.forEach { card ->
            assertTrue(card.de.isNotBlank(), "${card.id}: empty German")
            assertTrue(card.zh.isNotBlank(), "${card.id}: empty Chinese")
            assertTrue(card.en.isNotBlank(), "${card.id}: empty English")
        }
    }

    @Test
    fun `swiss spelling — never sharp s`() {
        val offenders = vocabulary.allCards.filter { "ß" in it.de }
        assertTrue(offenders.isEmpty(), "these cards use ß: ${offenders.map { it.id }}")
    }

    @Test
    fun `praise and nudge lines exist`() {
        assertTrue(vocabulary.ui.praise.isNotEmpty())
        assertTrue(vocabulary.ui.nudge.isNotEmpty())
    }

    @Test
    fun `deck lookup works`() {
        val first = vocabulary.decks.first()
        assertNotNull(vocabulary.deck(first.key))
        assertEquals(null, vocabulary.deck("no-such-deck"))
    }
}
