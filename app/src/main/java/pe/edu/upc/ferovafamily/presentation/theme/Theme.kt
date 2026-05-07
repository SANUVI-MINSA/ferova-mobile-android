package pe.edu.upc.ferovafamily.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FerovaColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = White,
    primaryContainer = Rose,
    onPrimaryContainer = CrimsonDark,
    secondary = CrimsonLight,
    onSecondary = White,
    secondaryContainer = RoseMid,
    onSecondaryContainer = TextDark,
    background = Cream,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    surfaceVariant = RoseInput,
    onSurfaceVariant = TextMid,
    outline = RoseBorder,
    error = ErrorRed,
    onError = White,
)

@Composable
fun FerovaFamilyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FerovaColorScheme,
        typography = FerovaTypography,
        content = content
    )
}