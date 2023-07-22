package com.strand.finaid.data.network

import com.google.firebase.firestore.QuerySnapshot
import com.strand.finaid.data.Result
import com.strand.finaid.data.network.model.NetworkTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionsNetworkDataSource {
    fun addTransactionsListener(userId: String, deleted: Boolean = false): Flow<Result<QuerySnapshot>>
    fun getTransaction(userId: String, transactionId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkTransaction?) -> Unit)
    fun getLimitedNumberOfTransactions(numberOfTransactions: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<NetworkTransaction?>) -> Unit)
    fun saveTransaction(userId: String, transaction: NetworkTransaction, onResult: (Throwable?) -> Unit)
    fun moveTransactionToTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun restoreTransactionFromTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
}