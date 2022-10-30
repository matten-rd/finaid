package com.strand.finaid.data.repository.impl

import com.strand.finaid.data.local.dao.SavingsDao
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.mappers.asSavingsAccount
import com.strand.finaid.data.mappers.asSavingsAccountEntity
import com.strand.finaid.data.models.SavingsAccount
import com.strand.finaid.data.repository.SavingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavingsRepositoryImpl @Inject constructor(
    private val savingsDao: SavingsDao
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

    override suspend fun saveSavingsAccount(savingsAccount: SavingsAccount) =
        savingsDao.insertSavingsAccountEntity(savingsAccount.asSavingsAccountEntity())

    override suspend fun moveSavingsAccountToTrash(savingsAccountId: String) =
        savingsDao.updateDeletedField(id = savingsAccountId, deleted = true)

    override suspend fun restoreSavingsAccountFromTrash(savingsAccountId: String) =
        savingsDao.updateDeletedField(id = savingsAccountId, deleted = false)

    override suspend fun deleteSavingsAccountPermanently(savingsAccountId: String) =
        savingsDao.deleteSavingsAccountEntityById(savingsAccountId)

}