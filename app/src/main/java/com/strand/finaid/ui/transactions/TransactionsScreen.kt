package com.strand.finaid.ui.transactions

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ext.rememberChartState
import com.strand.finaid.ui.components.AnimatedBarchart
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.list_items.TransactionItem

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit,
    openSortSheet: () -> Unit
) {
    val uiState: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<TransactionUiState?>(null) }

    TransactionsScreenDisplay(
        uiState = uiState,
        categories = categories,
        selectedCategories = selectedCategories,
        onToggleCategory = viewModel::toggleCategory,
        openSortSheet = openSortSheet,
        navigateToEditScreen = navigateToEditScreen,
        onDeleteClick = {
            selectedTransaction.value = it
            openDialog.value = true
        }
    )

    if (openDialog.value) {
        selectedTransaction.value?.let { transaction ->
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = stringResource(id = R.string.delete_transaction)) },
                text = { Text(text = stringResource(id = R.string.transaction_will_be_moved_to_trash)) },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            viewModel.onDeleteTransactionClick(transaction.id)
                            openDialog.value = false
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
private fun TransactionsScreenDisplay(
    uiState: TransactionScreenUiState,
    categories: List<Category>,
    selectedCategories: List<Category>,
    onToggleCategory: (Category) -> Unit,
    openSortSheet: () -> Unit,
    navigateToEditScreen: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit
) {
    Crossfade(targetState = uiState) { screen ->
        when (screen) {
            TransactionScreenUiState.Error -> { FullScreenError() }
            TransactionScreenUiState.Loading -> { FullScreenLoading() }
            is TransactionScreenUiState.Success -> {
                if (screen.transactions.isNullOrEmpty())
                    Text(text = "Empty Content")
                else {
                    TransactionsScreenContent(
                        transactions = screen.transactions,
                        categories = categories,
                        selectedCategories = selectedCategories,
                        onToggleCategory = onToggleCategory,
                        openSortSheet = openSortSheet,
                        onEditClick = navigateToEditScreen,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionsScreenContent(
    transactions: List<TransactionUiState>,
    categories: List<Category>,
    selectedCategories: List<Category>,
    onToggleCategory: (Category) -> Unit,
    openSortSheet: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            val chartState = rememberChartState(
                items = transactions,
                colors = { transaction -> transaction.color },
                amounts = { transaction -> transaction.amount }
            )
            TransactionGraph(
                displayValue = transactions.sumOf { it.amount },
                proportions = chartState.percentageProportions,
                colors = chartState.colors
            )
        }
        item {
            FilterRow(
                openSortSheet = openSortSheet,
                categories = categories,
                selectedCategories = selectedCategories,
                onToggleCategory = onToggleCategory
            )
        }
        items(transactions, key = { it.id }) { transactionItem ->
            TransactionItem(
                modifier = Modifier.animateItemPlacement(),
                transaction = transactionItem,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
        item { Spacer(modifier = Modifier.height(128.dp)) }
    }
}


@Composable
private fun FilterRow(
    openSortSheet: () -> Unit,
    categories: List<Category>,
    selectedCategories: List<Category>,
    onToggleCategory: (Category) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.width(0.dp)) }
        item {
            IconButton(onClick = openSortSheet) {
                Icon(imageVector = Icons.Default.Sort, contentDescription = null)
            }
        }
        items(categories, key = { it.id }) { category ->
            val selected by remember(selectedCategories) { mutableStateOf(category in selectedCategories) }
            FilterChip(
                selected = category in selectedCategories,
                onClick = { onToggleCategory(category) },
                label = { Text(text = category.name) },
                leadingIcon = {
                    if (selected)
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            tint = category.color
                        )
                }
            )
        }
        item { Spacer(modifier = Modifier.width(8.dp)) }
    }
}

@Composable
private fun TransactionGraph(
    displayValue: Int,
    proportions: List<Float>,
    colors: List<Color>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            shape = RoundedCornerShape(28.dp)
        ) {
            SegmentedButton(
                modifier = Modifier.padding(12.dp),
                items = listOf("Månad", "År", "Totalt"),
                selectedIndex = 0,
                indexChanged = {  }
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${displayValue.formatAmount()} kr", style = MaterialTheme.typography.headlineLarge)
                Row {
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
            AnimatedBarchart(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                proportions = proportions,
                colors = colors
            )
        }
    }
}