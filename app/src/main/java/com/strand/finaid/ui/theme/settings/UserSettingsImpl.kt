package com.strand.finaid.ui.theme.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserSettingsImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserSettings {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

    override val themeFlow: Flow<AppTheme> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { preferences ->
            AppTheme.fromOrdinal(preferences[PreferencesKeys.ThemeKey])
        }

    override suspend fun updateSelectedTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ThemeKey] = theme.ordinal
        }
    }

    private object PreferencesKeys {
        val ThemeKey = intPreferencesKey("ThemeKey")
    }

}