package io.github.dhianapereira.peso.model

import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeightTrendTest {
    @Test
    fun monthlyAveragesGroupsMultipleWeighInsInTheSameMonth() {
        val result = WeightTrend.monthlyAverages(
            listOf(entry("2026-01-03", 70_000), entry("2026-01-20", 72_000))
        )

        assertEquals(listOf(MonthlyWeightPoint(YearMonth.of(2026, 1), 71_000)), result)
    }

    @Test
    fun monthlyAveragesOrdersSparseMonthsChronologically() {
        val result = WeightTrend.monthlyAverages(
            listOf(entry("2026-10-01", 68_000), entry("2026-02-01", 70_000))
        )

        assertEquals(listOf(YearMonth.of(2026, 2), YearMonth.of(2026, 10)), result.map { it.month })
    }

    @Test
    fun monthlyAveragesOfEmptyHistoryIsEmpty() {
        assertTrue(WeightTrend.monthlyAverages(emptyList()).isEmpty())
    }

    private fun entry(date: String, grams: Int) = WeightEntryEntity(
        dateEpochDay = LocalDate.parse(date).toEpochDay(),
        weightGrams = grams
    )
}
