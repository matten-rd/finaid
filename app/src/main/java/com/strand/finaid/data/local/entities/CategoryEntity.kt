package com.strand.finaid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hexCode: String,
    val deleted: Boolean,
    val transactionType: TransactionType
)
