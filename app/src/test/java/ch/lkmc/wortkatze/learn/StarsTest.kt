package ch.lkmc.wortkatze.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StarsTest {

    @Test
    fun `a perfect round is three stars`() {
        assertEquals(3, Stars.forRound(firstTryCorrect = 20, total = 20))
    }

    @Test
    fun `ninety percent is still three stars`() {
        assertEquals(3, Stars.forRound(firstTryCorrect = 18, total = 20))
    }

    @Test
    fun `just under ninety drops to two`() {
        assertEquals(2, Stars.forRound(firstTryCorrect = 17, total = 20))
    }

    @Test
    fun `sixty percent is two stars`() {
        assertEquals(2, Stars.forRound(firstTryCorrect = 12, total = 20))
    }

    @Test
    fun `just under sixty drops to one`() {
        assertEquals(1, Stars.forRound(firstTryCorrect = 11, total = 20))
    }

    @Test
    fun `finishing always earns a star`() {
        assertEquals(1, Stars.forRound(firstTryCorrect = 0, total = 20))
    }

    @Test
    fun `an empty round cannot be graded down`() {
        assertEquals(3, Stars.forRound(firstTryCorrect = 0, total = 0))
    }

    @Test
    fun `integer division does not round up into a star`() {
        // 8/9 = 88.8% — floor keeps it at two stars, matching the Pebble app.
        assertEquals(2, Stars.forRound(firstTryCorrect = 8, total = 9))
    }

    @Test
    fun `impossible inputs are rejected rather than scored`() {
        assertFailsWith<IllegalArgumentException> { Stars.forRound(5, 4) }
        assertFailsWith<IllegalArgumentException> { Stars.forRound(-1, 4) }
        assertFailsWith<IllegalArgumentException> { Stars.forRound(0, -1) }
    }
}
