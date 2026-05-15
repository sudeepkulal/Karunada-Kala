package com.example.karunada_kala.ui.theme

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KarnatakaLightColorScheme = lightColorScheme(
    primary       = KarnatakaRed,
    onPrimary     = CardSurface,
    secondary     = KarnatakaYellow,
    onSecondary   = TextPrimary,
    tertiary      = SuccessGreen,
    background    = Parchment,
    onBackground  = TextPrimary,
    surface       = CardSurface,
    onSurface     = TextPrimary,
    error         = ErrorRed,
    onError       = CardSurface,
)

private val KarnatakaDarkColorScheme = darkColorScheme(
    primary       = KarnatakaRed,
    onPrimary     = Color.White,
    secondary     = KarnatakaYellow,
    onSecondary   = Color.Black,
    tertiary      = SuccessGreen,
    background    = BackgroundDark,
    onBackground  = OnSurfaceDark,
    surface       = CardDark,
    onSurface     = OnSurfaceDark,
    error         = ErrorRed,
    onError       = Color.White,
)

@Composable
fun KarunadaKalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KarnatakaDarkColorScheme else KarnatakaLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = KarnatakaRed.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}