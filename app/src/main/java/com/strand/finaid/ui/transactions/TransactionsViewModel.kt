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
import com.strand.finaid.ui.components.charts.PeriodStateHolder
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

data class TransactionUiState(
    val id: String,
    val icon: ImageVector,
    val amount: Int,
    val memo: String,
    val category: CategoryUiState,
    val date: String,
    val dateMonthYear: String
)

data class CategoryUiState(
    val color: Color,
    val name: String
)

enum class SortOrder(@StringRes val titleId: Int) {
    Date(R.string.date),
    Sum(R.string.sum),
    Name(R.string.name)
}


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val transactionsRepository: TransactionsRepository,
    transactionsUseCase: TransactionsUseCase
) : FinaidViewModel(logService) {

    val periodStateHolder = PeriodStateHolder()

    val transactionsUiState = periodStateHolder.periodStateFlow.flatMapLatest {
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

    fun onDuplicateTransactionSwipe(transactionId: String, onSuccess: (String) -> Unit) {
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