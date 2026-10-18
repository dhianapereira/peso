package io.github.dhianapereira.peso.model

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object WeightFormatter {
    fun parseGrams(value: String): Int? = value.trim().replace(',', '.').toBigDecimalOrNull()
        ?.takeIf { it >= MIN_KG && it <= MAX_KG }?.movePointRight(3)?.toInt()

    fun number(grams: Int, locale: Locale = Locale.getDefault()): String = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 2
    }.format(grams / 1000.0)

    fun kilograms(grams: Int, locale: Locale = Locale.getDefault()): String = "${number(grams, locale)} kg"

    fun signedKilograms(grams: Int, locale: Locale = Locale.getDefault()): String {
        val sign = when {
            grams > 0 -> "+"
            grams < 0 -> "−"
            else -> ""
        }
        return "$sign${number(abs(grams), locale)} kg"
    }

    private val MIN_KG = 1.toBigDecimal()
    private val MAX_KG = 500.toBigDecimal()
}
