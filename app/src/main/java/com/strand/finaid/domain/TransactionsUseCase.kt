package com.strand.finaid.domain

import com.strand.finaid.data.Result
import com.strand.finaid.data.asResult
import com.strand.finaid.data.mappers.asTransactionUiState
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ext.toDate
import com.strand.finaid.ui.components.charts.Period
import com.strand.finaid.ui.components.charts.PeriodState
import com.strand.finaid.ui.transactions.SortOrder
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.absoluteValue

data class TransactionScreenUiState(
    val transactions: List<TransactionUiState>? = null,
    val groupedTransactions: Map<String, List<TransactionUiState>>? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)

class TransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    operator fun invoke(
        deleted: Boolean = false,
        sortOrder: SortOrder = SortOrder.Date,
        periodState: PeriodState = PeriodState(),
        selectedCategories: List<Category> = emptyList(),
        searchQuery: String = ""
    ): Flow<TransactionScreenUiState> {
        val transactionsStream: Flow<List<Transaction>> =
            if (deleted) {
                transactionsRepository.getDeletedTransactionsStream()
            } else {
                transactionsRepository.getTransactionEntitiesStream()
            }

        val startOfMonth = periodState.selectedMonth.withDayOfMonth(1)
        val startOfYear = periodState.selectedYear.withDayOfYear(1)

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
                            when(periodState.period) {
                                Period.Month -> it.date >= startOfMonth.toDate() && it.date < startOfMonth.plusMonths(1).toDate()
                                Period.Year -> it.date >= startOfYear.toDate() && it.date < startOfYear.plusYears(1).toDate()
                                Period.Total -> true
                            }
                        }?.filter {
                            if (searchQuery.isNotEmpty())
                                it.memo.contains(searchQuery, ignoreCase = true)
                            else
                                true
                        }

                        val sortedTransactions = when(sortOrder) {
                            SortOrder.Date -> transactions?.sortedByDescending { it.date }
                            SortOrder.Sum -> transactions?.sortedByDescending { it.amount.absoluteValue }
                            SortOrder.Name -> transactions?.sortedBy { it.memo }
                        }?.map { it.asTransactionUiState() }

                        val groupedTransactions = sortedTransactions?.groupBy { it.dateMonthYear }

                        TransactionScreenUiState(sortedTransactions, groupedTransactions, isLoading = false, isError = false)
                    }
                    Result.Loading -> TransactionScreenUiState(null, null, isLoading = true, isError = false)
                    is Result.Error -> TransactionScreenUiState(null, null, isLoading = false, isError = true)
                }
            }
    }
}