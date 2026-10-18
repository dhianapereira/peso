package io.github.dhianapereira.peso.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.model.WeightFormatter
import io.github.dhianapereira.peso.ui.components.EmptyWeightState
import io.github.dhianapereira.peso.ui.components.ScreenColumn
import io.github.dhianapereira.peso.ui.components.ScreenTitle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<WeightEntryEntity>,
    onEdit: (WeightEntryEntity) -> Unit,
    onDelete: (WeightEntryEntity) -> Unit
) {
    var pendingDelete by remember { mutableStateOf<WeightEntryEntity?>(null) }
    if (entries.isEmpty()) {
        ScreenColumn(R.string.history) { EmptyWeightState() }
    } else {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { ScreenTitle(R.string.history) }
            val rows = entries.mapIndexed { index, entry ->
                HistoryRow(entry, entries.getOrNull(index - 1)?.let { entry.weightGrams - it.weightGrams })
            }.asReversed()
            items(rows, key = { it.entry.id }) { row ->
                val item = row.entry
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        when (value) {
                            SwipeToDismissBoxValue.StartToEnd -> pendingDelete = item
                            SwipeToDismissBoxValue.EndToStart -> onEdit(item)
                            SwipeToDismissBoxValue.Settled -> Unit
                        }
                        false
                    }
                )
                val editLabel = stringResource(R.string.edit_weight)
                val deleteLabel = stringResource(R.string.delete)
                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier.semantics {
                        customActions = listOf(
                            CustomAccessibilityAction(editLabel) {
                                onEdit(item)
                                true
                            },
                            CustomAccessibilityAction(deleteLabel) {
                                pendingDelete = item
                                true
                            }
                        )
                    },
                    backgroundContent = { SwipeActionBackground(dismissState.dismissDirection) },
                    content = { HistoryCard(row) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
    pendingDelete?.let { entry ->
        DeleteConfirmation(
            onDismiss = { pendingDelete = null },
            onConfirm = {
                onDelete(entry)
                pendingDelete = null
            }
        )
    }
}

@Composable
private fun HistoryCard(row: HistoryRow) {
    val item = row.entry
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    WeightFormatter.kilograms(item.weightGrams),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(formatDate(item.dateEpochDay), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stringResource(R.string.change),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    row.changeGrams?.let(WeightFormatter::signedKilograms)
                        ?: stringResource(R.string.first_record),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeActionBackground(direction: SwipeToDismissBoxValue) {
    val editing = direction == SwipeToDismissBoxValue.EndToStart
    val active = direction != SwipeToDismissBoxValue.Settled
    Row(
        Modifier.fillMaxSize().background(
            color = if (editing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(12.dp)
        ).padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (editing) Arrangement.End else Arrangement.Start
    ) {
        if (active) {
            Icon(
                if (editing) Icons.Outlined.Edit else Icons.Outlined.Delete,
                null,
                tint = if (editing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(if (editing) R.string.edit_weight else R.string.delete),
                color = if (editing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeleteConfirmation(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_title)) },
        text = { Text(stringResource(R.string.delete_description)) },
        confirmButton = { TextButton(onConfirm) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

private data class HistoryRow(val entry: WeightEntryEntity, val changeGrams: Int?)

private fun formatDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay)
    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
