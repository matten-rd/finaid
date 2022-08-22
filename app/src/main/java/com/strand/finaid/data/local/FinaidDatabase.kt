package com.strand.finaid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.strand.finaid.data.local.dao.SavingsDao
import com.strand.finaid.data.local.dao.TransactionsDao
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.local.typeconverters.DateTimeTypeConverter

@Database(entities = [TransactionEntity::class, SavingsAccountEntity::class], version = 2)
@TypeConverters(DateTimeTypeConverter::class)
abstract class FinaidDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
    abstract fun savingsDao(): SavingsDao
}