package com.strand.finaid.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun CategoryBottomSheet(
    viewModel: AddEditTransactionViewModel,
    onClose: () -> Unit
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
            onClose()
        },
        onDeleteCategoryClick = { category ->
            viewModel.setConfirmDeleteCategoryAlertDialogUiState(isOpen = true, category = category)
            onClose()
        },
        onEditCategoryClick = { category ->
            viewModel.setEditCategoryDialog(category)
            onClose()
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
                .padding(horizontal = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (options.isNotEmpty())
                    options.forEachIndexed { index, category ->
                        CategoryItem(
                            category = category,
                            onCategorySelected = onCategorySelected,
                            isSelected = category == selectedCategory,
                            isFirst = index == 0,
                            isLast = index == options.size - 1,
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
    isFirst: Boolean,
    isLast: Boolean,
    onDeleteCategoryClick: (Category) -> Unit,
    onEditCategoryClick: (Category) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)

    modifier = when {
        isSelected || (isFirst && isLast) -> modifier.clip(RoundedCornerShape(12.dp))
        isFirst -> modifier.clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
        isLast -> modifier.clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
        else -> modifier.clip(RoundedCornerShape(4.dp))
    }
    modifier = modifier.clickable { onCategorySelected(category) }

    Row(
        modifier =
        if (isSelected)
            modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
        else
            modifier.background(MaterialTheme.colorScheme.surface),
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