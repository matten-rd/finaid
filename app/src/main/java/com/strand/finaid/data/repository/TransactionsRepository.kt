package com.strand.finaid.data.repository

import com.strand.finaid.data.Result
import com.strand.finaid.data.model.Transaction
import com.strand.finaid.data.network.model.NetworkTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getTransactionsStream(userId: String): Flow<Result<List<Transaction>>>
    fun getDeletedTransactionsStream(userId: String): Flow<Result<List<Transaction>>>
    fun getTransaction(userId: String, transactionId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkTransaction?) -> Unit)
    fun getLimitedNumberOfTransactions(numberOfTransactions: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<NetworkTransaction?>) -> Unit)
    fun saveTransaction(userId: String, transaction: Transaction, onResult: (Throwable?) -> Unit)
    fun moveTransactionToTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun restoreTransactionFromTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
}