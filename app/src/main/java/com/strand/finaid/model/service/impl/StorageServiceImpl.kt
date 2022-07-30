package com.strand.finaid.model.service.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.strand.finaid.model.Result
import com.strand.finaid.model.data.FirebaseCategory
import com.strand.finaid.model.data.SavingsAccount
import com.strand.finaid.model.data.Transaction
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.model.service.repository.TransactionsPagingSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
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
     * Transactions
     */
    override fun addTransactionsListener(userId: String, deleted: Boolean): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(TransactionsCollection)
            .whereEqualTo(DeletedField, deleted)

        return query.snapshotFlow()
    }

    override fun saveTransaction(
        userId: String,
        transaction: Transaction,
        onResult: (Throwable?) -> Unit
    ) {
        val transactionDocRef = userId.userDocument.collection(TransactionsCollection).document(transaction.id)
        val transactionKeyDataCollectionRef = userId.userDocument.collection(TransactionsKeyDataCollection)
        val allTimeKeyDataDocRef = transactionKeyDataCollectionRef.document("allTime")
        val newMonthKeyDataDocRef = transactionKeyDataCollectionRef.document(transaction.date.yearMonthFormat)

        Firebase.firestore.runTransaction { transactionRun ->
            // Get previous transaction if update
            val prevTransactionSnapshot = transactionRun.get(transactionDocRef)

            val prevMonthKeyDataReference = if (prevTransactionSnapshot.exists()) {
                val prevTransactionYearMonth = prevTransactionSnapshot.getDate("date")!!.yearMonthFormat
                val prevMonthKeyDataDocRef = transactionKeyDataCollectionRef.document(prevTransactionYearMonth)
                transactionRun.get(prevMonthKeyDataDocRef).reference
            } else null

            // Get previous amount and categoryId
            val prevTransaction = if (prevTransactionSnapshot.exists()) prevTransactionSnapshot.toObject<Transaction>() else null
            val prevAmount = prevTransaction?.amount?.toDouble() ?: 0.0
            val prevCategoryId = prevTransaction?.category?.id

            // Get new amount and categoryId
            val newAmount = transaction.amount.toDouble()
            val newCategoryId = transaction.category.id

            // NOTE FOR BELOW CODE LOGIC
            // We should always subtract prevAmount and add newAmount
            // And most often prevMonth will be the same as newMonth

            // PREV MONTH - remove the current transaction if update
            if (prevMonthKeyDataReference != null) {
                transactionRun.set(prevMonthKeyDataReference, mapOf(
                    "net" to FieldValue.increment(-prevAmount),
                    "expense" to FieldValue.increment(if (prevAmount < 0) -prevAmount else 0.0),
                    "income" to FieldValue.increment(if (prevAmount >= 0) -prevAmount else 0.0)
                ) + if (prevCategoryId != null) mapOf(prevCategoryId to FieldValue.increment(-prevAmount)) else emptyMap(),
                    SetOptions.merge()
                )
            }

            // NEW MONTH - add the new/updated transaction
            transactionRun.set(newMonthKeyDataDocRef, mapOf(
                "net" to FieldValue.increment(newAmount),
                "expense" to FieldValue.increment(if (newAmount < 0) newAmount else 0.0),
                "income" to FieldValue.increment(if (newAmount >= 0) newAmount else 0.0),
                newCategoryId to FieldValue.increment(newAmount)
            ), SetOptions.merge())

            // ALL TIME
            // Determine the all time income and expense updates
            val allTimeUpdates = when {
                prevAmount >= 0 && newAmount >= 0 ->
                    mapOf("income" to FieldValue.increment(newAmount - prevAmount))

                prevAmount >= 0 && newAmount < 0 ->
                    mapOf("income" to FieldValue.increment(-prevAmount), "expense" to FieldValue.increment(newAmount))

                prevAmount < 0 && newAmount >= 0 ->
                    mapOf("income" to FieldValue.increment(newAmount), "expense" to FieldValue.increment(-prevAmount))

                prevAmount < 0 && newAmount < 0 ->
                    mapOf("expense" to FieldValue.increment(newAmount - prevAmount))

                else -> emptyMap()
            }
            // Update all time key data
            if (newCategoryId == prevCategoryId) {
                transactionRun.set(
                    allTimeKeyDataDocRef,
                    mapOf(
                        "net" to FieldValue.increment(newAmount - prevAmount),
                        newCategoryId to FieldValue.increment(newAmount - prevAmount)
                    ) + allTimeUpdates,
                    SetOptions.merge()
                )
            } else {
                transactionRun.set(
                    allTimeKeyDataDocRef,
                    mapOf(
                        "net" to FieldValue.increment(newAmount - prevAmount),
                        newCategoryId to FieldValue.increment(newAmount)
                    ) + allTimeUpdates
                            + if (prevCategoryId != null) mapOf(prevCategoryId to FieldValue.increment(-prevAmount)) else emptyMap(),
                    SetOptions.merge()
                )
            }

            // Set the transaction document
            transactionRun.set(transactionDocRef, transaction)
            null
        }.addOnCompleteListener { task -> onResult(task.exception) }

    }

    override fun getTransaction(
        userId: String,
        transactionId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Transaction?) -> Unit
    ) {
        userId.userDocument
            .collection(TransactionsCollection)
            .document(transactionId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObject()) }
    }

    override fun moveTransactionToTrash(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        // TODO: Update transactionsKeyData allTime and current month
        userId.userDocument
            .collection(TransactionsCollection)
            .document(transactionId)
            .update(DeletedField, true)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit) {
        // TODO: Update transactionsKeyData allTime and current month
        userId.userDocument
            .collection(TransactionsCollection)
            .document(transactionId)
            .delete()
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun getLimitedNumberOfTransactions(
        numberOfTransactions: Int,
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<Transaction?>) -> Unit
    ) {
        userId.userDocument
            .collection(TransactionsCollection)
            .orderBy("date", Query.Direction.DESCENDING)
            .whereEqualTo(DeletedField, false)
            .limit(numberOfTransactions.toLong())
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObjects()) }
    }

    override fun paginateTransactions(userId: String, pageSize: Int): Flow<PagingData<Transaction>> {
        val query = userId.userDocument
            .collection(TransactionsCollection)
            .limit(pageSize.toLong())

        return Pager(
            PagingConfig(pageSize = pageSize)
        ) {
            TransactionsPagingSource(query)
        }.flow
    }

    override fun addCategoriesListener(userId: String, deleted: Boolean): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(CategoriesCollection)
            .whereEqualTo(DeletedField, deleted)

        return query.snapshotFlow()
    }

    /**
     * Categories
     */
    override fun getCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (FirebaseCategory) -> Unit) {
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
        category: FirebaseCategory,
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
        onSuccess: (FirebaseCategory) -> Unit
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
    override fun addSavingsListener(userId: String): Flow<Result<QuerySnapshot>> {
        val query = userId.userDocument
            .collection(SavingsCollection)
            .orderBy("bank")

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

    override fun deleteSavingsAccount(
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
            .orderBy("amount")
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
    }
}