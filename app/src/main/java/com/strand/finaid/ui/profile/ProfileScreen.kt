package com.strand.finaid.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.preferences.rememberThemePreference
import com.strand.finaid.ui.theme.AppTheme
import com.strand.finaid.ui.theme.isAppInDarkTheme

@Composable
fun ProfileScreen(
    navigateToTrash: () -> Unit,
    navigateToExport: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val selectedTheme = rememberThemePreference()

    if (viewModel.isThemeSelectionDialogOpen) {
        ThemeSelectionDialog(
            onDismiss = { viewModel.setIsThemeSelectionDialogOpen(false) },
            onConfirmed = {
                viewModel.setIsThemeSelectionDialogOpen(false)
                selectedTheme.value = it
            },
            options = AppTheme.values(),
            initialSelectedOption = selectedTheme.value
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SettingsScreenCard(
                onClick = { viewModel.setIsThemeSelectionDialogOpen(true) },
                icon = if (isAppInDarkTheme()) Icons.Default.DarkMode else Icons.Default.LightMode,
                headline = stringResource(id = R.string.select_theme),
                subheader = selectedTheme.value.title,
                isFirst = true
            )
            SettingsScreenCard(
                onClick = navigateToTrash,
                icon = Icons.Default.Delete,
                headline = stringResource(id = R.string.screen_trash),
                subheader = "Se och återställ raderade objekt"
            )
            SettingsScreenCard(
                onClick = navigateToExport,
                icon = Icons.Default.FileUpload,
                headline = stringResource(id = R.string.export_data),
                subheader = "Exportera data till Excel",
                isLast = true
            )
        }
    }
}

@Composable
private fun SettingsScreenCard(
    onClick: () -> Unit,
    icon: ImageVector,
    headline: String,
    subheader: String,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val shape = when {
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        isLast -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        else -> RoundedCornerShape(4.dp)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                )
            }
            Column(
                modifier = Modifier.height(IntrinsicSize.Max),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = subheader,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Composable
private fun ThemeSelectionDialog(
    onDismiss: () -> Unit,
    onConfirmed: (AppTheme) -> Unit,
    options: Array<AppTheme>,
    initialSelectedOption: AppTheme
) {
    var selectedOption by remember(initialSelectedOption) { mutableStateOf(initialSelectedOption) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.select_theme)) },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.cancel)) }
        },
        confirmButton = {
            FilledTonalButton(onClick = { onConfirmed(selectedOption) }) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption.ordinal == option.ordinal,
                            onClick = { selectedOption = option }
                        )
                        Text(text = option.title)
                    }
                }
            }
        }
    )
}


