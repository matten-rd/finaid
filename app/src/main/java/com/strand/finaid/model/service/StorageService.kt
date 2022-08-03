package com.strand.finaid.model.service

import androidx.paging.PagingData
import com.google.firebase.firestore.QuerySnapshot
import com.strand.finaid.model.Result
import com.strand.finaid.model.data.FirebaseCategory
import com.strand.finaid.model.data.SavingsAccount
import com.strand.finaid.model.data.Transaction
import kotlinx.coroutines.flow.Flow

interface StorageService {
    fun addTransactionsListener(userId: String, deleted: Boolean = false): Flow<Result<QuerySnapshot>>
    fun saveTransaction(userId: String, transaction: Transaction, onResult: (Throwable?) -> Unit)
    fun getTransaction(userId: String, transactionId: String, onError: (Throwable) -> Unit, onSuccess: (Transaction?) -> Unit)
    fun moveTransactionToTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun restoreTransactionFromTrash(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionPermanently(userId: String, transactionId: String, onResult: (Throwable?) -> Unit)
    fun getLimitedNumberOfTransactions(numberOfTransactions: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<Transaction?>) -> Unit)
    fun paginateTransactions(userId: String, pageSize: Int): Flow<PagingData<Transaction>>

    fun addCategoriesListener(userId: String, deleted: Boolean = false): Flow<Result<QuerySnapshot>>
    fun getCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (FirebaseCategory) -> Unit)
    fun addTransactionCategory(userId: String, category: FirebaseCategory, onResult: (Throwable?) -> Unit)
    fun moveTransactionCategoryToTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun deleteTransactionCategoryPermanently(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun getDeletedCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (FirebaseCategory) -> Unit)
    fun restoreCategoryFromTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)

    fun addSavingsListener(userId: String): Flow<Result<QuerySnapshot>>
    fun saveSavingsAccount(userId: String, savingsAccount: SavingsAccount, onResult: (Throwable?) -> Unit)
    fun getSavingsAccount(userId: String, savingsAccountId: String, onError: (Throwable) -> Unit, onSuccess: (SavingsAccount?) -> Unit)
    fun deleteSavingsAccount(userId: String, savingsAccountId: String, onResult: (Throwable?) -> Unit)
    fun getLimitedNumberOfSavingsAccounts(numberOfAccounts: Int, userId: String, onError: (Throwable) -> Unit, onSuccess: (List<SavingsAccount?>) -> Unit)
}