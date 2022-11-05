package com.strand.finaid.data.repository.impl

import com.strand.finaid.data.local.dao.TransactionsDao
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.mappers.asTransaction
import com.strand.finaid.data.mappers.asTransactionEntity
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.data.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionsDao: TransactionsDao
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

    override suspend fun getTransactionSum(): Int = transactionsDao.getTransactionSum()

    override suspend fun saveTransaction(transaction: Transaction) =
        transactionsDao.insertTransactionEntity(transaction.asTransactionEntity())

    override suspend fun moveTransactionToTrash(transactionId: String) =
        transactionsDao.updateDeletedField(id = transactionId, deleted = true)

    override suspend fun restoreTransactionFromTrash(transactionId: String) =
        transactionsDao.updateDeletedField(id = transactionId, deleted = false)

    override suspend fun deleteTransactionPermanently(transactionId: String) =
        transactionsDao.deleteTransactionEntityById(transactionId)

}