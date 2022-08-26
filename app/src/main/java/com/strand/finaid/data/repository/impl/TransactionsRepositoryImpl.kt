package com.strand.finaid.data.repository.impl

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asNetworkTransaction
import com.strand.finaid.data.mapper.asTransaction
import com.strand.finaid.data.model.Transaction
import com.strand.finaid.data.network.TransactionsNetworkDataSource
import com.strand.finaid.data.network.model.NetworkTransaction
import com.strand.finaid.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val network: TransactionsNetworkDataSource
) : TransactionsRepository {

    override fun getTransactionsStream(userId: String): Flow<Result<List<Transaction>>> {
        return network.addTransactionsListener(userId, deleted = false)
            .map { result: Result<QuerySnapshot> ->
                when (result) {
                    is Result.Error -> result
                    Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<NetworkTransaction>()?.map { it.asTransaction() }
                    )
                }
            }
    }

    override fun getDeletedTransactionsStream(userId: String): Flow<Result<List<Transaction>>> {
        return network.addTransactionsListener(userId, deleted = true)
            .map { result: Result<QuerySnapshot> ->
                when (result) {
                    is Result.Error -> result
                    Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<NetworkTransaction>()?.map { it.asTransaction() }
                    )
                }
            }
    }

    override fun getTransaction(
        userId: String,
        transactionId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkTransaction?) -> Unit
    ) {
        network.getTransaction(userId, transactionId, onError, onSuccess)
    }

    override fun getLimitedNumberOfTransactions(
        numberOfTransactions: Int,
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<NetworkTransaction?>) -> Unit
    ) {
        network.getLimitedNumberOfTransactions(numberOfTransactions, userId, onError, onSuccess)
    }

    override fun saveTransaction(
        userId: String,
        transaction: Transaction,
        onResult: (Throwable?) -> Unit
    ) {
        network.saveTransaction(userId, transaction.asNetworkTransaction(), onResult)
    }

    override fun moveTransactionToTrash(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.moveTransactionToTrash(userId, transactionId, onResult)
    }

    override fun restoreTransactionFromTrash(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.restoreTransactionFromTrash(userId, transactionId, onResult)
    }

    override fun deleteTransactionPermanently(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.deleteTransactionPermanently(userId, transactionId, onResult)
    }

}