package com.strand.finaid.ui.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.transactions.CategoryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchScreenType(val title: String) {
    Transactions("Transaktioner"),
    Savings("Sparkonton")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService
) : FinaidViewModel(logService) {
    val searchScreens = SearchScreenType.values().map { it.title }

    var searchScreenType = mutableStateOf(SearchScreenType.Transactions)
        private set

    fun onSearchScreenTypeChange(newValue: Int) {
        searchScreenType.value = SearchScreenType.values()[newValue]
    }

    var categories = mutableStateListOf<CategoryUi>()
        private set

    private fun getCategories() {
        categories.clear()
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getCategories(accountService.getUserId(), ::onError) {
                categories.add(it.toCategoryUi())
            }
        }
    }

    init {
        getCategories()
    }

    val queryFlow = MutableStateFlow("")

    fun onQueryChange(newValue: String) {
        queryFlow.value = newValue
    }

    val transactionFlow = storageService
        .paginateTransactions(
            userId = accountService.getUserId(),
            pageSize = 20
        ).map { pagingData ->
            pagingData.map { transaction -> transaction.toTransactionUiState() }
        }
        .cachedIn(viewModelScope)
        .combine(queryFlow) { pagingData, query ->
            pagingData.filter { it.memo.contains(query, ignoreCase = true) }
        }
        .cachedIn(viewModelScope)

}