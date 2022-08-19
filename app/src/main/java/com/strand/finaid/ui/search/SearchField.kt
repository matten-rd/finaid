package com.strand.finaid.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strand.finaid.R

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigateUp: () -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .padding(end = 16.dp)
            .fillMaxWidth(),
        value = query, 
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->  
            SearchFieldDecorationBox(
                query = query,
                innerTextField = innerTextField,
                onNavigateUp = onNavigateUp
            )
        }
    )
}

@Composable
fun SearchFieldDecorationBox(
    query: String,
    innerTextField: @Composable () -> Unit,
    onNavigateUp: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .heightIn(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            innerTextField()
            if (query.isEmpty()) Text(text = stringResource(id = R.string.search), style = LocalTextStyle.current)
        }
    }
}