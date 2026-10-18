package io.github.dhianapereira.peso.ui.entry

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightInputTest {
    @Test fun keepsDigitsAndDecimalSeparators() {
        assertEquals("72,50", sanitizeWeightInput("a72,50 kg"))
    }

    @Test fun emptyInputRemainsEmpty() {
        assertEquals("", sanitizeWeightInput("abc"))
    }
}
