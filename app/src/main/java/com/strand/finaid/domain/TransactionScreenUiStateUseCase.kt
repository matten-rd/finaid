package com.strand.finaid.domain

import com.strand.finaid.data.Result
import com.strand.finaid.data.asResult
import com.strand.finaid.data.mappers.asTransactionUiState
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ui.transactions.Period
import com.strand.finaid.ui.transactions.SortOrder
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

sealed interface TransactionScreenUiState {
    data class Success(val transactions: List<TransactionUiState>?) : TransactionScreenUiState
    object Error : TransactionScreenUiState
    object Loading : TransactionScreenUiState
}

class TransactionScreenUiStateUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    operator fun invoke(
        deleted: Boolean = false,
        sortOrder: SortOrder = SortOrder.Date,
        period: Period = Period.Total,
        selectedCategories: List<Category> = emptyList()
    ): Flow<TransactionScreenUiState> {
        val transactionsStream: Flow<List<Transaction>> =
            if (deleted) {
                transactionsRepository.getDeletedTransactionsStream()
            } else {
                transactionsRepository.getTransactionEntitiesStream()
            }

        val startOfMonth = Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val startOfYear = Date.from(LocalDate.now().withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        return transactionsStream
            .asResult()
            .map { result: Result<List<Transaction>> ->
                when (result) {
                    is Result.Success -> {
                        val transactions = result.data?.filter { transaction ->
                            if (selectedCategories.isNotEmpty())
                                transaction.category in selectedCategories
                            else
                                true
                        }?.filter {
                            when(period) {
                                Period.Month -> it.date >= startOfMonth
                                Period.Year -> it.date >= startOfYear
                                Period.Total -> true
                            }
                        }

                        val sortedTransactions = when(sortOrder) {
                            SortOrder.Date -> transactions?.sortedByDescending { it.date }
                            SortOrder.Sum -> transactions?.sortedByDescending { it.amount.absoluteValue }
                            SortOrder.Name -> transactions?.sortedBy { it.memo }
                        }

                        TransactionScreenUiState.Success(sortedTransactions?.map { it.asTransactionUiState() })
                    }
                    Result.Loading -> TransactionScreenUiState.Loading
                    is Result.Error -> TransactionScreenUiState.Error
                }
            }
    }
}