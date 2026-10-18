package io.github.dhianapereira.peso.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.model.WeightFormatter
import io.github.dhianapereira.peso.model.WeightMath
import io.github.dhianapereira.peso.ui.components.ScreenColumn
import io.github.dhianapereira.peso.ui.entry.GoalSheet
import io.github.dhianapereira.peso.ui.overview.components.CurrentWeightCard
import io.github.dhianapereira.peso.ui.overview.components.GoalStatusCard
import io.github.dhianapereira.peso.ui.overview.components.MetricCard
import io.github.dhianapereira.peso.ui.overview.components.WeightChart
import io.github.dhianapereira.peso.ui.overview.components.trendIcon

@Composable
fun OverviewScreen(entries: List<WeightEntryEntity>, goal: Int?, onGoal: (Int?) -> Unit) {
    val latest = entries.lastOrNull()
    val difference = WeightMath.difference(latest?.weightGrams, entries.firstOrNull()?.weightGrams)
    var editGoal by rememberSaveable { mutableStateOf(false) }

    ScreenColumn(R.string.app_name) {
        Text(
            stringResource(R.string.app_tagline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        CurrentWeightCard(latest, difference)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                label = stringResource(R.string.since_first),
                value = difference?.let(WeightFormatter::signedKilograms) ?: "—",
                icon = trendIcon(difference),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = stringResource(R.string.goal),
                value = goal?.let(WeightFormatter::kilograms) ?: "—",
                icon = Icons.Outlined.TrackChanges,
                modifier = Modifier.weight(1f),
                onClick = { editGoal = true }
            )
        }
        Spacer(Modifier.height(28.dp))
        WeightChart(entries)
        latest?.let { current ->
            goal?.let { target ->
                Spacer(Modifier.height(12.dp))
                val remaining = WeightMath.remaining(current.weightGrams, target) ?: 0
                GoalStatusCard(
                    message = if (remaining == 0) {
                        stringResource(R.string.goal_reached)
                    } else {
                        stringResource(R.string.goal_remaining, WeightFormatter.number(remaining))
                    }
                )
            }
        }
    }
    if (editGoal) {
        GoalSheet(goal, { editGoal = false }) {
            onGoal(it)
            editGoal = false
        }
    }
}
