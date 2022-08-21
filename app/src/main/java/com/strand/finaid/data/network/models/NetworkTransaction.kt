package com.strand.finaid.data.network.models

import com.strand.finaid.NoArg
import java.util.*

@NoArg
data class NetworkTransaction(
    val id: String = UUID.randomUUID().toString(),
    val memo: String,
    val amount: Int,
    val category: NetworkCategory,
    val date: Date,
    val lastModified: Date,
    val deleted: Boolean
)
