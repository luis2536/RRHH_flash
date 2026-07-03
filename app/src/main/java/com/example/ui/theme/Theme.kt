package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TechBlue,
    secondary = HrPurple,
    tertiary = FinanceGreen,
    background = BgBase,
    surface = SurfaceSolid,
    onPrimary = BgBase,
    onSecondary = BgBase,
    onTertiary = BgBase,
    onBackground = TextLight,
    onSurface = TextLight,
    error = DangerRed
)

private val LightColorScheme = lightColorScheme(
    primary = TechBlue,
    secondary = HrPurple,
    tertiary = FinanceGreen,
    background = BgBase,
    surface = SurfaceSolid,
    onPrimary = BgBase,
    onSecondary = BgBase,
    onTertiary = BgBase,
    onBackground = TextLight,
    onSurface = TextLight,
    error = DangerRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic dynamic coloring to force our beautiful branded Core Nexus theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
