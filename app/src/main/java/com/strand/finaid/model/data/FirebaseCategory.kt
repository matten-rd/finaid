package com.strand.finaid.model.data


import androidx.compose.ui.graphics.Color
import com.strand.finaid.NoArg
import com.strand.finaid.ui.transactions.CategoryUi
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

@NoArg
data class FirebaseCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hexCode: String,
    val deleted: Boolean,
    val transactionType: TransactionType
) {
    fun toCategoryUi(): CategoryUi {
        return CategoryUi(
            id = id,
            name = name,
            color = Color(android.graphics.Color.parseColor("#${hexCode}")),
            deleted = deleted,
            transactionType = transactionType
        )
    }
}