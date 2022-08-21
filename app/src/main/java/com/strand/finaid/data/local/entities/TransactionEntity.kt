package com.strand.finaid.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strand.finaid.data.network.models.NetworkCategory
import java.util.*

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "transaction_id")
    val id: String = UUID.randomUUID().toString(),
    val memo: String,
    val amount: Int,
    @Embedded
    val category: NetworkCategory,
    val date: Date,
    val lastModified: Date,
    @ColumnInfo(name = "transaction_deleted")
    val deleted: Boolean
)