package com.strand.finaid.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.preferences.rememberThemePreference
import com.strand.finaid.ui.theme.AppTheme

@Composable
fun ProfileScreen(
    navigateToLanding: () -> Unit,
    navigateToTrash: () -> Unit,
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
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileScreenCard(onClick = { viewModel.setIsThemeSelectionDialogOpen(true) }) {
                Text(text = "Välj tema", style = MaterialTheme.typography.titleLarge)
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null)
            }
            ProfileScreenCard(onClick = navigateToTrash) {
                Text(text = "Papperskorgen", style = MaterialTheme.typography.titleLarge)
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null)
            }
            ProfileScreenCard(onClick = { /*TODO: Change password*/ }) {
                Text(text = "Byt lösenord", style = MaterialTheme.typography.titleLarge)
                Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null)
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.onSignOutClick(onSuccess = navigateToLanding) }
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Logga ut")
        }
    }
}


@Composable
private fun ProfileScreenCard(
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 28.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
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
        title = { Text(text = "Välj tema") },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Avbryt") }
        },
        confirmButton = {
            FilledTonalButton(onClick = { onConfirmed(selectedOption) }) {
                Text(text = "Spara")
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


