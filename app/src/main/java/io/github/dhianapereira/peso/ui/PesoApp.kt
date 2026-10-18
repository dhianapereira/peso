package io.github.dhianapereira.peso.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WaterfallChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.data.weight.WeightEntryEntity
import io.github.dhianapereira.peso.ui.entry.WeightEditorSheet
import io.github.dhianapereira.peso.ui.history.HistoryScreen
import io.github.dhianapereira.peso.ui.overview.OverviewScreen

private enum class Tab { OVERVIEW, HISTORY, SETTINGS }

@Composable
fun PesoApp(viewModel: MainViewModel) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val goal by viewModel.goalGrams.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(Tab.OVERVIEW) }
    var editing by remember { mutableStateOf<WeightEntryEntity?>(null) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    val snackbars = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val message = when (event) {
                AppEvent.Saved -> context.getString(R.string.saved)
                AppEvent.Deleted -> context.getString(R.string.deleted)
                is AppEvent.Backup -> context.getString(
                    if (!event.success) {
                        R.string.backup_error
                    } else if (event.importing) {
                        R.string.import_success
                    } else {
                        R.string.export_success
                    }
                )
            }
            snackbars.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbars) },
        bottomBar = {
            NavigationBar {
                NavItem(Tab.OVERVIEW, tab, Icons.Outlined.Home, R.string.overview) { tab = it }
                NavItem(Tab.HISTORY, tab, Icons.Outlined.WaterfallChart, R.string.history) { tab = it }
                NavItem(Tab.SETTINGS, tab, Icons.Outlined.Settings, R.string.settings) { tab = it }
            }
        },
        floatingActionButton = {
            if (tab != Tab.SETTINGS) {
                FloatingActionButton(onClick = {
                    editing = null
                    showEditor = true
                }) {
                    Icon(Icons.Outlined.Add, stringResource(R.string.add_weight))
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.OVERVIEW -> OverviewScreen(entries, goal, viewModel::setGoal)
                Tab.HISTORY -> HistoryScreen(
                    entries = entries,
                    onEdit = {
                        editing = it
                        showEditor = true
                    },
                    onDelete = viewModel::delete
                )
                Tab.SETTINGS -> SettingsRoute(theme, viewModel::setTheme, viewModel::export, viewModel::import)
            }
        }
    }

    if (showEditor) {
        WeightEditorSheet(
            entry = editing,
            onDismiss = { showEditor = false },
            onSave = { timestamp, grams ->
                viewModel.save(editing?.id, timestamp, grams)
                showEditor = false
            }
        )
    }
}

@Composable
private fun RowScope.NavItem(tab: Tab, selected: Tab, icon: ImageVector, label: Int, onSelect: (Tab) -> Unit) {
    val color = if (tab == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        Modifier.weight(1f).clickable { onSelect(tab) }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = if (tab == selected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent,
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(Modifier.width(64.dp).height(32.dp), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
        }
        Text(stringResource(label), color = color, style = MaterialTheme.typography.labelMedium)
    }
}
