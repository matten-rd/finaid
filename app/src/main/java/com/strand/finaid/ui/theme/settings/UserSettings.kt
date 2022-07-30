package com.strand.finaid.ui.theme.settings

import kotlinx.coroutines.flow.Flow

enum class AppTheme(val title: String) {
    Light("Ljust"),
    Dark("Mörkt"),
    Auto("Systemets standardinställning");

    companion object {
        fun fromOrdinal(ordinal: Int?) = ordinal?.let { values()[it] } ?: Auto
    }
}

interface UserSettings {
    val themeFlow: Flow<AppTheme>
    suspend fun updateSelectedTheme(theme: AppTheme)
}