package com.strand.finaid.data.models

import com.strand.finaid.NoArg
import java.util.*

@NoArg
data class SavingsAccount(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bank: String,
    val hexCode: String,
    val amount: Int,
    val lastModified: Date,
    val deleted: Boolean
)

