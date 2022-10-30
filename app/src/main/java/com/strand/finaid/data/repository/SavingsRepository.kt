package com.strand.finaid.data.repository

import com.strand.finaid.data.models.SavingsAccount
import kotlinx.coroutines.flow.Flow

interface SavingsRepository {
    fun getSavingsAccountsStream(): Flow<List<SavingsAccount>>
    fun getDeletedSavingsAccountsStream(): Flow<List<SavingsAccount>>
    suspend fun getSavingsAccountById(savingsAccountId: String): SavingsAccount
    suspend fun getLimitedNumberOfSavingsAccounts(numberOfTransactions: Int): List<SavingsAccount>
    suspend fun saveSavingsAccount(savingsAccount: SavingsAccount)
    suspend fun moveSavingsAccountToTrash(savingsAccountId: String)
    suspend fun restoreSavingsAccountFromTrash(savingsAccountId: String)
    suspend fun deleteSavingsAccountPermanently(savingsAccountId: String)
}