package com.strand.finaid.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strand.finaid.R

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigateUp: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        modifier = Modifier
            .padding(end = 16.dp)
            .fillMaxWidth(),
        value = query, 
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, capitalization = KeyboardCapitalization.Sentences),
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
            focusManager.clearFocus()
        },
        decorationBox = { innerTextField ->  
            SearchFieldDecorationBox(
                query = query,
                onClearQuery = { onQueryChange("") },
                innerTextField = innerTextField,
                onNavigateUp = onNavigateUp
            )
        }
    )
}

@Composable
fun SearchFieldDecorationBox(
    query: String,
    onClearQuery: () -> Unit,
    innerTextField: @Composable () -> Unit,
    onNavigateUp: () -> Unit,
) {
    Surface(
        modifier = Modifier.height(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 6.dp
    ) {
        Row(
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
            Spacer(modifier = Modifier.weight(1f))
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            }
        }
    }

}