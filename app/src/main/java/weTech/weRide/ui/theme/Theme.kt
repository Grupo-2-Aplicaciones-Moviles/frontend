package weTech.weRide.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * WeRide Dark Color Scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = EnergyGreen,
    secondary = MediumGray,
    tertiary = White,
    background = Black,
    surface = DarkGray,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
    error = ErrorRed,
    onError = White
)

/**
 * WeRide Light Color Scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = EnergyGreen,
    secondary = MediumGray,
    tertiary = Black,
    background = White,
    surface = LightGray,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    error = ErrorRed,
    onError = White
)

/**
 * WeRide Application Theme
 * Uses light theme as primary, with dark theme support
 */
@Composable
fun WeRideTheme(
    darkTheme: Boolean = false, // WeRide uses light theme as primary
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Legacy theme name for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WeRideTheme(darkTheme = darkTheme, content = content)
}
