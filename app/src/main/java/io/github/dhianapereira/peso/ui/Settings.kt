package io.github.dhianapereira.peso.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import io.github.dhianapereira.peso.R
import io.github.dhianapereira.peso.model.AppTheme
import java.time.LocalDate

private enum class SettingsPage { MAIN, PREFERENCES, DATA, LEGAL, ABOUT }

@Composable
fun SettingsRoute(
    theme: AppTheme,
    onTheme: (AppTheme) -> Unit,
    onExport: (String) -> Unit,
    onImport: (String) -> Unit
) {
    var page by rememberSaveable { mutableStateOf(SettingsPage.MAIN) }
    var pendingImport by rememberSaveable { mutableStateOf<String?>(null) }
    val uriHandler = LocalUriHandler.current
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
            it?.toString()?.let(onExport)
        }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        pendingImport =
            it?.toString()
    }
    BackHandler(enabled = page != SettingsPage.MAIN) { page = SettingsPage.MAIN }
    when (page) {
        SettingsPage.MAIN -> SettingsPage(stringResource(R.string.settings)) {
            SettingsRow(
                Icons.Outlined.Tune,
                stringResource(R.string.preferences),
                stringResource(R.string.preferences_description)
            ) {
                page =
                    SettingsPage.PREFERENCES
            }
            SettingsRow(
                Icons.Outlined.Folder,
                stringResource(R.string.data_backup),
                stringResource(R.string.data_backup_description)
            ) {
                page =
                    SettingsPage.DATA
            }
            SettingsRow(
                Icons.Outlined.Gavel,
                stringResource(R.string.legal),
                stringResource(R.string.legal_description)
            ) {
                page =
                    SettingsPage.LEGAL
            }
            SettingsRow(
                Icons.Outlined.Info,
                stringResource(R.string.about),
                stringResource(R.string.about_description)
            ) {
                page =
                    SettingsPage.ABOUT
            }
        }
        SettingsPage.PREFERENCES -> PreferencesPage(theme, { page = SettingsPage.MAIN }, onTheme)
        SettingsPage.DATA -> SettingsPage(stringResource(R.string.data_backup), { page = SettingsPage.MAIN }) {
            SettingsRow(
                Icons.Outlined.Upload,
                stringResource(R.string.export_data),
                stringResource(R.string.export_description)
            ) {
                exportLauncher.launch("peso-backup-${LocalDate.now()}.json")
            }
            SettingsRow(
                Icons.Outlined.Download,
                stringResource(R.string.import_data),
                stringResource(R.string.import_description)
            ) {
                importLauncher.launch(arrayOf("application/json", "text/json", "text/plain"))
            }
        }
        SettingsPage.LEGAL -> SettingsPage(stringResource(R.string.legal), { page = SettingsPage.MAIN }) {
            SettingsRow(Icons.Outlined.Description, stringResource(R.string.privacy_policy)) {
                uriHandler.openUri("https://dhianapereira.github.io/apps/peso/legal/politica-de-privacidade/")
            }
            SettingsRow(Icons.Outlined.Description, stringResource(R.string.terms_of_use)) {
                uriHandler.openUri("https://dhianapereira.github.io/apps/peso/legal/termos-de-uso/")
            }
        }
        SettingsPage.ABOUT -> AboutPage { page = SettingsPage.MAIN }
    }
    pendingImport?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text(stringResource(R.string.confirm_import)) },
            text = { Text(stringResource(R.string.confirm_import_description)) },
            confirmButton = {
                TextButton({
                    pendingImport = null
                    onImport(uri)
                }) { Text(stringResource(R.string.import_data)) }
            },
            dismissButton = { TextButton({ pendingImport = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@Composable private fun PreferencesPage(theme: AppTheme, onBack: () -> Unit, onTheme: (AppTheme) -> Unit) {
    val currentLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    SettingsPage(stringResource(R.string.preferences), onBack) {
        Text(
            stringResource(R.string.appearance),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        AppTheme.entries.forEach { option ->
            val label = when (option) {
                AppTheme.SYSTEM -> R.string.theme_system
                AppTheme.LIGHT -> R.string.theme_light
                AppTheme.DARK -> R.string.theme_dark
            }
            SelectionRow(stringResource(label), theme == option) { onTheme(option) }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            stringResource(R.string.language),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        SelectionRow(
            stringResource(R.string.language_portuguese),
            currentLanguage.startsWith("pt") || currentLanguage.isBlank()
        ) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("pt-BR"))
        }
        SelectionRow(stringResource(R.string.language_english), currentLanguage.startsWith("en")) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
    }
}

@Composable private fun SelectionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected, onClick)
        Text(label, Modifier.padding(start = 8.dp))
    }
}

@Composable private fun AboutPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember { context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty() }
    SettingsPage(stringResource(R.string.about), onBack) {
        Text(stringResource(R.string.about_app), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        SettingsRow(
            Icons.Outlined.Person,
            stringResource(R.string.developed_by),
            stringResource(R.string.developer_name)
        )
        SettingsRow(Icons.Outlined.Info, stringResource(R.string.version), version)
    }
}

@Composable private fun SettingsPage(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().widthIn(max = 680.dp).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack !=
                null
            ) {
                IconButton(onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.back)) }
            }
            Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
        content()
        Spacer(Modifier.height(100.dp))
    }
}

@Composable private fun SettingsRow(icon: ImageVector, title: String, value: String? = null, onClick: (() -> Unit)? = null) {
    Card(
        Modifier.fillMaxWidth().padding(bottom = 10.dp).then(
            if (onClick !=
                null
            ) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (value !=
                    null
                ) {
                    Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (onClick != null) Icon(Icons.Outlined.ChevronRight, null)
        }
    }
}
