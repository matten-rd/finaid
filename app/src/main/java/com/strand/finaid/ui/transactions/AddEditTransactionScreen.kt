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
import com.strand.finaid.data.models.Category
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
                title = { Text(text = stringResource(id = R.string.delete_category)) },
                text = { Text(text = stringResource(id = R.string.category_will_be_moved_to_trash)) },
                dismissButton = {
                    TextButton(onClick = { viewModel.setConfirmDeleteCategoryAlertDialogUiState(false) }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(onClick = { viewModel.moveTransactionCategoryToTrash(category.id) }) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                }
            )
        }
    }

    if (viewModel.isDeleteTransactionDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.setIsDeleteTransactionDialogOpen(false) },
            title = { Text(text = stringResource(id = R.string.delete_transaction)) },
            text = { Text(text = stringResource(id = R.string.transaction_will_be_moved_to_trash)) },
            dismissButton = {
                TextButton(onClick = { viewModel.setIsDeleteTransactionDialogOpen(false) }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.deleteTransaction(uiState.id)
                        viewModel.setIsDeleteTransactionDialogOpen(false)
                        navigateUp()
                    }
                ) { Text(text = stringResource(id = R.string.delete)) }
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
            amountLabel = stringResource(id = if (transactionType == TransactionType.Expense) R.string.expense else R.string.income),
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
    category: Category?,
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
            label = stringResource(id = R.string.select_category),
            leadingIcon = category?.let { { Icon(imageVector = Icons.Default.Circle, contentDescription = null, tint = it.color) } },
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
            label = stringResource(id = R.string.memo)
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
            ) { Text(text = stringResource(id = R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.cancel)) }
        },
        title = { Text(text = stringResource(id = if (uiState.isEdit) R.string.edit_category else R.string.add_category)) },
        text = {
            Column {
                FinaidTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.name,
                    onValueChange = onCategoryNameChange,
                    label = stringResource(id = R.string.category)
                )

                ColorPicker(
                    modifier = Modifier.fillMaxWidth(),
                    items = uiState.availableColors,
                    selectedColor = uiState.color,
                    onColorSelected = onColorSelected,
                    disabledColors = uiState.disabledColors
                )

                Text(
                    text = stringResource(id = R.string.warning_color_already_selected),
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