package ch.lkmc.wortkatze.learn

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StreakTest {

    private val day = LocalDate.of(2026, 8, 13)

    @Test
    fun `the first round starts the streak at one`() {
        assertEquals(Streak(1, day), Streak().bumped(day))
    }

    @Test
    fun `a consecutive day extends it`() {
        val after = Streak().bumped(day).bumped(day.plusDays(1))
        assertEquals(2, after.days)
        assertEquals(day.plusDays(1), after.lastDay)
    }

    @Test
    fun `a second round on the same day changes nothing`() {
        val once = Streak().bumped(day)
        assertEquals(once, once.bumped(day))
    }

    @Test
    fun `a missed day resets to one`() {
        val after = Streak(9, day).bumped(day.plusDays(2))
        assertEquals(Streak(1, day.plusDays(2)), after)
    }

    @Test
    fun `a backwards clock never breaks the streak`() {
        val before = Streak(9, day)
        assertEquals(before, before.bumped(day.minusDays(3)))
    }

    @Test
    fun `it survives a month boundary`() {
        val end = LocalDate.of(2026, 1, 31)
        assertEquals(2, Streak(1, end).bumped(end.plusDays(1)).days)
    }

    @Test
    fun `it survives a leap day`() {
        val feb28 = LocalDate.of(2028, 2, 28)
        val chain = Streak(1, feb28)
            .bumped(LocalDate.of(2028, 2, 29))
            .bumped(LocalDate.of(2028, 3, 1))
        assertEquals(3, chain.days)
    }

    @Test
    fun `a hundred consecutive days count to a hundred`() {
        var streak = Streak()
        repeat(100) { streak = streak.bumped(day.plusDays(it.toLong())) }
        assertEquals(100, streak.days)
    }

    @Test
    fun `the flame stays lit through the day after`() {
        val streak = Streak(7, day)
        assertEquals(7, streak.displayDays(day))
        assertEquals(7, streak.displayDays(day.plusDays(1)))
        assertEquals(0, streak.displayDays(day.plusDays(2)))
    }

    @Test
    fun `an untouched streak shows nothing`() {
        assertEquals(0, Streak().displayDays(day))
    }

    @Test
    fun `today's round is needed until it happens`() {
        assertTrue(Streak(3, day.minusDays(1)).needsRoundOn(day))
        assertFalse(Streak(3, day).needsRoundOn(day))
    }

    @Test
    fun `a negative streak is not representable`() {
        kotlin.test.assertFailsWith<IllegalArgumentException> { Streak(-1, day) }
    }
}
