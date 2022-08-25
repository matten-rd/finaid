package com.strand.finaid.data.local.dao

import androidx.room.*
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SavingsDao {

    // GET
    @Query("SELECT * FROM savings_accounts WHERE deleted = 0 ORDER BY name DESC")
    fun getSavingsAccountEntitiesStream(): Flow<List<SavingsAccountEntity>>

    @Query("SELECT * FROM savings_accounts WHERE deleted = 1 ORDER BY name DESC")
    fun getDeletedSavingsAccountEntitiesStream(): Flow<List<SavingsAccountEntity>>

    @Query("SELECT * FROM savings_accounts WHERE id = :id")
    suspend fun getSavingsAccountEntityById(id: String): SavingsAccountEntity

    @Query("SELECT * FROM savings_accounts WHERE deleted = 0 ORDER BY name DESC LIMIT :limit")
    suspend fun getLimitedNumberOfSavingsAccountEntities(limit: Int): List<SavingsAccountEntity>

    @Query("SELECT MAX(lastModified) FROM savings_accounts")
    suspend fun getLastModifiedDate(): Date

    // INSERT/UPDATE SINGLE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreSavingsAccountEntity(entity: SavingsAccountEntity): Long

    @Update
    suspend fun updateSavingsAccountEntity(entity: SavingsAccountEntity)

    @Transaction
    suspend fun upsertSavingsAccountEntity(entity: SavingsAccountEntity) = upsert(
        item = entity,
        insert = ::insertOrIgnoreSavingsAccountEntity,
        update = ::updateSavingsAccountEntity
    )

    // DELETE
    @Delete
    suspend fun deleteSavingsAccountEntity(entity: SavingsAccountEntity)

    @Query("DELETE FROM savings_accounts WHERE id = :id")
    suspend fun deleteSavingsAccountEntityById(id: String)

}