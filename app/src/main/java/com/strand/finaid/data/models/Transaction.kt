package com.strand.finaid.data.models

import com.google.firebase.firestore.Exclude
import com.strand.finaid.NoArg
import com.strand.finaid.data.network.models.NetworkCategory
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

@NoArg
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val memo: String,
    val amount: Int,
    val category: NetworkCategory,
    val date: Date,
    val lastModified: Date,
    val deleted: Boolean
) {
    @Exclude
    fun getTransactionType(): TransactionType {
        return if (amount < 0) TransactionType.Expense else TransactionType.Income
    }
}
