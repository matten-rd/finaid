package com.strand.finaid.data.repository

import com.strand.finaid.data.Result
import com.strand.finaid.data.model.SavingsAccount
import com.strand.finaid.data.network.model.NetworkSavingsAccount
import kotlinx.coroutines.flow.Flow

interface SavingsRepository {
    fun getSavingsAccountsStream(userId: String): Flow<Result<List<SavingsAccount>>>
    fun getDeletedSavingsAccountsStream(userId: String): Flow<Result<List<SavingsAccount>>>
    fun getSavingsAccount(userId: String, savingsAccountId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkSavingsAccount?) -> Unit)
    fun getLimitedNumberOfSavingsAccounts(numberOfAccounts: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<NetworkSavingsAccount?>) -> Unit)
    fun saveSavingsAccount(userId: String, savingsAccount: SavingsAccount, onResult: (Throwable?) -> Unit)
    fun moveSavingsAccountToTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun restoreSavingsAccountFromTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun deleteSavingsAccountPermanently(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
}