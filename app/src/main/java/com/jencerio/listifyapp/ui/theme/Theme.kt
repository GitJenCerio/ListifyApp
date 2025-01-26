package com.jencerio.listifyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenAccent,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimaryTextColor,
    onSecondary = OnPrimaryTextColor,
    onTertiary = OnPrimaryTextColor,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenAccent,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimaryTextColor,
    onSecondary = OnPrimaryTextColor,
    onTertiary = OnPrimaryTextColor,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun ListifyAppTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Use a custom Typography if needed
        content = content
    )
}