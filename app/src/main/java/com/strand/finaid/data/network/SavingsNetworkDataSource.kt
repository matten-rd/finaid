package com.strand.finaid.data.network

import com.google.firebase.firestore.QuerySnapshot
import com.strand.finaid.data.Result
import com.strand.finaid.data.network.model.NetworkSavingsAccount
import kotlinx.coroutines.flow.Flow

interface SavingsNetworkDataSource {
    fun addSavingsListener(userId: String, deleted: Boolean = false): Flow<Result<QuerySnapshot>>
    fun getSavingsAccount(userId: String, savingsAccountId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkSavingsAccount?) -> Unit)
    fun getLimitedNumberOfSavingsAccounts(numberOfAccounts: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<NetworkSavingsAccount?>) -> Unit)
    fun saveSavingsAccount(userId: String, savingsAccount: NetworkSavingsAccount, onResult: (Throwable?) -> Unit)
    fun moveSavingsAccountToTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun restoreSavingsAccountFromTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun deleteSavingsAccountPermanently(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
}