package com.strand.finaid.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

fun <E> List<E>.extractProportions(selector: (E) -> Int): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}

data class ChartState(
    val floatProportions: List<Float>,
    val percentageProportions: List<Float>,
    val colors: List<Color>
)

@Composable
fun <T> rememberChartState(
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Int
): ChartState {
    val proportions = remember(items) { items.extractProportions { amounts(it).absoluteValue } }
    val allColors = remember(items) { items.map { colors(it) } }
    val uniqueColors = remember(allColors) { allColors.distinct() }

    // Used for circle chart
    val floatProportions = remember(proportions, allColors) {
        (allColors zip proportions).groupBy { it.first }.mapKeys { entry ->
            entry.value.sumOf { it.second.toDouble() }
        }.keys.toList().map { it.toFloat() }
    }
    // Used for bar chart
    val percentageProportions = remember(floatProportions) {
        val maxValue = floatProportions.maxOrNull()!!
        floatProportions.map { it / maxValue }
    }

    return remember(floatProportions, percentageProportions, uniqueColors) {
        ChartState(
            floatProportions = floatProportions,
            percentageProportions = percentageProportions,
            colors = uniqueColors
        )
    }

}