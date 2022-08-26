package com.strand.finaid.data.repository

import com.strand.finaid.data.Result
import com.strand.finaid.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesStream(userId: String): Flow<Result<List<Category>>>
    fun getDeletedCategoriesStream(userId: String): Flow<Result<List<Category>>>
    fun saveCategory(userId: String, category: Category, onResult: (Throwable?) -> Unit)
    fun moveCategoryToTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun restoreCategoryFromTrash(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
    fun deleteCategoryPermanently(userId: String, categoryId: String, onResult: (Throwable?) -> Unit)
}