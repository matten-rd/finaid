package com.strand.finaid.data.network

import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.network.models.NetworkSavingsAccount

interface SavingsNetworkDataSource {
    fun addSavingsAccountsListener(userId: String, deleted: Boolean = false, onDocumentEvent: (Boolean, SavingsAccountEntity) -> Unit)
    fun removeSavingsAccountsListener()
    fun saveSavingsAccount(userId: String, savingsAccount: NetworkSavingsAccount, onResult: (Throwable?) -> Unit)
    fun moveSavingsAccountToTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun restoreSavingsAccountFromTrash(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun deleteSavingsAccountPermanently(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
}