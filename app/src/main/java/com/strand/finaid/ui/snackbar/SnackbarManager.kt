package com.strand.finaid.ui.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackbarMessage?> get() = messages.asStateFlow()

    fun showMessage(@StringRes message: Int, actionLabel: String? = null, onActionPerformed: () -> Unit = {}) {
        messages.value = SnackbarMessage.ResourceSnackbar(message, actionLabel, onActionPerformed)
    }

    fun showMessage(message: SnackbarMessage) {
        messages.value = message
    }
}