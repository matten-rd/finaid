package com.strand.finaid.ui.transactions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionScreenUiStateUseCase
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

data class TransactionUiState(
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val amount: Int,
    val memo: String,
    val category: String,
    val date: String
)

enum class SortOrder(val title: String, val field: String) {
    Date("Datum", "date"),
    Sum("Summa", "amount"),
    Name("Namn", "memo")
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val categoriesRepository: CategoriesRepository,
    private val accountService: AccountService,
    private val transactionsRepository: TransactionsRepository,
    transactionsScreenUiStateUseCase: TransactionScreenUiStateUseCase
) : FinaidViewModel(logService) {

    val possibleSortOrders = SortOrder.values().map { it.title }

    val sortFlow = MutableStateFlow(SortOrder.Date)

    fun onSetSortOrder(newValue: Int) {
        val newSortOrder = SortOrder.values()[newValue]
        sortFlow.value = newSortOrder
    }

    private fun Iterable<Transaction>.customSortedBy(sortOrder: SortOrder): List<Transaction> {
        return when (sortOrder) {
            SortOrder.Date -> sortedWith(compareByDescending { it.date })
            SortOrder.Sum -> sortedWith(compareByDescending { it.amount.absoluteValue })
            SortOrder.Name -> sortedWith(compareBy { it.memo.lowercase() })
        }
    }

    val transactionsUiState: StateFlow<TransactionScreenUiState> = transactionsScreenUiStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionScreenUiState.Loading
        )

    val categories: StateFlow<Result<List<Category>>> =
        categoriesRepository.addCategoriesListener(accountService.getUserId())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )

    fun onDeleteTransactionClick(transaction: TransactionUiState) {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(accountService.getUserId(), transaction.id) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.transaction_removed)
                else
                    onError(error)
            }
        }
    }

    fun addListener() {
        viewModelScope.launch {
            val lastModifiedDate = transactionsRepository.getLastModifiedDate()
            transactionsRepository.addTransactionsListener(accountService.getUserId(), lastModifiedDate,false, ::onDocumentEvent)
        }
    }

    fun removeListener() {
        viewModelScope.launch { transactionsRepository.removeListener() }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionsRepository.updateLocalDatabase(wasDocumentDeleted, transaction)
        }
    }
}