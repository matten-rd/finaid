package com.strand.finaid.data.local.dao

import androidx.room.*
import com.strand.finaid.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {

    // GET
    @Query("SELECT * FROM categories WHERE deleted = 0 ORDER BY name DESC")
    fun getCategoryEntitiesStream(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE deleted = 1 ORDER BY name DESC")
    fun getDeletedCategoryEntitiesStream(): Flow<List<CategoryEntity>>

    // INSERT/UPDATE SINGLE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryEntity(entity: CategoryEntity)

    @Update
    suspend fun updateCategoryEntity(entity: CategoryEntity)

    @Query("UPDATE categories SET deleted = :deleted WHERE id = :id")
    suspend fun updateDeletedField(id: String, deleted: Boolean)

    @Query("UPDATE transactions SET category_name = :name, category_hexCode = :hexCode WHERE category_id = :id")
    suspend fun updateTransactionsWithNewCategory(id: String, name: String, hexCode: String)

    // DELETE
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteSCategoryEntityById(id: String)

}