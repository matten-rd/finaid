package com.strand.finaid.data.network.model

import com.strand.finaid.NoArg
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

@NoArg
data class NetworkCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hexCode: String,
    val deleted: Boolean,
    val transactionType: TransactionType
)
