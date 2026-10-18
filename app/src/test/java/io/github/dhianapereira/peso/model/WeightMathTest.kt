package io.github.dhianapereira.peso.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeightMathTest {
    @Test
    fun differenceUsesFirstAndCurrent() {
        assertEquals(-2500, WeightMath.difference(72_500, 75_000))
    }

    @Test
    fun differenceNeedsBothValues() {
        assertNull(WeightMath.difference(null, 75_000))
    }

    @Test
    fun remainingIsAbsolute() {
        assertEquals(2_000, WeightMath.remaining(72_000, 70_000))
        assertEquals(
            2_000,
            WeightMath.remaining(68_000, 70_000)
        )
    }
}
