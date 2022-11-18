package com.strand.finaid.ui.components.list_items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class RevealValue {
    Default, Start, End
}

@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default
): SwipeableState<RevealValue> {
    return rememberSwipeableState(initialValue = initialValue)
}

suspend fun SwipeableState<RevealValue>.reset() {
    animateTo(targetValue = RevealValue.Default)
}

@Composable
internal fun RevealSwipe(
    modifier: Modifier = Modifier,
    revealState: SwipeableState<RevealValue> = rememberRevealState(),
    startBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    startContentColor: Color = contentColorFor(backgroundColor = startBackgroundColor),
    endBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    endContentColor: Color = contentColorFor(backgroundColor = endBackgroundColor),
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    hiddenIconEnd: ImageVector,
    hiddenIconStart: ImageVector,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sizePx = with(LocalDensity.current) { 80.dp.toPx() }
    val anchors = mapOf(0f to RevealValue.Default, -sizePx to RevealValue.End, sizePx to RevealValue.Start)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .swipeable(
                state = revealState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
    ) {
        // Start hidden content
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .drawBehind { drawRect(startBackgroundColor) }
                .clickable {
                    onStartClick()
                    scope.launch { revealState.reset() }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier.padding(start = 28.dp),
                imageVector = hiddenIconStart,
                contentDescription = null,
                tint = startContentColor,
            )
        }

        // End hidden content
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .drawBehind { drawRect(endBackgroundColor) }
                .clickable {
                    onEndClick()
                    scope.launch { revealState.reset() }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                modifier = Modifier.padding(end = 28.dp),
                imageVector = hiddenIconEnd,
                contentDescription = null,
                tint = endContentColor,
            )
        }

        // Main content
        Box(modifier = modifier.offset { IntOffset(revealState.offset.value.roundToInt(), 0) }) {
            content()
        }
    }
}
