package com.strand.finaid.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R
import com.strand.finaid.ui.components.ColorPicker
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.textfield.FinaidTextField
import java.time.LocalDate

@Composable
fun AddEditTransactionScreen(
    viewModel: AddEditTransactionViewModel = hiltViewModel(),
    transactionId: String,
    openSheet: () -> Unit,
    navigateUp: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.initialize(transactionId)
    }

    val transactionType by viewModel.transactionType
    val uiState = viewModel.uiState

    val addEditCategoryDialogUiState = viewModel.addEditCategoryDialogUiState
    if (addEditCategoryDialogUiState.isOpen) {
        AddEditCategoryDialog(
            onDismiss = viewModel::dismissDialog,
            uiState = addEditCategoryDialogUiState,
            onCategoryNameChange = viewModel::onCategoryNameChange,
            onColorSelected = viewModel::onColorSelected,
            addTransactionCategory = viewModel::addTransactionCategory
        )
    }

    val deleteCategoryDialogUiState = viewModel.deleteCategoryDialogUiState
    if (deleteCategoryDialogUiState.isOpen) {
        deleteCategoryDialogUiState.category?.let { category ->
            AlertDialog(
                onDismissRequest = { viewModel.setConfirmDeleteCategoryAlertDialogUiState(false) },
                title = { Text(text = "Flytta kategorin till papperskorgen?") },
                text = { Text(text = "Kategorin flyttas till papperskorgen. Det är möjligt att återskapa den senare.") },
                dismissButton = {
                    TextButton(onClick = { viewModel.setConfirmDeleteCategoryAlertDialogUiState(false) }) {
                        Text(text = "Avbryt")
                    }
                },
                confirmButton = {
                    FilledTonalButton(onClick = { viewModel.moveTransactionCategoryToTrash(category) }) {
                        Text(text = "Radera")
                    }
                }
            )
        }
    }

    if (viewModel.isDeleteTransactionDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.setIsDeleteTransactionDialogOpen(false) },
            title = { Text(text = "Radera transaktionen?") },
            text = { Text(text = "Att radera den här transaktionen tar bort den permanent.") },
            dismissButton = {
                TextButton(onClick = { viewModel.setIsDeleteTransactionDialogOpen(false) }) {
                    Text(text = "Avbryt")
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.deleteTransaction(uiState.id)
                        viewModel.setIsDeleteTransactionDialogOpen(false)
                        navigateUp()
                    }
                ) { Text(text = "Radera") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SegmentedButton(
            items = viewModel.transactionTypes,
            selectedIndex = transactionType.ordinal,
            indexChanged = viewModel::onTransactionTypeChange
        )

        AddEditTransactionContent(
            memo = uiState.memo,
            amount = uiState.amount,
            date = uiState.date,
            category = if (transactionType == TransactionType.Expense) uiState.expenseCategory else uiState.incomeCategory,
            onMemoChange = viewModel::onMemoChange,
            onAmountChange = viewModel::onAmountChange,
            onDateChange = viewModel::onDateChange,
            amountLabel = if (transactionType == TransactionType.Expense) "Utgift" else "Inkomst",
            showBottomSheet = openSheet,
            onSaveClick = { viewModel.saveTransaction(navigateUp) }
        )
    }
}

@Composable
private fun AddEditTransactionContent(
    memo: String,
    amount: String,
    date: LocalDate,
    category: CategoryUi?,
    onMemoChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    amountLabel: String,
    showBottomSheet: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ReadonlyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = category?.name ?: "",
            onClick = showBottomSheet,
            label = "Välj kategori",
            leadingIcon = category?.let { { Icon(imageVector = Icons.Filled.Circle, contentDescription = null, tint = it.color) } },
            trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = onAmountChange,
            label = amountLabel,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
        FinaidTextField(
            modifier = Modifier.fillMaxWidth(),
            value = memo,
            onValueChange = onMemoChange,
            label = "Kommentar"
        )
        DateTextField(
            modifier = Modifier.fillMaxWidth(),
            currentDate = date,
            onDateChange = onDateChange
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveClick
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
private fun AddEditCategoryDialog(
    onDismiss: () -> Unit,
    uiState: AddEditCategoryDialogUiState,
    onCategoryNameChange: (String) -> Unit,
    onColorSelected: (Color) -> Unit,
    addTransactionCategory: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(
                onClick = addTransactionCategory,
                enabled = remember(uiState) { uiState.name.isNotBlank() && uiState.color != null }
            ) { Text(text = "Spara") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Avbryt") }
        },
        title = { Text(text = if (uiState.isEdit) "Redigera kategori" else "Lägg till kategori") },
        text = {
            Column {
                FinaidTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.name,
                    onValueChange = onCategoryNameChange,
                    label = "Kategori"
                )

                ColorPicker(
                    modifier = Modifier.fillMaxWidth(),
                    items = uiState.availableColors,
                    selectedColor = uiState.color,
                    onColorSelected = onColorSelected,
                    disabledColors = uiState.disabledColors
                )

                Text(
                    text = "Varning: Färgen redan vald i en annan kategori.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .alpha(if (uiState.isColorError) 1f else 0f)
                )
            }
        }
    )
}