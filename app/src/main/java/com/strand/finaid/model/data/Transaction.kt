package com.strand.finaid.model.data

import android.graphics.Color.parseColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.Exclude
import com.strand.finaid.NoArg
import com.strand.finaid.ui.transactions.AddEditTransactionUiState
import com.strand.finaid.ui.transactions.TransactionType
import com.strand.finaid.ui.transactions.TransactionUiState
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.math.absoluteValue

@NoArg
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val memo: String,
    val amount: Int,
    val category: FirebaseCategory,
    val date: Date,
    val lastModified: Date,
    val deleted: Boolean
) {
    fun toTransactionUiState(): TransactionUiState {
        return TransactionUiState(
            id = id,
            icon = if (amount > 0) Icons.Default.AttachMoney else Icons.Default.MoneyOff,
            color = Color(parseColor("#${category.hexCode}")),
            amount = amount,
            memo = memo,
            category = category.name,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        )
    }

    fun toEditTransactionUiState(): AddEditTransactionUiState {
        return AddEditTransactionUiState(
            id = id,
            memo = memo,
            amount = amount.absoluteValue.toString(),
            incomeCategory = if (getTransactionType() == TransactionType.Income) category.toCategoryUi() else null,
            expenseCategory = if (getTransactionType() == TransactionType.Expense) category.toCategoryUi() else null,
            date = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        )
    }

    @Exclude
    fun getTransactionType(): TransactionType {
        return if (amount < 0) TransactionType.Expense else TransactionType.Income
    }
}
