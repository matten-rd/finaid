package com.strand.finaid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseBottomSheet(
    title: String,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
//        Divider(
//            thickness = 4.dp,
//            modifier = Modifier
//                .width(48.dp)
//                .align(Alignment.CenterHorizontally)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.onSurface)
//        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
            }
        }
//        Divider(
//            thickness = 0.5.dp,
//            color = MaterialTheme.colorScheme.outline
//        )

        content()


    }
}