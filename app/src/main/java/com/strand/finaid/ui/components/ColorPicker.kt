package com.strand.finaid.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun ColorPicker(
    modifier: Modifier = Modifier,
    items: List<Color>,
    selectedColor: Color?,
    onColorSelected: (Color) -> Unit,
    disabledColors: List<Color> = emptyList()
) {
    Column(modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = 5
        ) {
            items.distinct().forEach { color ->
                ColorItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    disabled = color in disabledColors,
                    selected = color == selectedColor,
                    color = color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorItem(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val boxBackgroundColor: Color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.inverseOnSurface
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .size(48.dp)
            .background(boxBackgroundColor)
            .clickable(onClick = onClick)
            .alpha(if (disabled) 0.25f else 1f),
        contentAlignment = Alignment.Center
    ) {
        val circleSize: Dp by animateDpAsState(if (selected) 36.dp else 24.dp)
        val circleModifier = Modifier
            .clip(CircleShape)
            .size(circleSize)
            .background(color)

        Box(modifier = circleModifier) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    contentDescription = null
                )
            }
        }
    }
}