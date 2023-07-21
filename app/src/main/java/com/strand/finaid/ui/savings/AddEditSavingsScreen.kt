package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    navigateUp: () -> Unit
) {
    val uiState = viewModel.uiState

    if (viewModel.isDeleteSavingsAccountDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.setIsDeleteSavingsAccountDialogOpen(false) },
            title = { Text(text = stringResource(id = R.string.delete_savingsaccount)) },
            text = { Text(text = stringResource(id = R.string.savingsaccount_will_be_moved_to_trash)) },
            dismissButton = {
                TextButton(onClick = { viewModel.setIsDeleteSavingsAccountDialogOpen(false) }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.onDeleteSavingsAccountClick(uiState.id)
                        viewModel.setIsDeleteSavingsAccountDialogOpen(false)
                        navigateUp()
                    }
                ) { Text(text = stringResource(id = R.string.delete)) }
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
            label = stringResource(id = R.string.account_name),
            imeAction = ImeAction.Next
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.bank,
            onValueChange = viewModel::onBankChange,
            label = stringResource(id = R.string.bank),
            imeAction = ImeAction.Next
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.amount,
            onValueChange = viewModel::onAmountChange,
            label = stringResource(id = R.string.money_in_account),
            keyboardType = KeyboardType.Number
        )
        Card(shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(text = stringResource(id = R.string.select_color), style = MaterialTheme.typography.bodySmall)
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