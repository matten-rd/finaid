package com.strand.finaid.preferences

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.strand.finaid.ui.theme.AppTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

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

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = remember {
        context.dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map {
                it[key] ?: defaultValue
            }
    }.collectAsState(initial = defaultValue)

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    scope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")