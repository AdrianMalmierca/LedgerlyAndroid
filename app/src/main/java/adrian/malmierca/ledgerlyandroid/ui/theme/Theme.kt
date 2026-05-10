package adrian.malmierca.ledgerlyandroid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LedgerlyPrimary,
    onPrimary = LedgerlyOnPrimary,
    primaryContainer = LedgerlyPrimaryContainer,
    onPrimaryContainer = LedgerlyOnPrimaryContainer,
    secondary = LedgerlySecondary,
    onSecondary = LedgerlyOnSecondary,
    secondaryContainer = LedgerlySecondaryContainer,
    onSecondaryContainer = LedgerlyOnSecondaryContainer,
    tertiary = LedgerlyTertiary,
    onTertiary = LedgerlyOnTertiary,
    tertiaryContainer = LedgerlyTertiaryContainer,
    onTertiaryContainer = LedgerlyOnTertiaryContainer,
    error = LedgerlyError,
    onError = LedgerlyOnError,
    background = LedgerlyBackground,
    onBackground = LedgerlyOnBackground,
    surface = LedgerlySurface,
    onSurface = LedgerlyOnSurface,
    surfaceVariant = LedgerlySurfaceVariant,
    onSurfaceVariant = LedgerlyOnSurfaceVariant,
    outline = LedgerlyOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = LedgerlyPrimaryDark,
    onPrimary = LedgerlyOnPrimaryDark,
    primaryContainer = LedgerlyPrimaryContainerDark,
    onPrimaryContainer = LedgerlyOnPrimaryContainerDark,
    background = LedgerlyBackgroundDark,
    onBackground = LedgerlyOnBackgroundDark,
    surface = LedgerlySurfaceDark,
    onSurface = LedgerlyOnSurfaceDark
)

@Composable
fun LedgerlyAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current //access to the Andrid View from Compose
    if (!view.isInEditMode) { //to not execute in the android preview, to avoid errors in the editor
        SideEffect { //access to the app window
            val window = (view.context as Activity).window //access to some system stuff like the statusbar
            window.statusBarColor = colorScheme.primary.toArgb()
            //to put the icons dark with light background or light icons with dark background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}