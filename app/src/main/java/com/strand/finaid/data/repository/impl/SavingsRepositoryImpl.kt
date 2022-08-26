package com.strand.finaid.data.repository.impl

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asNetworkSavingsAccount
import com.strand.finaid.data.mapper.asSavingsAccount
import com.strand.finaid.data.model.SavingsAccount
import com.strand.finaid.data.network.SavingsNetworkDataSource
import com.strand.finaid.data.network.model.NetworkSavingsAccount
import com.strand.finaid.data.repository.SavingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavingsRepositoryImpl @Inject constructor(
    private val network: SavingsNetworkDataSource
) : SavingsRepository {

    override fun getSavingsAccountsStream(userId: String): Flow<Result<List<SavingsAccount>>> {
        return network.addSavingsListener(userId, deleted = false)
            .map { result: Result<QuerySnapshot> ->
                when (result) {
                    is Result.Error -> result
                    Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<NetworkSavingsAccount>()?.map { it.asSavingsAccount() }
                    )
                }
            }
    }

    override fun getDeletedSavingsAccountsStream(userId: String): Flow<Result<List<SavingsAccount>>> {
        return network.addSavingsListener(userId, deleted = true)
            .map { result: Result<QuerySnapshot> ->
                when (result) {
                    is Result.Error -> result
                    Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(
                        result.data?.toObjects<NetworkSavingsAccount>()?.map { it.asSavingsAccount() }
                    )
                }
            }
    }

    override fun getSavingsAccount(
        userId: String,
        savingsAccountId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkSavingsAccount?) -> Unit
    ) {
        network.getSavingsAccount(userId, savingsAccountId, onError, onSuccess)
    }

    override fun getLimitedNumberOfSavingsAccounts(
        numberOfAccounts: Int,
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<NetworkSavingsAccount?>) -> Unit
    ) {
        network.getLimitedNumberOfSavingsAccounts(numberOfAccounts, userId, onError, onSuccess)
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