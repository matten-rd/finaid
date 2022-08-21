package com.strand.finaid.data.local.dao

import androidx.room.*
import com.strand.finaid.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {

    // GET
    @Query("SELECT * FROM transactions WHERE transaction_deleted = 0 ORDER BY date DESC")
    fun getTransactionEntitiesStream(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transaction_id = :id")
    suspend fun getTransactionEntityById(id: String): TransactionEntity

    @Query("SELECT * FROM transactions WHERE transaction_deleted = 0 ORDER BY date DESC LIMIT :limit")
    suspend fun getLimitedNumberOfTransactionEntities(limit: Int): List<TransactionEntity>

    // INSERT/UPDATE SINGLE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTransactionEntity(entity: TransactionEntity): Long

    @Update
    suspend fun updateTransactionEntity(entity: TransactionEntity)

    @Transaction
    suspend fun upsertTransactionEntity(entity: TransactionEntity) = upsert(
        item = entity,
        insert = ::insertOrIgnoreTransactionEntity,
        update = ::updateTransactionEntity
    )

    // INSERT/UPDATE MANY
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTransactionEntities(transactionEntities: List<TransactionEntity>): List<Long>

    @Update
    suspend fun updateTransactionEntities(entities: List<TransactionEntity>)

    @Transaction
    suspend fun upsertTransactionEntities(entities: List<TransactionEntity>) = upsertMany(
        items = entities,
        insertMany = ::insertOrIgnoreTransactionEntities,
        updateMany = ::updateTransactionEntities
    )

    // DELETE
    @Delete
    suspend fun deleteTransactionEntity(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE transaction_id = :id")
    suspend fun deleteTransactionEntityById(id: String)

}