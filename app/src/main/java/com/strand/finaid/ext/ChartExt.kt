package com.strand.finaid.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.strand.finaid.ui.transactions.CategoryUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlin.math.absoluteValue

fun <E> List<E>.extractProportions(selector: (E) -> Int): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}

data class ChartState(
    val values: List<Int>,
    val colors: List<Color>
)

@Composable
fun <T> rememberChartState(
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Int
): ChartState {
    val values = remember(items) { items.map { amounts(it) } }
    val allColors = remember(items) { items.map { colors(it) } }

    val valuesByColor = remember(items) {
        (allColors zip values).groupBy { it.first }.mapValues { (_, value) ->
            value.sumOf { it.second.absoluteValue }
        }
    }

    return remember(valuesByColor) {
        ChartState(
            values = valuesByColor.values.toList(),
            colors = valuesByColor.keys.toList()
        )
    }
}

data class TransactionChartState(
    val values: List<Int>,
    val categories: List<CategoryUiState>
)

@Composable
fun rememberTransactionChartState(
    transactions: List<TransactionUiState>
): TransactionChartState {
    val values = remember(transactions) { transactions.map { it.amount } }
    val categories = remember(transactions) { transactions.map { it.category } }

    val valuesByCategory = remember(transactions) {
        (categories zip values).groupBy { it.first }.mapValues { (_, value) ->
            value.sumOf { it.second.absoluteValue }
        }
    }

    return remember(valuesByCategory) {
        TransactionChartState(
            values = valuesByCategory.values.toList(),
            categories = valuesByCategory.keys.toList()
        )
    }
}