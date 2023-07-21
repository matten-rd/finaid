package com.strand.finaid.ui.search

import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionsUseCase
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.transactions.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    logService: LogService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    transactionsUseCase: TransactionsUseCase
) : FinaidViewModel(logService) {
    private val _queryFlow = MutableStateFlow("")
    val queryFlow = _queryFlow.asStateFlow()

    fun onQueryChange(newValue: String) {
        _queryFlow.value = newValue
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

    val categories: StateFlow<List<Category>> = categoriesRepository.getCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactionsUiState: StateFlow<TransactionScreenUiState> =
        combine(sortFlow, selectedCategories, queryFlow) { sortOrder, selected, query ->
            Triple(sortOrder, selected, query)
        }.flatMapLatest { (sortOrder, selected, query) ->
            transactionsUseCase(sortOrder = sortOrder, selectedCategories = selected, searchQuery = query)
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