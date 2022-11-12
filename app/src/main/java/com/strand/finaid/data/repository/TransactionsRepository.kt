package com.strand.finaid.data.repository

import com.strand.finaid.data.models.Transaction
import com.strand.finaid.ui.transactions.SortOrder
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getTransactionEntitiesStream(sortOrder: SortOrder): Flow<List<Transaction>>
    fun getDeletedTransactionsStream(): Flow<List<Transaction>>
    suspend fun getTransactionById(transactionId: String): Transaction
    suspend fun getLimitedNumberOfTransactions(numberOfTransactions: Int): List<Transaction>
    suspend fun getTransactionSum(): Int
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun moveTransactionToTrash(transactionId: String)
    suspend fun restoreTransactionFromTrash(transactionId: String)
    suspend fun deleteTransactionPermanently(transactionId: String)
}