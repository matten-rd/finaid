package com.strand.finaid.domain

import com.strand.finaid.data.mappers.asSavingsAccountUiState
import com.strand.finaid.data.mappers.asTransactionUiState
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface HomeScreenUiState {
    data class Success(
        val transactionSum: Int,
        val transactions: List<TransactionUiState>,
        val savingsAccountSum: Int,
        val savingsAccounts: List<SavingsAccountUiState>
    ) : HomeScreenUiState
    object Error : HomeScreenUiState
    object Loading : HomeScreenUiState
}

class HomeScreenUiStateUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository,
    private val transactionsRepository: TransactionsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): HomeScreenUiState = withContext(defaultDispatcher) {
        val transactionSum = transactionsRepository.getTransactionSum()
        val transactions = transactionsRepository.getLimitedNumberOfTransactions(3)
            .map { transaction -> transaction.asTransactionUiState() }

        val savingsAccountSum = savingsRepository.getSavingsAccountSum()
        val savingsAccounts = savingsRepository.getLimitedNumberOfSavingsAccounts(3)
            .map { savingsAccount -> savingsAccount.asSavingsAccountUiState() }

        HomeScreenUiState.Success(
            transactionSum = transactionSum,
            transactions = transactions,
            savingsAccountSum = savingsAccountSum,
            savingsAccounts = savingsAccounts
        )
    }
}