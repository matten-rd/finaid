package com.strand.finaid.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.strand.finaid.ext.extractProportions

/**
 * A bar chart that animates when loading.
 */
@Composable
fun AnimatedBarchart(
    values: List<Int>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val proportions = remember(values) {
        val percentageProportions = values.extractProportions { it }
        val maxValue = percentageProportions.maxOrNull() ?: 1f
        percentageProportions.map { it / maxValue }
    }

    val currentState = remember(proportions) {
        MutableTransitionState(AnimatedBarchartProgress.START)
            .apply { targetState = AnimatedBarchartProgress.END }
    }

    val transition = updateTransition(transitionState = currentState, label = "")
    val percentAnimate by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 0,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }, label = ""
    ) { state ->
        when(state) {
            AnimatedBarchartProgress.START -> 0f
            AnimatedBarchartProgress.END -> 1f
        }
    }

    Canvas(
        modifier = modifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val numberOfBars = proportions.size
        val cornerRadius = CornerRadius(x = 16f, y = 16f)

        proportions.forEachIndexed { index, percent ->
            val barHeight = ( canvasHeight*percent*percentAnimate )
            val barWidth = ( canvasWidth/(numberOfBars*1.2f + 0.2f) )
            val yOffset = canvasHeight-barHeight
            val xOffset = ( barWidth * (index*1.2f + 0.2f) )
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset(x = xOffset, y = yOffset),
                            size = Size(width = barWidth, height = barHeight)
                        ),
                        topLeft = cornerRadius,
                        topRight = cornerRadius
                    )
                )
            }
            drawPath(path = path, color = colors[index])
        }
    }

}

private enum class AnimatedBarchartProgress { START, END }