package com.strand.finaid.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.strand.finaid.ui.theme.settings.AppTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
//        }
//    }

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


@Composable
fun rememberThemePreference(): MutableState<AppTheme> {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val themeKey = intPreferencesKey("ThemeKey")
    val state = remember {
        context.dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map { preferences ->
                AppTheme.fromOrdinal(preferences[themeKey])
            }
    }.collectAsState(initial = AppTheme.Auto)

    return remember {
        object : MutableState<AppTheme> {
            override var value: AppTheme
                get() = state.value
                set(value) {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[themeKey] = value.ordinal
                        }
                    }
                }

            override fun component1(): AppTheme = value
            override fun component2(): (AppTheme) -> Unit = { value = it }
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")