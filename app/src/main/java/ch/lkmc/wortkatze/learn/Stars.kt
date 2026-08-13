package ch.lkmc.wortkatze.learn

/**
 * How many stars a finished round is worth.
 *
 * Ported unchanged from the Pebble app (`src/c/study.c::compute_stars`) so a
 * learner who used the watch keeps the same mental model: stars come from
 * cards you got right the **first** time, and finishing at all always earns
 * at least one. Missing a card and then getting it on the retry still
 * completes the round — it just doesn't buy a star.
 */
object Stars {

    const val MAX = 3

    /** Thresholds in percent of first-try-correct cards. */
    private const val THREE_STAR_PERCENT = 90
    private const val TWO_STAR_PERCENT = 60

    fun forRound(firstTryCorrect: Int, total: Int): Int {
        require(firstTryCorrect >= 0) { "firstTryCorrect must not be negative" }
        require(total >= 0) { "total must not be negative" }
        require(firstTryCorrect <= total) { "firstTryCorrect ($firstTryCorrect) exceeds total ($total)" }
        // An empty round cannot be graded; award full marks rather than
        // punishing the learner for a data problem that isn't theirs.
        if (total == 0) return MAX
        val percent = firstTryCorrect * 100 / total
        return when {
            percent >= THREE_STAR_PERCENT -> 3
            percent >= TWO_STAR_PERCENT -> 2
            else -> 1
        }
    }
}
