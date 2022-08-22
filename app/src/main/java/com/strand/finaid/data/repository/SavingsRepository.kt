package com.strand.finaid.data.repository

import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.models.SavingsAccount
import kotlinx.coroutines.flow.Flow
import java.util.*

interface SavingsRepository {
    fun getSavingsAccountsStream(): Flow<List<SavingsAccount>>
    fun getDeletedSavingsAccountsStream(): Flow<List<SavingsAccount>>
    suspend fun getSavingsAccountById(savingsAccountId: String): SavingsAccount
    suspend fun getLimitedNumberOfSavingsAccounts(numberOfTransactions: Int): List<SavingsAccount>
    suspend fun getLastModifiedDate(): Date
    fun addSavingsAccountsListener(userId: String, lastModifiedDate: Date?, deleted: Boolean = false, onDocumentEvent: (Boolean, SavingsAccountEntity) -> Unit)
    fun removeListener()
    suspend fun updateLocalDatabase(wasDocumentDeleted: Boolean, savingsAccount: SavingsAccountEntity)
    fun saveSavingsAccount(userId: String, savingsAccount: SavingsAccount, onResult: (Throwable?) -> Unit)
    fun moveSavingsAccountToTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun restoreSavingsAccountFromTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun deleteSavingsAccountPermanently(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
}