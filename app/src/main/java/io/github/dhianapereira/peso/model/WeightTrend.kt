package io.github.dhianapereira.peso.model

import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

data class MonthlyWeightPoint(val month: YearMonth, val averageGrams: Int)

object WeightTrend {
    fun monthlyAverages(entries: List<WeightEntryEntity>): List<MonthlyWeightPoint> = entries
        .groupBy { YearMonth.from(LocalDate.ofEpochDay(it.dateEpochDay)) }
        .map { (month, values) -> MonthlyWeightPoint(month, values.map { it.weightGrams }.average().roundToInt()) }
        .sortedBy { it.month }
}
