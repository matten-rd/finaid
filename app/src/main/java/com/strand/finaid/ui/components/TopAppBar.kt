package com.strand.finaid.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.strand.finaid.R

@Composable
fun FinaidMainTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int? = null,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(id = titleId ?: R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}