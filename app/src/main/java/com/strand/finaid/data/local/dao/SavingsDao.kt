package com.strand.finaid.data.local.dao

import androidx.room.*
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    // GET
    @Query("SELECT * FROM savings_accounts WHERE deleted = 0 ORDER BY hexCode DESC")
    fun getSavingsAccountEntitiesStream(): Flow<List<SavingsAccountEntity>>

    @Query("SELECT * FROM savings_accounts WHERE deleted = 1 ORDER BY name DESC")
    fun getDeletedSavingsAccountEntitiesStream(): Flow<List<SavingsAccountEntity>>

    @Query("SELECT * FROM savings_accounts WHERE id = :id")
    suspend fun getSavingsAccountEntityById(id: String): SavingsAccountEntity

    @Query("SELECT * FROM savings_accounts WHERE deleted = 0 ORDER BY name DESC LIMIT :limit")
    suspend fun getLimitedNumberOfSavingsAccountEntities(limit: Int): List<SavingsAccountEntity>

    // INSERT/UPDATE SINGLE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccountEntity(entity: SavingsAccountEntity)

    @Update
    suspend fun updateSavingsAccountEntity(entity: SavingsAccountEntity)

    @Query("UPDATE savings_accounts SET deleted = :deleted WHERE id = :id")
    suspend fun updateDeletedField(id: String, deleted: Boolean)

    // DELETE
    @Delete
    suspend fun deleteSavingsAccountEntity(entity: SavingsAccountEntity)

    @Query("DELETE FROM savings_accounts WHERE id = :id")
    suspend fun deleteSavingsAccountEntityById(id: String)

}