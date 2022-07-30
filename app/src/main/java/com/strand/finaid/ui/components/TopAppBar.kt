package com.strand.finaid.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.strand.finaid.R

@Composable
fun FinaidMainTopAppBar(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}