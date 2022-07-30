package com.strand.finaid.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : FinaidViewModel(logService,) {

    var transactions = mutableStateListOf<TransactionUiState>()
        private set

    var savingsAccounts = mutableStateListOf<SavingsAccountUiState>()
        private set

    private fun getTransactions() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getLimitedNumberOfTransactions(
                3, accountService.getUserId(), ::onError
            ) { items ->
                items.forEach {
                    if (it != null) transactions.add(it.toTransactionUiState())
                }
            }
        }
    }

    private fun getSavingsAccounts() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getLimitedNumberOfSavingsAccounts(
                3, accountService.getUserId(), ::onError
            ) { items ->
                items.forEach {
                    if (it != null) savingsAccounts.add(it.toSavingsAccountUiState())
                }
            }
        }
    }


    init {
        getTransactions()
        getSavingsAccounts()
    }

}