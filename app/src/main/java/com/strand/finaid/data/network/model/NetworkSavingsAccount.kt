package com.strand.finaid.data.network.model

import com.strand.finaid.NoArg
import java.util.*

@NoArg
data class NetworkSavingsAccount(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bank: String,
    val hexCode: String,
    val amount: Int,
    val lastModified: Date,
    val deleted: Boolean
)
