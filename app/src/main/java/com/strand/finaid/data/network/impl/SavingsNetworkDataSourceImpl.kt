package com.strand.finaid.data.network.impl

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.mappers.asSavingsAccountEntity
import com.strand.finaid.data.network.SavingsNetworkDataSource
import com.strand.finaid.data.network.models.NetworkSavingsAccount
import java.time.Instant
import java.util.*
import javax.inject.Inject

class SavingsNetworkDataSourceImpl @Inject constructor() : SavingsNetworkDataSource {

    private val String.userDocument: DocumentReference
        get() = Firebase.firestore.collection(UsersCollection).document(this)

    private var listenerRegistration: ListenerRegistration? = null

    override fun addSavingsAccountsListener(
        userId: String,
        deleted: Boolean,
        onDocumentEvent: (Boolean, SavingsAccountEntity) -> Unit
    ) {
        val query = userId.userDocument
            .collection(SavingsCollection)
            .whereGreaterThanOrEqualTo(LastModifiedField, Date.from(Instant.parse("2018-11-30T18:35:24.00Z")))

        listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener

            snapshot?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == DocumentChange.Type.REMOVED
                val savingsAccount = it.document.toObject<NetworkSavingsAccount>()
                onDocumentEvent(wasDocumentDeleted, savingsAccount.asSavingsAccountEntity())
            }
        }
    }

    override fun removeSavingsAccountsListener() {
        listenerRegistration?.remove()
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
    }

}