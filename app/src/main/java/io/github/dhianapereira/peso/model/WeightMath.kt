package io.github.dhianapereira.peso.model

import kotlin.math.abs

object WeightMath {
    fun difference(currentGrams: Int?, firstGrams: Int?): Int? = if (currentGrams == null || firstGrams == null) null else currentGrams - firstGrams

    fun remaining(currentGrams: Int?, goalGrams: Int?): Int? = if (currentGrams == null || goalGrams == null) null else abs(currentGrams - goalGrams)
}
