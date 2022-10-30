package com.strand.finaid.data.repository.impl

import com.strand.finaid.data.local.dao.CategoriesDao
import com.strand.finaid.data.local.entities.CategoryEntity
import com.strand.finaid.data.mappers.asCategory
import com.strand.finaid.data.mappers.asCategoryEntity
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesDao: CategoriesDao
) : CategoriesRepository {

    override fun getCategoriesStream(): Flow<List<Category>> =
        categoriesDao.getCategoryEntitiesStream()
            .map { it.map(CategoryEntity::asCategory) }

    override fun getDeletedCategoriesStream(): Flow<List<Category>> =
        categoriesDao.getDeletedCategoryEntitiesStream()
            .map { it.map(CategoryEntity::asCategory) }

    override suspend fun addCategory(category: Category) =
        categoriesDao.insertCategoryEntity(category.asCategoryEntity())

    override suspend fun moveCategoryToTrash(categoryId: String) =
        categoriesDao.updateDeletedField(id = categoryId, deleted = true)

    override suspend fun restoreCategoryFromTrash(categoryId: String) =
        categoriesDao.updateDeletedField(id = categoryId, deleted = false)

    override suspend fun deleteCategoryPermanently(categoryId: String) =
        categoriesDao.deleteSCategoryEntityById(id = categoryId)

    override suspend fun updateTransactionsWithNewCategory(id: String, name: String, hexCode: String) =
        categoriesDao.updateTransactionsWithNewCategory(id = id, name = name, hexCode = hexCode)

}