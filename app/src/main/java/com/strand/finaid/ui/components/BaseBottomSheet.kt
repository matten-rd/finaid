package com.strand.finaid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BaseBottomSheet(
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Divider(
            thickness = 4.dp,
            modifier = Modifier
                .width(32.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .alpha(0.4f)
        )
        if (title != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
        }
        content()
    }
}