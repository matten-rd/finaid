package com.strand.finaid.ui.transactions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asTransactionUiState
import com.strand.finaid.data.model.Category
import com.strand.finaid.data.model.Transaction
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

sealed interface TransactionScreenUiState {
    data class Success(val transactions: List<TransactionUiState>?) : TransactionScreenUiState
    object Error : TransactionScreenUiState
    object Loading : TransactionScreenUiState
}

enum class SortOrder(val title: String, val field: String) {
    Date("Datum", "date"),
    Sum("Summa", "amount"),
    Name("Namn", "memo")
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository
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

    private val _transactionsUiState: Flow<TransactionScreenUiState> = sortFlow.flatMapLatest { order ->
        transactionsRepository
            .getTransactionsStream(accountService.getUserId())
            .map { result: Result<List<Transaction>> ->
                when (result) {
                    is Result.Success -> TransactionScreenUiState.Success(
                        result.data?.customSortedBy(order)?.map { it.asTransactionUiState() }
                    )
                    Result.Loading -> TransactionScreenUiState.Loading
                    is Result.Error -> {
                        onError(result.exception)
                        TransactionScreenUiState.Error
                    }
                }
            }
        }

    val transactionsUiState: StateFlow<TransactionScreenUiState> = _transactionsUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionScreenUiState.Loading
        )

    val categories: StateFlow<Result<List<Category>>> =
        categoriesRepository.getCategoriesStream(accountService.getUserId())
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
}