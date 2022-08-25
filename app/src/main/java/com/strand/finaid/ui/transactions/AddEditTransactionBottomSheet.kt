package com.strand.finaid.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.ui.components.BaseBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CategoryBottomSheet(
    viewModel: AddEditTransactionViewModel,
    bottomSheetState: ModalBottomSheetState,
    scope: CoroutineScope
) {
    val transactionType by viewModel.transactionType
    val incomeCategories by viewModel.incomeCategories.collectAsStateWithLifecycle()
    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()

    val (options, selectedCategory, onCategorySelected) = when (transactionType) {
        TransactionType.Income -> Triple(incomeCategories, viewModel.uiState.incomeCategory, viewModel::onIncomeCategoryChange)
        TransactionType.Expense -> Triple(expenseCategories, viewModel.uiState.expenseCategory, viewModel::onExpenseCategoryChange)
    }

    CategoryBottomSheetContent(
        options = options,
        selectedCategory = selectedCategory,
        onCategorySelected = onCategorySelected,
        onCreateCategoryClick = {
            viewModel.setIsAddEditCategoryDialogOpen(true)
            scope.launch { bottomSheetState.hide() }
        },
        onDeleteCategoryClick = { category ->
            viewModel.setConfirmDeleteCategoryAlertDialogUiState(isOpen = true, category = category)
            scope.launch { bottomSheetState.hide() }
        },
        onEditCategoryClick = { category ->
            viewModel.setEditCategoryDialog(category)
            scope.launch { bottomSheetState.hide() }
        }
    )
}


@Composable
private fun CategoryBottomSheetContent(
    options: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    onCreateCategoryClick: () -> Unit,
    onDeleteCategoryClick: (Category) -> Unit,
    onEditCategoryClick: (Category) -> Unit,
) {
    BaseBottomSheet(title = stringResource(id = R.string.select_category)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (options.isNotEmpty())
                    options.forEach { category ->
                        CategoryItem(
                            category = category,
                            onCategorySelected = onCategorySelected,
                            isSelected = category == selectedCategory,
                            onDeleteCategoryClick = onDeleteCategoryClick,
                            onEditCategoryClick = onEditCategoryClick
                        )
                    }
                else
                    Text(text = "Inga kategorier än!") // TODO: Add image to illustrate empty
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(onClick = onCreateCategoryClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.add_category))
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onCategorySelected: (Category) -> Unit,
    isSelected: Boolean,
    onDeleteCategoryClick: (Category) -> Unit,
    onEditCategoryClick: (Category) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .clip(CircleShape)
        .clickable { onCategorySelected(category) }

    Row(
        modifier =
        if (isSelected)
            modifier.background(MaterialTheme.colorScheme.secondaryContainer)
        else
            modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                tint = category.color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = category.name,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else LocalContentColor.current
            )
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else LocalContentColor.current
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.edit)) },
                    onClick = {
                        menuExpanded = false
                        onEditCategoryClick(category)
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.delete)) },
                    onClick = {
                        menuExpanded = false
                        onDeleteCategoryClick(category)
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) }
                )
            }
        }
    }
}