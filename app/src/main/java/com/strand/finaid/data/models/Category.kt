package com.strand.finaid.data.models

import androidx.compose.ui.graphics.Color
import com.strand.finaid.ui.transactions.TransactionType
import java.util.*

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Color,
    val deleted: Boolean = false,
    val transactionType: TransactionType
)