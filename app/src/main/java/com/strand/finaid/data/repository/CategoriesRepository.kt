package com.strand.finaid.data.repository

import com.strand.finaid.data.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesStream(): Flow<List<Category>>
    fun getIncomeCategoriesStream(): Flow<List<Category>>
    fun getExpenseCategoriesStream(): Flow<List<Category>>
    fun getDeletedCategoriesStream(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun moveCategoryToTrash(categoryId: String)
    suspend fun restoreCategoryFromTrash(categoryId: String)
    suspend fun deleteCategoryPermanently(categoryId: String)
    suspend fun updateTransactionsWithNewCategory(id: String, name: String, hexCode: String)
}