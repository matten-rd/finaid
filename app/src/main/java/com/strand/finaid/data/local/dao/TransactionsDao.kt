package com.strand.finaid.data.local.dao

import androidx.room.*
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.ui.transactions.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {

    // GET
    @Query("SELECT * FROM transactions WHERE transaction_deleted = 0 ORDER BY " +
            "CASE WHEN :sortOrder = 'Date' THEN date END DESC," +
            "CASE WHEN :sortOrder = 'Sum' THEN ABS(amount) END DESC," +
            "CASE WHEN :sortOrder = 'Name' THEN memo END ASC"
    )
    fun getTransactionEntitiesStream(sortOrder: SortOrder): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transaction_deleted = 1 ORDER BY date DESC")
    fun getDeletedTransactionEntitiesStream(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transaction_id = :id")
    suspend fun getTransactionEntityById(id: String): TransactionEntity

    @Query("SELECT * FROM transactions WHERE transaction_deleted = 0 ORDER BY date DESC LIMIT :limit")
    suspend fun getLimitedNumberOfTransactionEntities(limit: Int): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE transaction_deleted = 0")
    suspend fun getTransactionSum(): Int

    // INSERT/UPDATE SINGLE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionEntity(entity: TransactionEntity)

    @Update
    suspend fun updateTransactionEntity(entity: TransactionEntity)

    @Query("UPDATE transactions SET transaction_deleted = :deleted WHERE transaction_id = :id")
    suspend fun updateDeletedField(id: String, deleted: Boolean)

    // DELETE
    @Query("DELETE FROM transactions WHERE transaction_id = :id")
    suspend fun deleteTransactionEntityById(id: String)

}