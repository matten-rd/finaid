package com.strand.finaid.ui.transactions

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionsUseCase
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

data class TransactionUiState(
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val amount: Int,
    val memo: String,
    val category: String,
    val date: String,
    val dateMonthYear: String
)

enum class SortOrder(@StringRes val titleId: Int) {
    Date(R.string.date),
    Sum(R.string.sum),
    Name(R.string.name)
}

enum class Period(@StringRes val periodId: Int) {
    Month(R.string.month),
    Year(R.string.year),
    Total(R.string.total)
}

data class PeriodState(
    val period: Period = Period.Total,
    val selectedYear: LocalDate = LocalDate.now(),
    val selectedMonth: LocalDate = LocalDate.now()
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val transactionsRepository: TransactionsRepository,
    transactionsUseCase: TransactionsUseCase
) : FinaidViewModel(logService) {

    val periods = Period.values().map { it.periodId }

    private val _periodStateFlow = MutableStateFlow(PeriodState())
    val periodStateFlow = _periodStateFlow.asStateFlow()

    fun onSetPeriod(newValue: Int) {
        val newPeriod = Period.values()[newValue]
        _periodStateFlow.update { it.copy(period = newPeriod) }
    }

    fun incrementYear() {
        _periodStateFlow.update { it.copy(selectedYear = it.selectedYear.plusYears(1)) }
    }
    fun decrementYear() {
        _periodStateFlow.update { it.copy(selectedYear = it.selectedYear.minusYears(1)) }
    }
    fun incrementMonth() {
        _periodStateFlow.update { it.copy(selectedMonth = it.selectedMonth.plusMonths(1)) }
    }
    fun decrementMonth() {
        _periodStateFlow.update { it.copy(selectedMonth = it.selectedMonth.minusMonths(1)) }
    }

    val transactionsUiState = periodStateFlow.flatMapLatest {
        transactionsUseCase(periodState = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionScreenUiState(isLoading = true)
    )

    fun onDeleteTransactionSwipe(transactionId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(transactionId = transactionId)
        }
        viewModelScope.launch(showErrorExceptionHandler) {
            SnackbarManager.showMessage(R.string.transaction_removed, false, R.string.undo) {
                viewModelScope.launch {
                    transactionsRepository.restoreTransactionFromTrash(transactionId = transactionId)
                }
            }
        }
    }

    fun duplicateTransaction(transactionId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch(showErrorExceptionHandler) {
            var transaction = transactionsRepository.getTransactionById(transactionId = transactionId)
            transaction = transaction.copy(
                id = UUID.randomUUID().toString(),
                date = Date.from(Instant.now()),
                lastModified = Date.from(Instant.now())
            )
            transactionsRepository.saveTransaction(transaction)
            onSuccess(transaction.id)
        }
    }
}