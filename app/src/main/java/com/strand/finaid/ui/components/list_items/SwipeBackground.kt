package com.strand.finaid.ui.components.list_items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
internal fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val startBackgroundColor = MaterialTheme.colorScheme.primary
    val startContentColor = contentColorFor(backgroundColor = startBackgroundColor)
    val endBackgroundColor = MaterialTheme.colorScheme.error
    val endContentColor = contentColorFor(backgroundColor = endBackgroundColor)
    
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.ContentCopy
        DismissDirection.EndToStart -> Icons.Default.Delete
    }

    val iconTint = when (direction) {
        DismissDirection.StartToEnd -> startContentColor
        DismissDirection.EndToStart -> endContentColor
    }
    
    val iconScale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    val backgroundColor = when (direction) {
        DismissDirection.StartToEnd -> startBackgroundColor
        DismissDirection.EndToStart -> endBackgroundColor
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawRect(backgroundColor) }
            .padding(horizontal = 16.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, 
            modifier = Modifier.scale(iconScale),
            tint = iconTint
        )
    }
}
