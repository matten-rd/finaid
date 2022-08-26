package com.strand.finaid.ui.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.strand.finaid.data.Result
import com.strand.finaid.data.model.Category
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class SearchScreenType(val title: String) {
    Transactions("Transaktioner"),
    Savings("Sparkonton")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val categoriesRepository: CategoriesRepository
) : FinaidViewModel(logService) {
    val searchScreens = SearchScreenType.values().map { it.title }

    var searchScreenType = mutableStateOf(SearchScreenType.Transactions)
        private set

    fun onSearchScreenTypeChange(newValue: Int) {
        searchScreenType.value = SearchScreenType.values()[newValue]
    }

    val categories: StateFlow<Result<List<Category>>> =
        categoriesRepository.getCategoriesStream(accountService.getUserId())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )

    val queryFlow = MutableStateFlow("")

    fun onQueryChange(newValue: String) {
        queryFlow.value = newValue
    }

}