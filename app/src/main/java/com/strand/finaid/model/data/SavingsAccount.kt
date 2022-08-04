package com.strand.finaid.model.data

import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color
import com.strand.finaid.NoArg
import com.strand.finaid.ui.savings.AddEditSavingsAccountUiState
import com.strand.finaid.ui.savings.SavingsAccountUiState
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
) {
    fun toSavingsAccountUiState(): SavingsAccountUiState {
        return SavingsAccountUiState(
            id = id,
            color = Color(parseColor("#$hexCode")),
            amount = amount,
            name = name,
            bank = bank
        )
    }

    fun toAddEditSavingsAccountUiState(): AddEditSavingsAccountUiState {
        return AddEditSavingsAccountUiState(
            id = id,
            color = Color(parseColor("#$hexCode")),
            amount = amount.toString(),
            name = name,
            bank = bank
        )
    }
}

