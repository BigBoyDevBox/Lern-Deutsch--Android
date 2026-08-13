package ch.lkmc.wortkatze.ui.theme

import ch.lkmc.wortkatze.learn.VocabParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The generator writes accent *tokens*; the palette turns them into colours.
 * Nothing in the type system connects the two, so this test does: every token
 * the shipped vocabulary uses must resolve, and every token the palette
 * defines must be used. Without it a renamed accent degrades silently to the
 * fallback pink and every deck looks the same.
 */
class CandyPaletteTest {

    private val vocabulary = VocabParser.parse(
        checkNotNull(javaClass.getResourceAsStream("/vocab.json")) {
            "vocab.json is not on the test classpath — check the test sourceSet in app/build.gradle.kts"
        }.bufferedReader().use { it.readText() }
    )

    private val used: Set<String>
        get() = (vocabulary.decks.map { it.accent } + vocabulary.tiers.map { it.accent }).toSet()

    @Test
    fun `every accent the vocabulary uses is defined`() {
        val missing = used - Candy.accentTokens
        assertTrue(missing.isEmpty(), "undefined accent tokens: $missing")
    }

    @Test
    fun `the palette carries no dead tokens`() {
        val unused = Candy.accentTokens - used
        assertTrue(unused.isEmpty(), "accent tokens nothing uses: $unused")
    }

    @Test
    fun `the three article colours are distinct`() {
        assertEquals(
            3,
            Candy.articleColors.toSet().size,
            "der/die/das must never share a colour",
        )
    }

    @Test
    fun `no article colour is reused as a surface colour`() {
        // The palette's first rule is that der/die/das mean gender and nothing
        // else. Brand pink and "die" pink were literally the same value once,
        // which would have put a pink article chip on a pink banner with zero
        // contrast the first time the two met.
        val clashes = Candy.articleColors.filter { it in Candy.surfaceColors }
        assertTrue(clashes.isEmpty(), "article colours reused as surfaces: $clashes")
    }
}
