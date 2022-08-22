package com.strand.finaid.data.repository.impl

import com.strand.finaid.data.local.dao.TransactionsDao
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.mappers.asNetworkTransaction
import com.strand.finaid.data.mappers.asTransaction
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.data.network.TransactionsNetworkDataSource
import com.strand.finaid.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionsDao: TransactionsDao,
    private val network: TransactionsNetworkDataSource
) : TransactionsRepository {

    override fun getTransactionsStream(): Flow<List<Transaction>> =
        transactionsDao.getTransactionEntitiesStream()
            .map { it.map(TransactionEntity::asTransaction) }

    override fun getDeletedTransactionsStream(): Flow<List<Transaction>> =
        transactionsDao.getDeletedTransactionEntitiesStream()
            .map { it.map(TransactionEntity::asTransaction) }

    override suspend fun getTransactionById(transactionId: String): Transaction =
        transactionsDao.getTransactionEntityById(transactionId).asTransaction()

    override suspend fun getLimitedNumberOfTransactions(numberOfTransactions: Int): List<Transaction> =
        transactionsDao.getLimitedNumberOfTransactionEntities(numberOfTransactions)
            .map { it.asTransaction() }

    override suspend fun getLastModifiedDate(): Date = transactionsDao.getLastModifiedDate()

    override fun addTransactionsListener(
        userId: String,
        lastModifiedDate: Date?,
        deleted: Boolean,
        onDocumentEvent: (Boolean, TransactionEntity) -> Unit
    ) {
        network.addTransactionsListener(userId, lastModifiedDate, deleted, onDocumentEvent)
    }

    override fun removeListener() {
        network.removeTransactionsListener()
    }

    override suspend fun updateLocalDatabase(wasDocumentDeleted: Boolean, transaction: TransactionEntity) {
        if (wasDocumentDeleted)
            transactionsDao.deleteTransactionEntity(transaction)
        else
            transactionsDao.upsertTransactionEntity(transaction)
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