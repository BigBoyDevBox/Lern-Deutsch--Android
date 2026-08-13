package ch.lkmc.wortkatze.learn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The vocabulary, exactly as `tools/gen_vocab.py` writes it into
 * `assets/vocab.json`. These types are the domain model as well as the wire
 * format — one shape, no mapping layer to keep in step.
 *
 * Everything in the `learn` package is **pure Kotlin**: no Android imports, no
 * Context, no clocks or randomness that isn't passed in. That is what lets the
 * whole learning core be covered by fast JVM unit tests
 * (`./gradlew testDebugUnitTest`). Loading the asset is the one Android-shaped
 * job, and it lives in `data/VocabLoader`.
 */
@Serializable
data class Vocabulary(
    val schema: Int,
    val source: String = "",
    val tiers: List<Tier>,
    val decks: List<Deck>,
    val ui: UiText,
) {
    /** Every card in every deck, source order. */
    val allCards: List<Card> get() = decks.flatMap { it.cards }

    fun deck(key: String): Deck? = decks.firstOrNull { it.key == key }

    fun decksIn(tierId: String): List<Deck> = decks.filter { it.tier == tierId }

    companion object {
        /** Bumped whenever the JSON shape changes incompatibly. */
        const val SCHEMA = 1
    }
}

@Serializable
data class Tier(
    val id: String,
    val de: String,
    val zh: String,
    val en: String,
    val accent: String,
)

@Serializable
data class Deck(
    val key: String,
    val tier: String,
    val de: String,
    val zh: String,
    val en: String,
    val accent: String,
    /** A single emoji — deck art with no drawables to maintain. */
    val icon: String,
    val cards: List<Card>,
)

/**
 * One vocabulary item. [id] is `"<deckKey>/<german>"` and is the key every
 * piece of saved progress hangs off, so it must stay stable: reordering decks
 * or inserting cards is free, and only rewriting the German retires a card's
 * history — which is correct, because that is a different word.
 */
@Serializable
data class Card(
    val id: String,
    val de: String,
    val zh: String,
    val en: String,
    val gender: Gender,
    val pos: Wortart,
) {
    /** „der Hund" → „Hund". The article is the answer in Artikel mode, so the
     *  prompt must not contain it. Non-nouns are returned unchanged. */
    val withoutArticle: String
        get() = if (gender == Gender.NONE) de else de.substringAfter(' ', de)
}

/** Grammatical gender, named after the article the learner has to produce. */
@Serializable
enum class Gender {
    @SerialName("none") NONE,
    @SerialName("der") DER,
    @SerialName("die") DIE,
    @SerialName("das") DAS;

    /** The three that Artikel mode offers, in the order German learners chant. */
    companion object {
        val ARTICLES = listOf(DER, DIE, DAS)
    }
}

/**
 * Word class. `OTHER` is an honest answer, not a gap: pronouns, numerals,
 * particles and whole phrases genuinely are none of the three, and Wortarten
 * mode simply never draws from that bucket. See `tools/vocab_pos.py` for how
 * each card gets its label.
 */
@Serializable
enum class Wortart {
    @SerialName("noun") NOUN,
    @SerialName("verb") VERB,
    @SerialName("adjective") ADJECTIVE,
    @SerialName("other") OTHER;

    companion object {
        /** The three Wortarten mode plays with. */
        val PLAYABLE = listOf(NOUN, VERB, ADJECTIVE)
    }
}

/** Chinese praise and nudge lines, shared with the Pebble app's word list. */
@Serializable
data class UiText(
    val praise: List<String>,
    val nudge: List<String>,
)
