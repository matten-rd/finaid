package com.strand.finaid.ui.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.strand.finaid.data.mappers.asCategory
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.network.StorageService
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    var categories = mutableStateListOf<Category>()
        private set

    private fun getCategories() {
        categories.clear()
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getCategories(accountService.getUserId(), ::onError) {
                categories.add(it.asCategory())
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


}