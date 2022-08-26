package com.strand.finaid.data.model

import com.strand.finaid.NoArg
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

@NoArg
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val memo: String,
    val amount: Int,
    val category: Category,
    val date: Date,
    val deleted: Boolean
) {
    fun getTransactionType(): TransactionType {
        return if (amount < 0) TransactionType.Expense else TransactionType.Income
    }
}
