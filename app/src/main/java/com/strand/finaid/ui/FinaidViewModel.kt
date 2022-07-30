package com.strand.finaid.ui

import androidx.lifecycle.ViewModel
import com.strand.finaid.model.service.LogService
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler


open class FinaidViewModel(
    private val logService: LogService
) : ViewModel() {

    val showErrorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    val logErrorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logService.logNonFatalCrash(throwable)
    }

    fun onError(error: Throwable) {
        SnackbarManager.showMessage(error.toSnackbarMessage())
    }
}