package com.strand.finaid.data.network.impl

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.mappers.asTransactionEntity
import com.strand.finaid.data.network.TransactionsNetworkDataSource
import com.strand.finaid.data.network.models.NetworkTransaction
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.inject.Inject

class TransactionsNetworkDataSourceImpl @Inject constructor() : TransactionsNetworkDataSource {

    private val Date.yearMonthFormat: String
        get() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(this)

    private val String.userDocument: DocumentReference
        get() = Firebase.firestore.collection(UsersCollection).document(this)

    private var listenerRegistration: ListenerRegistration? = null

    override fun addTransactionsListener(
        userId: String,
        deleted: Boolean,
        onDocumentEvent: (Boolean, TransactionEntity) -> Unit
    ) {
        val query = userId.userDocument
            .collection(TransactionsCollection)
            .whereGreaterThanOrEqualTo(LastModifiedField, Date.from(Instant.parse("2018-11-30T18:35:24.00Z")))

        listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener

            snapshot?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == DocumentChange.Type.REMOVED
                val transaction = it.document.toObject<NetworkTransaction>()
                println(transaction.toString())
                onDocumentEvent(wasDocumentDeleted, transaction.asTransactionEntity())
            }
        }
    }

    override fun removeTransactionsListener() {
        listenerRegistration?.remove()
    }

    override fun saveTransaction(
        userId: String,
        transaction: NetworkTransaction,
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
                val prevTransactionYearMonth = prevTransactionSnapshot.getDate(DateField)!!.yearMonthFormat
                val prevMonthKeyDataDocRef = transactionKeyDataCollectionRef.document(prevTransactionYearMonth)
                transactionRun.get(prevMonthKeyDataDocRef).reference
            } else null

            // Get previous amount and categoryId
            val prevTransaction = if (prevTransactionSnapshot.exists()) prevTransactionSnapshot.toObject<NetworkTransaction>() else null
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
                    NetField to FieldValue.increment(-prevAmount),
                    ExpenseField to FieldValue.increment(if (prevAmount < 0) -prevAmount else 0.0),
                    IncomeField to FieldValue.increment(if (prevAmount >= 0) -prevAmount else 0.0)
                ) + if (prevCategoryId != null) mapOf(prevCategoryId to FieldValue.increment(-prevAmount)) else emptyMap(),
                    SetOptions.merge()
                )
            }

            // NEW MONTH - add the new/updated transaction
            transactionRun.set(newMonthKeyDataDocRef, mapOf(
                NetField to FieldValue.increment(newAmount),
                ExpenseField to FieldValue.increment(if (newAmount < 0) newAmount else 0.0),
                IncomeField to FieldValue.increment(if (newAmount >= 0) newAmount else 0.0),
                newCategoryId to FieldValue.increment(newAmount)
            ), SetOptions.merge())

            // ALL TIME
            // Determine the all time income and expense updates
            val allTimeUpdates = when {
                prevAmount >= 0 && newAmount >= 0 ->
                    mapOf(IncomeField to FieldValue.increment(newAmount - prevAmount))

                prevAmount >= 0 && newAmount < 0 ->
                    mapOf(IncomeField to FieldValue.increment(-prevAmount), ExpenseField to FieldValue.increment(newAmount))

                prevAmount < 0 && newAmount >= 0 ->
                    mapOf(IncomeField to FieldValue.increment(newAmount), ExpenseField to FieldValue.increment(-prevAmount))

                prevAmount < 0 && newAmount < 0 ->
                    mapOf(ExpenseField to FieldValue.increment(newAmount - prevAmount))

                else -> emptyMap()
            }
            // Update all time key data
            if (newCategoryId == prevCategoryId) {
                transactionRun.set(
                    allTimeKeyDataDocRef,
                    mapOf(
                        NetField to FieldValue.increment(newAmount - prevAmount),
                        newCategoryId to FieldValue.increment(newAmount - prevAmount)
                    ) + allTimeUpdates,
                    SetOptions.merge()
                )
            } else {
                transactionRun.set(
                    allTimeKeyDataDocRef,
                    mapOf(
                        NetField to FieldValue.increment(newAmount - prevAmount),
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

    override fun moveTransactionToTrash(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        val transactionDocRef = userId.userDocument.collection(TransactionsCollection).document(transactionId)
        val transactionKeyDataCollectionRef = userId.userDocument.collection(TransactionsKeyDataCollection)
        val allTimeKeyDataDocRef = transactionKeyDataCollectionRef.document("allTime")

        Firebase.firestore.runTransaction { transactionRun ->
            // Get transaction
            val transactionSnapshot = transactionRun.get(transactionDocRef)
            val transaction = transactionSnapshot.toObject<NetworkTransaction>()!!

            val transactionYearMonth = transaction.date.yearMonthFormat
            val monthKeyDataDocRef = transactionKeyDataCollectionRef.document(transactionYearMonth)
            val monthKeyDataReference = transactionRun.get(monthKeyDataDocRef).reference

            // Get amount and categoryId
            val amount = transaction.amount.toDouble()
            val categoryId = transaction.category.id

            val updates = mapOf(
                NetField to FieldValue.increment(-amount),
                ExpenseField to FieldValue.increment(if (amount < 0) -amount else 0.0),
                IncomeField to FieldValue.increment(if (amount >= 0) -amount else 0.0),
                categoryId to FieldValue.increment(-amount)
            )
            // MONTH
            transactionRun.set(monthKeyDataReference, updates, SetOptions.merge())
            // ALL TIME
            transactionRun.set(allTimeKeyDataDocRef, updates, SetOptions.merge())
            // Update the transaction document
            val transactionUpdates = mapOf(
                DeletedField to true,
                LastModifiedField to Date.from(Instant.now())
            )
            transactionRun.update(transactionDocRef, transactionUpdates)
            null
        }.addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun restoreTransactionFromTrash(
        userId: String,
        transactionId: String,
        onResult: (Throwable?) -> Unit
    ) {
        val transactionDocRef = userId.userDocument.collection(TransactionsCollection).document(transactionId)
        val transactionKeyDataCollectionRef = userId.userDocument.collection(TransactionsKeyDataCollection)
        val allTimeKeyDataDocRef = transactionKeyDataCollectionRef.document("allTime")

        Firebase.firestore.runTransaction { transactionRun ->
            // Get transaction
            val transactionSnapshot = transactionRun.get(transactionDocRef)
            val transaction = transactionSnapshot.toObject<NetworkTransaction>()!!

            val transactionYearMonth = transaction.date.yearMonthFormat
            val monthKeyDataDocRef = transactionKeyDataCollectionRef.document(transactionYearMonth)
            val monthKeyDataReference = transactionRun.get(monthKeyDataDocRef).reference

            // Get amount and categoryId
            val amount = transaction.amount.toDouble()
            val categoryId = transaction.category.id

            val updates = mapOf(
                NetField to FieldValue.increment(amount),
                ExpenseField to FieldValue.increment(if (amount < 0) amount else 0.0),
                IncomeField to FieldValue.increment(if (amount >= 0) amount else 0.0),
                categoryId to FieldValue.increment(amount)
            )
            // MONTH
            transactionRun.set(monthKeyDataReference, updates, SetOptions.merge())
            // ALL TIME
            transactionRun.set(allTimeKeyDataDocRef, updates, SetOptions.merge())
            // Update the transaction document
            val transactionUpdates = mapOf(
                DeletedField to false,
                LastModifiedField to Date.from(Instant.now())
            )
            transactionRun.update(transactionDocRef, transactionUpdates)
            null
        }.addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit) {
        userId.userDocument
            .collection(TransactionsCollection)
            .document(transactionId)
            .delete()
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    companion object {
        private const val UsersCollection = "users"
        private const val TransactionsCollection = "transactions"
        private const val TransactionsKeyDataCollection = "transactionsKeyData"

        private const val DeletedField = "deleted"
        private const val LastModifiedField = "lastModified"
        private const val DateField = "date"
        private const val NetField = "net"
        private const val IncomeField = "income"
        private const val ExpenseField = "expense"
    }
}