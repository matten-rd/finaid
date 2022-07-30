package com.strand.finaid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun SegmentedButton(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    indexChanged: (Int) -> Unit
) {
    Row(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val buttonModifier = Modifier
                .zIndex(if (selectedIndex == index) 1f else 0f)
                .defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                .weight(1f)

            OutlinedButton(
                modifier = if (index == 0) buttonModifier else buttonModifier.offset((-1*index).dp, 0.dp),
                onClick = { indexChanged(index) },
                shape = when (index) {
                    0 -> RoundedCornerShape( 100, 0, 0, 100)
                    items.size - 1 -> RoundedCornerShape(0, 100, 100, 0)
                    else -> RoundedCornerShape(0)
                },
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                },
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                if (index == selectedIndex) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                }
                Text(text = item)
            }
        }
    }
}