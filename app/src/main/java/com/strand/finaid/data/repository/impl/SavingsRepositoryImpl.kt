package com.strand.finaid.data.repository.impl

import com.strand.finaid.data.local.dao.SavingsDao
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.mappers.asNetworkSavingsAccount
import com.strand.finaid.data.mappers.asSavingsAccount
import com.strand.finaid.data.models.SavingsAccount
import com.strand.finaid.data.network.SavingsNetworkDataSource
import com.strand.finaid.data.repository.SavingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class SavingsRepositoryImpl @Inject constructor(
    private val savingsDao: SavingsDao,
    private val network: SavingsNetworkDataSource
) : SavingsRepository {

    override fun getSavingsAccountsStream(): Flow<List<SavingsAccount>> =
        savingsDao.getSavingsAccountEntitiesStream()
            .map { it.map(SavingsAccountEntity::asSavingsAccount) }

    override fun getDeletedSavingsAccountsStream(): Flow<List<SavingsAccount>> =
        savingsDao.getDeletedSavingsAccountEntitiesStream()
            .map { it.map(SavingsAccountEntity::asSavingsAccount) }

    override suspend fun getSavingsAccountById(savingsAccountId: String): SavingsAccount =
        savingsDao.getSavingsAccountEntityById(savingsAccountId).asSavingsAccount()

    override suspend fun getLimitedNumberOfSavingsAccounts(numberOfTransactions: Int): List<SavingsAccount> =
        savingsDao.getLimitedNumberOfSavingsAccountEntities(numberOfTransactions)
            .map { it.asSavingsAccount() }

    override suspend fun getLastModifiedDate(): Date = savingsDao.getLastModifiedDate()

    override fun addSavingsAccountsListener(
        userId: String,
        lastModifiedDate: Date?,
        deleted: Boolean,
        onDocumentEvent: (Boolean, SavingsAccountEntity) -> Unit
    ) {
        network.addSavingsAccountsListener(userId, lastModifiedDate, deleted, onDocumentEvent)
    }

    override fun removeListener() {
        network.removeSavingsAccountsListener()
    }

    override suspend fun updateLocalDatabase(
        wasDocumentDeleted: Boolean,
        savingsAccount: SavingsAccountEntity
    ) {
        if (wasDocumentDeleted)
            savingsDao.deleteSavingsAccountEntity(savingsAccount)
        else
            savingsDao.upsertSavingsAccountEntity(savingsAccount)
    }

    override fun saveSavingsAccount(
        userId: String,
        savingsAccount: SavingsAccount,
        onResult: (Throwable?) -> Unit
    ) {
        network.saveSavingsAccount(userId, savingsAccount.asNetworkSavingsAccount(), onResult)
    }

    override fun moveSavingsAccountToTrash(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.moveSavingsAccountToTrash(userId, savingsAccountId, onResult)
    }

    override fun restoreSavingsAccountFromTrash(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.restoreSavingsAccountFromTrash(userId, savingsAccountId, onResult)
    }

    override fun deleteSavingsAccountPermanently(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        network.deleteSavingsAccountPermanently(userId, savingsAccountId, onResult)
    }

}