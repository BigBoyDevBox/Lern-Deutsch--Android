package ch.lkmc.wortkatze.learn

import java.time.LocalDate

/**
 * The daily streak — finish at least one round a day and the flame grows.
 *
 * Ported from the Pebble app, with its calendar arithmetic replaced by
 * `java.time.LocalDate` (JDK, not Android — this file stays unit-testable).
 * The caller decides what "today" is, which is the whole reason the tests can
 * walk a year forward in a millisecond.
 *
 * Deliberate kindnesses, both of which exist because the alternative punishes
 * a 14-year-old for something that isn't her fault:
 *
 *  * A second round on the same day does not extend the streak, but never
 *    resets it either.
 *  * A date *before* [lastDay] — a phone whose clock was wrong, or a flight
 *    west across the date line — leaves the streak alone instead of breaking
 *    it.
 */
data class Streak(
    val days: Int = 0,
    val lastDay: LocalDate? = null,
) {
    init {
        require(days >= 0) { "days must not be negative" }
    }

    /** Record that a round finished on [today] and return the updated streak. */
    fun bumped(today: LocalDate): Streak = when {
        lastDay == null -> Streak(1, today)
        today == lastDay -> this
        today == lastDay.plusDays(1) -> Streak(days + 1, today)
        today.isBefore(lastDay) -> this
        else -> Streak(1, today)
    }

    /**
     * What the flame should show on [today]. A streak stays lit through the
     * day after its last round — you have until bedtime tomorrow, not until
     * midnight tonight — and reads 0 once that grace day passes.
     */
    fun displayDays(today: LocalDate): Int = when {
        lastDay == null -> 0
        today == lastDay || today == lastDay.plusDays(1) -> days
        today.isBefore(lastDay) -> days
        else -> 0
    }

    /** True while today's round has not been finished yet. */
    fun needsRoundOn(today: LocalDate): Boolean = lastDay != today
}
