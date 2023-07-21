package com.strand.finaid.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.strand.finaid.R

@Composable
internal fun FullScreenLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun FullScreenError() {
    EmptyContentScreen(
        id = R.drawable.ice_cream_spill,
        text = stringResource(id = R.string.generic_error)
    )
}

@Composable
internal fun EmptyContentScreen(
    @DrawableRes id: Int,
    text: String,
    supportingText: String? = null,
    size: Dp = 250.dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = id),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, style = MaterialTheme.typography.titleLarge)
        if (supportingText != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = supportingText, textAlign = TextAlign.Center, modifier = Modifier.width(250.dp))
        }
    }
}