package com.strand.finaid.domain

import androidx.compose.ui.graphics.Color
import com.strand.finaid.ui.theme.*
import javax.inject.Inject

data class TransactionColors(
    val incomeColors: List<Color>,
    val expenseColors: List<Color>
)

class GetTransactionColorsUseCase @Inject constructor() {
    operator fun invoke(): TransactionColors {
        val incomeColors = listOf(
            Income1, Income2, Income3, Income4, Income5,
            Income6, Income7, Income8, Income9, Income10
        )

        val expenseColors = listOf(
            Expense1, Expense2, Expense3, Expense4, Expense5,
            Expense6, Expense7, Expense8, Expense9, Expense10
        )

        return TransactionColors(incomeColors = incomeColors, expenseColors = expenseColors)
    }
}