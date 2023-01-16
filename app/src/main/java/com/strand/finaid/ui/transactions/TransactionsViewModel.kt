package com.strand.finaid.ui.transactions

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionScreenUiStateUseCase
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

data class TransactionUiState(
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val amount: Int,
    val memo: String,
    val category: String,
    val date: String
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

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    transactionsScreenUiStateUseCase: TransactionScreenUiStateUseCase
) : FinaidViewModel(logService) {

    val periods = Period.values().map { it.periodId }
    private val _periodFlow = MutableStateFlow(Period.Total)
    val periodFlow = _periodFlow.asStateFlow()

    fun onSetPeriod(newValue: Int) {
        val newPeriod = Period.values()[newValue]
        _periodFlow.value = newPeriod
    }

    val possibleSortOrders = SortOrder.values().map { it.titleId }
    private val _sortFlow = MutableStateFlow(SortOrder.Date)
    val sortFlow = _sortFlow.asStateFlow()

    fun onSetSortOrder(newValue: Int) {
        val newSortOrder = SortOrder.values()[newValue]
        _sortFlow.value = newSortOrder
    }

    private val _selectedCategories = MutableStateFlow(emptyList<Category>())
    val selectedCategories = _selectedCategories.asStateFlow()

    fun toggleCategory(category: Category) {
        _selectedCategories.update {
            if (category in it) { it - category } else { it + category }
        }
    }

    fun clearSelectedCategories() {
        _selectedCategories.update { emptyList() }
    }

    val transactionsUiState: StateFlow<TransactionScreenUiState> =
        combine(sortFlow, periodFlow, selectedCategories) { sortOrder, period, selected ->
            Triple(sortOrder, period, selected)
        }.flatMapLatest { (sortOrder, period, selected) ->
            transactionsScreenUiStateUseCase(sortOrder = sortOrder, period = period, selectedCategories = selected)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionScreenUiState.Loading
        )


    val categories: StateFlow<List<Category>> = categoriesRepository.getCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedTransaction = mutableStateOf<TransactionUiState?>(null)

    fun setSelectedTransaction(transaction: TransactionUiState) {
        selectedTransaction.value = transaction
    }

    fun onConfirmDeleteTransactionClick() {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(transactionId = selectedTransaction.value!!.id)
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