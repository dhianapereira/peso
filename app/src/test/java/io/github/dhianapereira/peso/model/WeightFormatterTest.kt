package io.github.dhianapereira.peso.model

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeightFormatterTest {
    @Test fun parsesCommaDecimal() = assertEquals(72_550, WeightFormatter.parseGrams("72,55"))

    @Test fun parsesDotDecimal() = assertEquals(72_550, WeightFormatter.parseGrams("72.55"))

    @Test fun trimsInput() = assertEquals(80_000, WeightFormatter.parseGrams(" 80 "))

    @Test fun acceptsMinimum() = assertEquals(1_000, WeightFormatter.parseGrams("1"))

    @Test fun acceptsMaximum() = assertEquals(500_000, WeightFormatter.parseGrams("500"))

    @Test fun rejectsBelowMinimum() = assertNull(WeightFormatter.parseGrams("0.999"))

    @Test fun rejectsAboveMaximum() = assertNull(WeightFormatter.parseGrams("500.1"))

    @Test fun rejectsBlank() = assertNull(WeightFormatter.parseGrams(" "))

    @Test fun rejectsMalformedValue() = assertNull(WeightFormatter.parseGrams("72.5.5"))

    @Test fun formatsPortugueseNumber() = assertEquals("72,55", WeightFormatter.number(72_550, Locale.forLanguageTag("pt-BR")))

    @Test fun formatsEnglishKilograms() = assertEquals("72.5 kg", WeightFormatter.kilograms(72_500, Locale.US))

    @Test fun formatsPositiveDifference() = assertEquals("+2.5 kg", WeightFormatter.signedKilograms(2_500, Locale.US))

    @Test fun formatsNegativeDifference() = assertEquals("−2.5 kg", WeightFormatter.signedKilograms(-2_500, Locale.US))

    @Test fun formatsZeroWithoutSign() = assertEquals("0.0 kg", WeightFormatter.signedKilograms(0, Locale.US))
}
