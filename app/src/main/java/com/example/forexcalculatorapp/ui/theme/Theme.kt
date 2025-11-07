package com.example.forexcalculatorapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    secondary = androidx.compose.ui.graphics.Color(0xFFCE93D8),
    tertiary = androidx.compose.ui.graphics.Color(0xFFA5D6A7),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF1E88E5),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF8E24AA),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF43A047)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF1976D2),
    secondary = androidx.compose.ui.graphics.Color(0xFF9C27B0),
    tertiary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE1BEE7),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFC8E6C9),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE)
)

@Composable
fun SimpleConvertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
