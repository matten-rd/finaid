package com.strand.finaid.data.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.models.NetworkCategory

fun Category.asNetworkCategory(): NetworkCategory {
    return NetworkCategory(
        id = id,
        name = name,
        hexCode = String.format("%06X", color.toArgb() and 0xFFFFFF),
        deleted = deleted,
        transactionType = transactionType
    )
}

fun NetworkCategory.asCategory(): Category {
    return Category(
        id = id,
        name = name,
        color = Color(android.graphics.Color.parseColor("#${hexCode}")),
        deleted = deleted,
        transactionType = transactionType
    )
}