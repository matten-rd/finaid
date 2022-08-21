package com.strand.finaid.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.strand.finaid.data.mappers.asSavingsAccountUiState
import com.strand.finaid.data.mappers.asTransactionUiState
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.network.StorageService
import com.strand.finaid.data.repository.TransactionsRepository
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
    private val accountService: AccountService,
    private val transactionsRepository: TransactionsRepository
) : FinaidViewModel(logService,) {

    var transactions = mutableStateListOf<TransactionUiState>()
        private set

    var savingsAccounts = mutableStateListOf<SavingsAccountUiState>()
        private set

    private fun getTransactions() {
        viewModelScope.launch(showErrorExceptionHandler) {
            val trans = transactionsRepository.getLimitedNumberOfTransactions(3)
            trans.forEach { transaction -> transactions.add(transaction.asTransactionUiState()) }
        }
    }

    private fun getSavingsAccounts() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getLimitedNumberOfSavingsAccounts(
                3, accountService.getUserId(), ::onError
            ) { items ->
                items.forEach {
                    if (it != null) savingsAccounts.add(it.asSavingsAccountUiState())
                }
            }
        }
    }


    init {
        getTransactions()
        getSavingsAccounts()
    }

}