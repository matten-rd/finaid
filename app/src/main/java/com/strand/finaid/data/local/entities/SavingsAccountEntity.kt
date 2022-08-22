package com.strand.finaid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "savings_accounts")
data class SavingsAccountEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bank: String,
    val hexCode: String,
    val amount: Int,
    val lastModified: Date,
    val deleted: Boolean
)
