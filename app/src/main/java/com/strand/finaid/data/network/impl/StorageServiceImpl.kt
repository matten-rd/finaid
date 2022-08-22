package com.strand.finaid.data.network.impl

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.strand.finaid.data.Result
import com.strand.finaid.data.models.SavingsAccount
import com.strand.finaid.data.network.StorageService
import com.strand.finaid.data.network.models.NetworkCategory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.inject.Inject


class StorageServiceImpl @Inject constructor() : StorageService {

    private val Date.yearMonthFormat: String
        get() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(this)

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

    /**
     * Categories
     */
    override fun addCategoriesListener(userId: String, deleted: Boolean): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(CategoriesCollection)
            .whereEqualTo(DeletedField, deleted)

        return query.snapshotFlow()
    }

    override fun getCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkCategory) -> Unit) {
        userId.userDocument
            .collection(CategoriesCollection)
            .whereEqualTo(DeletedField, false)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result ->
                result.forEach { document -> onSuccess(document.toObject()) }
            }
    }

    override fun addTransactionCategory(
        userId: String,
        category: NetworkCategory,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(CategoriesCollection)
            .document(category.id)
            .set(category)
            .addOnCompleteListener { task -> onResult(task.exception) }

        userId.userDocument
            .collection(TransactionsCollection)
            .whereEqualTo("category.id", category.id)
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    document.reference
                        .update(mapOf("category.hexCode" to category.hexCode, "category.name" to category.name))
                }
            }
            .addOnFailureListener { e -> onResult(e) }
    }

    override fun moveTransactionCategoryToTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(CategoriesCollection)
            .document(categoryId)
            .update(DeletedField, true)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun deleteTransactionCategoryPermanently(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(CategoriesCollection)
            .document(categoryId)
            .delete()
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun getDeletedCategories(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (NetworkCategory) -> Unit
    ) {
        userId.userDocument
            .collection(CategoriesCollection)
            .whereEqualTo(DeletedField, true)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result ->
                result.forEach { document -> onSuccess(document.toObject()) }
            }
    }

    override fun restoreCategoryFromTrash(
        userId: String,
        categoryId: String,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(CategoriesCollection)
            .document(categoryId)
            .update(DeletedField, false)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    /**
     * Savings
     */
    override fun addSavingsListener(userId: String, deleted: Boolean): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(SavingsCollection)
            .whereEqualTo(DeletedField, deleted)

        return query.snapshotFlow()
    }

    override fun saveSavingsAccount(
        userId: String,
        savingsAccount: SavingsAccount,
        onResult: (Throwable?) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccount.id)
            .set(savingsAccount)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun getSavingsAccount(
        userId: String,
        savingsAccountId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (SavingsAccount?) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .document(savingsAccountId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObject()) }
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

    override fun getLimitedNumberOfSavingsAccounts(
        numberOfAccounts: Int,
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<SavingsAccount?>) -> Unit
    ) {
        userId.userDocument
            .collection(SavingsCollection)
            .orderBy(AmountField)
            .limit(numberOfAccounts.toLong())
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObjects()) }
    }


    companion object {
        private const val UsersCollection = "users"
        private const val TransactionsCollection = "transactions"
        private const val TransactionsKeyDataCollection = "transactionsKeyData"
        private const val SavingsCollection = "savings"
        private const val CategoriesCollection = "categories"

        private const val DeletedField = "deleted"
        private const val LastModifiedField = "lastModified"
        private const val DateField = "date"
        private const val NetField = "net"
        private const val IncomeField = "income"
        private const val ExpenseField = "expense"
        private const val AmountField = "amount"
    }
}