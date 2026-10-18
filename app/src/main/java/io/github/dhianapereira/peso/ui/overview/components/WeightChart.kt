package io.github.dhianapereira.peso.ui.overview.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.model.MonthlyWeightPoint
import io.github.dhianapereira.peso.model.WeightFormatter
import io.github.dhianapereira.peso.model.WeightTrend
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
internal fun WeightChart(entries: List<WeightEntryEntity>) {
    val points = WeightTrend.monthlyAverages(entries)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(stringResource(R.string.evolution), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            ChartSummary(points)
            Spacer(Modifier.height(18.dp))
            ChartCanvas(points)
            ChartLabels(points)
        }
    }
}

@Composable
private fun ChartSummary(points: List<MonthlyWeightPoint>) {
    if (points.size < 2) {
        Text(
            stringResource(R.string.not_enough_data),
            Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        val min = points.minOf { it.averageGrams }
        val max = points.maxOf { it.averageGrams }
        Text(
            stringResource(R.string.weight_range, WeightFormatter.kilograms(min), WeightFormatter.kilograms(max)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChartLabels(points: List<MonthlyWeightPoint>) {
    if (points.isEmpty()) return
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            formatMonth(points.first().month),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (points.size > 1) {
            Text(
                formatMonth(points.last().month),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartCanvas(points: List<MonthlyWeightPoint>) {
    val description = stringResource(R.string.chart_description)
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val min = points.minOfOrNull { it.averageGrams }?.toFloat() ?: 0f
    val span = ((points.maxOfOrNull { it.averageGrams } ?: 0) - min).coerceAtLeast(100f)
    Box(Modifier.fillMaxWidth().height(180.dp)) {
        Canvas(Modifier.matchParentSize().semantics { contentDescription = description }) {
            repeat(4) { index ->
                val y = index * size.height / 3
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            }
            if (points.size == 1) {
                drawCircle(lineColor, 4.dp.toPx(), center)
            } else if (points.size > 1) {
                val path = Path()
                points.forEachIndexed { index, point ->
                    val x = index * size.width / (points.size - 1)
                    val y = size.height - ((point.averageGrams - min) / span * size.height)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, lineColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                points.forEachIndexed { index, point ->
                    val x = index * size.width / (points.size - 1)
                    val y = size.height - ((point.averageGrams - min) / span * size.height)
                    drawCircle(lineColor, 4.dp.toPx(), Offset(x, y))
                }
            }
        }
    }
}

private fun formatMonth(month: YearMonth): String = month.format(DateTimeFormatter.ofPattern("MMM yyyy"))
