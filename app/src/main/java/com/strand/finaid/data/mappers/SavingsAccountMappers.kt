package com.strand.finaid.data.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.strand.finaid.data.models.SavingsAccount
import com.strand.finaid.ui.savings.AddEditSavingsAccountUiState
import com.strand.finaid.ui.savings.SavingsAccountUiState
import java.time.Instant
import java.util.*

fun SavingsAccount.asSavingsAccountUiState(): SavingsAccountUiState {
    return SavingsAccountUiState(
        id = id,
        color = Color(android.graphics.Color.parseColor("#$hexCode")),
        amount = amount,
        name = name,
        bank = bank
    )
}

fun SavingsAccount.asAddEditSavingsAccountUiState(): AddEditSavingsAccountUiState {
    return AddEditSavingsAccountUiState(
        id = id,
        color = Color(android.graphics.Color.parseColor("#$hexCode")),
        amount = amount.toString(),
        name = name,
        bank = bank
    )
}

fun AddEditSavingsAccountUiState.asSavingsAccount(): SavingsAccount? {
    return if (color != null && amount.toIntOrNull() != null)
        SavingsAccount(
            id = id,
            name = name,
            bank = bank,
            hexCode = String.format("%06X", color.toArgb() and 0xFFFFFF),
            amount = amount.toInt(),
            lastModified = Date.from(Instant.now()),
            deleted = deleted
        )
    else null
}