package io.github.dhianapereira.peso.ui.entry

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.model.WeightFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightEditorSheet(
    entry: WeightEntryEntity?,
    onDismiss: () -> Unit,
    onSave: (Long, Int) -> Unit
) {
    val context = LocalContext.current
    var date by remember(entry) {
        mutableStateOf(entry?.let { LocalDate.ofEpochDay(it.dateEpochDay) } ?: LocalDate.now())
    }
    var weight by remember(entry) { mutableStateOf(entry?.let { WeightFormatter.number(it.weightGrams) } ?: "") }
    var error by remember { mutableStateOf(false) }
    var pendingEdit by remember { mutableStateOf<Pair<Long, Int>?>(null) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxWidth().imePadding().navigationBarsPadding().padding(
                horizontal = 24.dp
            ).verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(
                    if (entry ==
                        null
                    ) {
                        R.string.add_weight
                    } else {
                        R.string.edit_weight
                    }
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = sanitizeWeightInput(it)
                    error = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.weight_kg)) },
                isError = error,
                supportingText = { if (error) Text(stringResource(R.string.invalid_weight)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showDatePicker(context, date) { date = it } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val grams = WeightFormatter.parseGrams(weight)
                    if (grams ==
                        null
                    ) {
                        error = true
                    } else if (entry != null) {
                        pendingEdit = date.toEpochDay() to grams
                    } else {
                        onSave(date.toEpochDay(), grams)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
            Spacer(Modifier.height(16.dp))
        }
    }
    pendingEdit?.let { edit ->
        EditConfirmation(
            onDismiss = { pendingEdit = null },
            onConfirm = { onSave(edit.first, edit.second) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSheet(goal: Int?, onDismiss: () -> Unit, onSave: (Int?) -> Unit) {
    var value by remember { mutableStateOf(goal?.let { WeightFormatter.number(it) } ?: "") }
    var error by remember { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().imePadding().navigationBarsPadding().padding(24.dp)) {
            Text(
                stringResource(R.string.set_goal),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value,
                {
                    value = sanitizeWeightInput(it)
                    error = false
                },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.goal_optional)) },
                isError = error,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Button({
                if (value.isBlank()) {
                    onSave(null)
                } else {
                    WeightFormatter.parseGrams(value)?.let(onSave)
                        ?: run { error = true }
                }
            }, Modifier.fillMaxWidth()) { Text(stringResource(R.string.save)) }
            if (goal !=
                null
            ) {
                TextButton({ onSave(null) }, Modifier.fillMaxWidth()) { Text(stringResource(R.string.remove_goal)) }
            }
        }
    }
}

internal fun sanitizeWeightInput(value: String): String = value.filter { it.isDigit() || it == ',' || it == '.' }

@Composable
private fun EditConfirmation(onDismiss: () -> Unit, onConfirm: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.edit_confirm_title)) },
    text = { Text(stringResource(R.string.edit_confirm_description)) },
    confirmButton = { TextButton(onConfirm) { Text(stringResource(R.string.confirm_edit)) } },
    dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.cancel)) } }
)

private fun showDatePicker(context: Context, current: LocalDate, onChange: (LocalDate) -> Unit) {
    DatePickerDialog(
        context,
        { _, y, m, d ->
            onChange(LocalDate.of(y, m + 1, d))
        },
        current.year,
        current.monthValue - 1,
        current.dayOfMonth
    ).show()
}
