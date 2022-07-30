package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R
import com.strand.finaid.ui.components.ColorPicker
import com.strand.finaid.ui.components.textfield.FinaidTextField

@Composable
fun AddEditSavingsScreen(
    viewModel: AddEditSavingsViewModel = hiltViewModel(),
    savingsAccountId: String,
    navigateUp: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.initialize(savingsAccountId)
    }

    val uiState by viewModel.uiState

    if (viewModel.isDeleteSavingsAccountDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.setIsDeleteSavingsAccountDialogOpen(false) },
            title = { Text(text = "Radera sparkontot?") },
            text = { Text(text = "Att radera det här sparkontot tar bort det permanent.") },
            dismissButton = {
                TextButton(onClick = { viewModel.setIsDeleteSavingsAccountDialogOpen(false) }) {
                    Text(text = "Avbryt")
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.onDeleteSavingsAccountClick(uiState.id)
                        viewModel.setIsDeleteSavingsAccountDialogOpen(false)
                        navigateUp()
                    }
                ) { Text(text = "Radera") }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = "Kontonamn",
            imeAction = ImeAction.Next
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.bank,
            onValueChange = viewModel::onBankChange,
            label = "Bank",
            imeAction = ImeAction.Next
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.amount,
            onValueChange = viewModel::onAmountChange,
            label = "Pengar på kontot",
            keyboardType = KeyboardType.Number
        )
        Card(shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "Välj färg", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPicker(
                    modifier = Modifier.fillMaxWidth(),
                    items = viewModel.colors,
                    selectedColor = uiState.color,
                    onColorSelected = viewModel::onColorChange,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.saveSavingsAccount { navigateUp() } }
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}