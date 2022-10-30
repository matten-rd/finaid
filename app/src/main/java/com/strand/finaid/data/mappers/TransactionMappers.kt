package com.strand.finaid.data.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.ui.transactions.AddEditTransactionUiState
import com.strand.finaid.ui.transactions.TransactionType
import com.strand.finaid.ui.transactions.TransactionUiState
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.absoluteValue

fun Transaction.asTransactionUiState(): TransactionUiState {
    return TransactionUiState(
        id = id,
        icon = if (amount > 0) Icons.Default.AttachMoney else Icons.Default.MoneyOff,
        color = category.color,
        amount = amount,
        memo = memo,
        category = category.name,
        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    )
}

fun Transaction.asAddEditTransactionUiState(): AddEditTransactionUiState {
    return AddEditTransactionUiState(
        id = id,
        memo = memo,
        amount = amount.absoluteValue.toString(),
        incomeCategory = if (getTransactionType() == TransactionType.Income) category else null,
        expenseCategory = if (getTransactionType() == TransactionType.Expense) category else null,
        date = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    )
}

fun AddEditTransactionUiState.asTransaction(transactionType: TransactionType) : Transaction? {
    val isCategoryOfSelectedTransactionTypeNull =
        (incomeCategory == null && transactionType == TransactionType.Income) ||
                (expenseCategory == null && transactionType == TransactionType.Expense)

    return if (!isCategoryOfSelectedTransactionTypeNull && amount.toIntOrNull() != null && memo.isNotBlank())
        Transaction(
            id = id,
            memo = memo.trim(),
            amount = when (transactionType) {
                TransactionType.Income -> amount.toInt()
                TransactionType.Expense -> amount.toInt() * -1
            },
            category = when (transactionType) {
                TransactionType.Income -> incomeCategory!!
                TransactionType.Expense -> expenseCategory!!
            },
            date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            lastModified = Date.from(Instant.now()),
            deleted = deleted
        )
    else null
}

fun TransactionEntity.asTransaction(): Transaction {
    return Transaction(
        id = id,
        memo = memo,
        amount = amount,
        category = category.asCategory(),
        date = date,
        lastModified = lastModified,
        deleted = deleted
    )
}

fun Transaction.asTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        memo = memo,
        amount = amount,
        category = category.asCategoryEntity(),
        date = date,
        lastModified = lastModified,
        deleted = deleted
    )
}
