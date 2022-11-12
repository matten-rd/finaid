package com.strand.finaid.ui.transactions

import androidx.annotation.StringRes
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

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    logService: LogService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    transactionsScreenUiStateUseCase: TransactionScreenUiStateUseCase
) : FinaidViewModel(logService) {

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

    val transactionsUiState: StateFlow<TransactionScreenUiState> =
        combine(sortFlow, selectedCategories) { sortOrder, selected ->
            Pair(sortOrder, selected)
        }.flatMapLatest { (sortOrder, selected) ->
            transactionsScreenUiStateUseCase(sortOrder = sortOrder, selectedCategories = selected)
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

    fun onDeleteTransactionClick(transactionId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(transactionId = transactionId)
        }
    }
}