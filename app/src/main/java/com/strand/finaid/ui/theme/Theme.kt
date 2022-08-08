package com.strand.finaid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.strand.finaid.preferences.rememberThemePreference

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FinaidTheme(
    darkTheme: Boolean = isAppInDarkTheme(),
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
        typography = Typography,
        content = content
    )
}

@Composable
fun isAppInDarkTheme(): Boolean {
    val theme by rememberThemePreference()
    return when (theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.Auto -> isSystemInDarkTheme()
    }
}

enum class AppTheme(val title: String) {
    Light("Ljust"),
    Dark("Mörkt"),
    Auto("Systemets standardinställning");

    companion object {
        fun fromOrdinal(ordinal: Int?) = ordinal?.let { values()[it] } ?: Auto
    }
}
