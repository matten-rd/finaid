package com.strand.finaid.data.repository

import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.models.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TransactionsRepository {
    fun getTransactionsStream(): Flow<List<Transaction>>
    fun getDeletedTransactionsStream(): Flow<List<Transaction>>
    suspend fun getTransactionById(transactionId: String): Transaction
    suspend fun getLimitedNumberOfTransactions(numberOfTransactions: Int): List<Transaction>
    suspend fun getLastModifiedDate(): Date
    fun addTransactionsListener(userId: String, lastModifiedDate: Date?, deleted: Boolean = false, onDocumentEvent: (Boolean, TransactionEntity) -> Unit)
    fun removeListener()
    suspend fun updateLocalDatabase(wasDocumentDeleted: Boolean, transaction: TransactionEntity)
    fun saveTransaction(userId: String, transaction: Transaction, onResult: (Throwable?) -> Unit)
    fun moveTransactionToTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun restoreTransactionFromTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
}