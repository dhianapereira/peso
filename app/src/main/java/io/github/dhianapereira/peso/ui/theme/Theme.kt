package io.github.dhianapereira.peso.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF216A4A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC0EFD1),
    onPrimaryContainer = Color(0xFF082116),
    secondary = Color(0xFF4E6355),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E8D7),
    onSecondaryContainer = Color(0xFF0B1F13),
    tertiary = Color(0xFF3D6470),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC1E9F7),
    onTertiaryContainer = Color(0xFF001F27),
    background = Color(0xFFF8FAF7),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFF8FAF7),
    onSurface = Color(0xFF191C1A),
    surfaceDim = Color(0xFFD8DBD7),
    surfaceBright = Color(0xFFF8FAF7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F5F1),
    surfaceContainer = Color(0xFFECEFEB),
    surfaceContainerHigh = Color(0xFFE6E9E5),
    surfaceContainerHighest = Color(0xFFE0E4DF),
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = Color(0xFF404943),
    inverseSurface = Color(0xFF2E312F),
    inverseOnSurface = Color(0xFFEFF1ED),
    inversePrimary = Color(0xFFA4D5B5),
    outline = Color(0xFF707971),
    outlineVariant = Color(0xFFC0C9C1)
)
private val Dark = darkColorScheme(
    primary = Color(0xFFA4D5B5),
    onPrimary = Color(0xFF0D3823),
    primaryContainer = Color(0xFF174F3A),
    onPrimaryContainer = Color(0xFFC0EFD1),
    secondary = Color(0xFFB5CCBB),
    onSecondary = Color(0xFF203529),
    secondaryContainer = Color(0xFF354B3B),
    onSecondaryContainer = Color(0xFFD1E8D7),
    tertiary = Color(0xFFA5CDDA),
    onTertiary = Color(0xFF073640),
    tertiaryContainer = Color(0xFF244D58),
    onTertiaryContainer = Color(0xFFC1E9F7),
    background = Color(0xFF101512),
    onBackground = Color(0xFFE0E4DF),
    surface = Color(0xFF101512),
    onSurface = Color(0xFFE0E4DF),
    surfaceDim = Color(0xFF101512),
    surfaceBright = Color(0xFF363B37),
    surfaceContainerLowest = Color(0xFF0B100D),
    surfaceContainerLow = Color(0xFF151A17),
    surfaceContainer = Color(0xFF191E1B),
    surfaceContainerHigh = Color(0xFF232825),
    surfaceContainerHighest = Color(0xFF2E332F),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C1),
    inverseSurface = Color(0xFFE0E4DF),
    inverseOnSurface = Color(0xFF2E312F),
    inversePrimary = Color(0xFF216A4A),
    outline = Color(0xFF89938B),
    outlineVariant = Color(0xFF404943)
)

@Composable
fun PesoTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, content = content)
}
