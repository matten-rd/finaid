package com.strand.finaid.ui.transactions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.R
import com.strand.finaid.model.Result
import com.strand.finaid.model.data.FirebaseCategory
import com.strand.finaid.model.data.Transaction
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
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

enum class SortOrder(val title: String, val field: String) {
    Date("Datum", "date"),
    Sum("Summa", "amount"),
    Name("Namn", "memo")
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
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

    private val transactionResult: Flow<Result<List<TransactionUiState>>> = sortFlow.flatMapLatest { order ->
        storageService
            .addTransactionsListener(accountService.getUserId())
            .map { result ->
                when (result) {
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<Transaction>()
                            ?.customSortedBy(order)
                            ?.map { it.toTransactionUiState() }
                    )
                    Result.Loading -> Result.Loading
                    is Result.Error -> {
                        onError(result.exception)
                        result
                    }
                }
            }
        }

    val transactions: StateFlow<Result<List<TransactionUiState>>> = transactionResult
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )


    private val categoriesResult: Flow<Result<List<CategoryUi>>> =
        storageService.addCategoriesListener(accountService.getUserId())
            .map { result ->
                when (result) {
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<FirebaseCategory>()?.map { it.toCategoryUi() })
                    Result.Loading -> Result.Loading
                    is Result.Error -> {
                        onError(result.exception)
                        result
                    }
                }
            }

    val categories: StateFlow<Result<List<CategoryUi>>> = categoriesResult
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun onDeleteTransactionClick(transaction: TransactionUiState) {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.moveTransactionToTrash(accountService.getUserId(), transaction.id) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.transaction_removed)
                else
                    onError(error)
            }
        }
    }
}