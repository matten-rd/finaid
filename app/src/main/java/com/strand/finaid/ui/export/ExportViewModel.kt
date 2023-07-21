package com.strand.finaid.ui.export

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ext.formatMonthYear
import com.strand.finaid.ext.toDate
import com.strand.finaid.services.export.ExportConfig
import com.strand.finaid.services.export.ExportService
import com.strand.finaid.services.export.Exports
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


data class ExportScreenUiState(
    val fileName: String = "",
    val sheetName: String = "",
    val date: LocalDate = LocalDate.now()
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    logService: LogService,
    private val transactionsRepository: TransactionsRepository
) : FinaidViewModel(logService) {

    var uiState by mutableStateOf(ExportScreenUiState())

    fun onDateChange(newDate: LocalDate) {
        uiState = uiState.copy(date = newDate)
    }

    fun onFileNameChange(newFileName: String) {
        uiState = uiState.copy(fileName = newFileName)
    }

    fun onSheetNameChange(newSheetName: String) {
        uiState = uiState.copy(sheetName = newSheetName)
    }

    fun onExportClick() = viewModelScope.launch(Dispatchers.IO) {
        val allTransactions = transactionsRepository.getTransactionEntitiesUpToDate(uiState.date.toDate())
        if (allTransactions.isEmpty()) {
            SnackbarManager.showMessage(R.string.empty_period_error)
            return@launch
        }
        println(allTransactions)

        val sheetName = uiState.sheetName.ifBlank { uiState.date.formatMonthYear() }

        ExportService.exportTransactions(
            type = Exports.XLSX(ExportConfig(sheetName = sheetName)),
            content = allTransactions
        ).catch { error ->
            SnackbarManager.showMessage(error.toSnackbarMessage())
        }
        .collect {
            SnackbarManager.showMessage(R.string.export_success)
        }
    }

}