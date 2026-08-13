package ch.lkmc.wortkatze.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * The candy palette. Every colour the app draws comes from here — no ad-hoc
 * `Color(0x…)` in a screen, ever (AGENTS.md).
 *
 * Two rules the palette exists to protect:
 *
 *  1. **The article colours are a learning aid, not decoration.** der = blue,
 *     die = pink, das = green, identical to the Pebble app, so the association
 *     a learner already built on the watch transfers to the phone. They are
 *     never reused for anything else.
 *  2. **Colour is never the only signal.** Every article chip also carries its
 *     word, every correct/wrong verdict also carries a shape — roughly 1 in 200
 *     girls has a colour-vision deficiency, and the app must still teach.
 */
object Candy {

    // --- Ground ------------------------------------------------------------
    val Cream = Color(0xFFFFF8F3)
    val Blush = Color(0xFFFFE7F0)
    val Lavender = Color(0xFFF1EAFF)
    val Ink = Color(0xFF3A2F4A)
    val InkSoft = Color(0xFF7A6E8C)
    val Card = Color(0xFFFFFFFF)
    val Shadow = Color(0x1A3A2F4A)

    // --- Brand -------------------------------------------------------------
    val Bubblegum = Color(0xFFFF5FA2)
    val BubblegumDeep = Color(0xFFE03C82)

    // --- Article colours (see rule 1 above) --------------------------------
    val Der = Color(0xFF3E9BFF)
    val Die = Color(0xFFFF5FA2)
    val Das = Color(0xFF26C6A6)

    // --- Verdicts ----------------------------------------------------------
    val Correct = Color(0xFF2ECC8F)
    val Wrong = Color(0xFFFF7A6B)
    val Gold = Color(0xFFFFC53D)
    val Flame = Color(0xFFFF8A3D)

    // --- Deck accents ------------------------------------------------------
    // Keys match the tokens `tools/gen_vocab.py` writes into vocab.json; a deck
    // whose accent is missing here falls back to Bubblegum rather than
    // crashing, and `CandyPaletteTest` fails if any shipped deck needs that
    // fallback.
    private val accents = mapOf(
        "rose" to Color(0xFFFF6FAE),
        "honey" to Color(0xFFFFB03A),
        "aqua" to Color(0xFF3ED0E0),
        "cherry" to Color(0xFFFF5C6C),
        "mint" to Color(0xFF4FD1A5),
        "clover" to Color(0xFF43BE6D),
        "orchid" to Color(0xFFC77DFF),
        "peach" to Color(0xFFFF9E7A),
        "tangerine" to Color(0xFFFF8A3D),
        "sky" to Color(0xFF5AA9FF),
        "grape" to Color(0xFF9B72E8),
        "apricot" to Color(0xFFFFC06B),
        "lime" to Color(0xFFA8D94A),
        "lemon" to Color(0xFFFFD84D),
    )

    /** The colour for a deck or tier accent token, or [Bubblegum] if unknown. */
    fun accent(token: String): Color = accents[token] ?: Bubblegum

    /** Every accent token this build knows — used by tests, not by screens. */
    val accentTokens: Set<String> get() = accents.keys
}
