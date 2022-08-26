package com.strand.finaid.data.network.impl

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.strand.finaid.data.Result
import com.strand.finaid.data.network.SavingsNetworkDataSource
import com.strand.finaid.data.network.model.NetworkSavingsAccount
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.Instant
import java.util.*
import javax.inject.Inject

class SavingsNetworkDataSourceImpl @Inject constructor() : SavingsNetworkDataSource {

    private val String.userDocument: DocumentReference
        get() = Firebase.firestore.collection(UsersCollection).document(this)

    private fun Query.snapshotFlow(): Flow<Result<QuerySnapshot>> = callbackFlow {
        val listenerRegistration = addSnapshotListener { value, error ->
            val response = if (error == null) {
                Result.Success(value)
            } else {
                Result.Error(error)
            }
            this.trySend(response)
        }
        awaitClose { listenerRegistration.remove() }
    }

    override fun addSavingsListener(userId: String, deleted: Boolean): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(SavingsCollection)
            .whereEqualTo(DeletedField, deleted)

        return query.snapshotFlow()
    }

    override fun getSavingsAccount(
        userId: String,
        savingsAccountId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkSavingsAccount?) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccountId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObject()) }
    }

    override fun getLimitedNumberOfSavingsAccounts(
        numberOfAccounts: Int,
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<NetworkSavingsAccount?>) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .orderBy(AmountField)
            .limit(numberOfAccounts.toLong())
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObjects()) }
    }

    override fun saveSavingsAccount(
        userId: String,
        savingsAccount: NetworkSavingsAccount,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccount.id)
            .set(savingsAccount)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun moveSavingsAccountToTrash(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        val savingsAccountUpdates = mapOf(
            DeletedField to true,
            LastModifiedField to Date.from(Instant.now())
        )
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccountId)
            .update(savingsAccountUpdates)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun restoreSavingsAccountFromTrash(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        val savingsAccountUpdates = mapOf(
            DeletedField to false,
            LastModifiedField to Date.from(Instant.now())
        )
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccountId)
            .update(savingsAccountUpdates)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun deleteSavingsAccountPermanently(
        userId: String,
        savingsAccountId: String,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccountId)
            .delete()
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    companion object {
        private const val UsersCollection = "users"
        private const val SavingsCollection = "savings"

        private const val DeletedField = "deleted"
        private const val LastModifiedField = "lastModified"
        private const val AmountField = "amount"
    }
}