package com.strand.finaid.data.network

import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.network.models.NetworkTransaction

interface TransactionsNetworkDataSource {
    fun addTransactionsListener(userId: String, deleted: Boolean = false, onDocumentEvent: (Boolean, TransactionEntity) -> Unit)
    fun removeTransactionsListener()
    fun saveTransaction(userId: String, transaction: NetworkTransaction, onResult: (Throwable?) -> Unit)
    fun moveTransactionToTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun restoreTransactionFromTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
}