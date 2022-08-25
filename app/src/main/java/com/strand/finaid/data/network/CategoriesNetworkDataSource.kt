package com.strand.finaid.data.network

import com.google.firebase.firestore.QuerySnapshot
import com.strand.finaid.data.Result
import com.strand.finaid.data.network.models.NetworkCategory
import kotlinx.coroutines.flow.Flow

interface CategoriesNetworkDataSource {
    fun addCategoriesListener(userId: String, deleted: Boolean = false): Flow<Result<QuerySnapshot>>
    fun getCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkCategory) -> Unit)
    fun addCategory(userId: String, category: NetworkCategory, onResult: (Throwable?) -> Unit)
    fun moveCategoryToTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun deleteCategoryPermanently(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun getDeletedCategories(userId: String, onError: (Throwable) -> Unit, onSuccess: (NetworkCategory) -> Unit)
    fun restoreCategoryFromTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
}