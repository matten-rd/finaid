package com.strand.finaid.ui.transactions

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.graphics.Brush
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
    openSortSheet: () -> Unit,
    filterRowListState: LazyListState = rememberLazyListState()
) {
    val uiState: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val isDialogOpen = remember { mutableStateOf(false) }
    val periods = viewModel.periods
    val selectedPeriod by viewModel.periodFlow.collectAsStateWithLifecycle()

    TransactionsScreenDisplay(
        uiState = uiState,
        categories = categories,
        selectedCategories = selectedCategories,
        onToggleCategory = viewModel::toggleCategory,
        onClearSelectedCategories = viewModel::clearSelectedCategories,
        openSortSheet = openSortSheet,
        navigateToEditScreen = navigateToEditScreen,
        onDeleteClick = {
            viewModel.setSelectedTransaction(it)
            isDialogOpen.value = true
        },
        filterRowListState = filterRowListState,
        periods = periods,
        selectedPeriod = selectedPeriod,
        onSetPeriod = viewModel::onSetPeriod
    )

    if (isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { isDialogOpen.value = false },
            title = { Text(text = stringResource(id = R.string.delete_transaction)) },
            text = { Text(text = stringResource(id = R.string.transaction_will_be_moved_to_trash)) },
            dismissButton = {
                TextButton(onClick = { isDialogOpen.value = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                FilledTonalButton(onClick = {
                    viewModel.onConfirmDeleteTransactionClick()
                    isDialogOpen.value = false
                }) {
                    Text(text = stringResource(id = R.string.delete))
                }
            }
        )
    }
}

@Composable
private fun TransactionsScreenDisplay(
    uiState: TransactionScreenUiState,
    categories: List<Category>,
    selectedCategories: List<Category>,
    onToggleCategory: (Category) -> Unit,
    onClearSelectedCategories: () -> Unit,
    openSortSheet: () -> Unit,
    navigateToEditScreen: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit,
    filterRowListState: LazyListState,
    periods: List<Int>,
    selectedPeriod: Period,
    onSetPeriod: (Int) -> Unit
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
                        onClearSelectedCategories = onClearSelectedCategories,
                        openSortSheet = openSortSheet,
                        onEditClick = navigateToEditScreen,
                        onDeleteClick = onDeleteClick,
                        filterRowListState = filterRowListState,
                        periods = periods,
                        selectedPeriod = selectedPeriod,
                        onSetPeriod = onSetPeriod
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
    onClearSelectedCategories: () -> Unit,
    openSortSheet: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit,
    filterRowListState: LazyListState,
    periods: List<Int>,
    selectedPeriod: Period,
    onSetPeriod: (Int) -> Unit
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
                colors = chartState.colors,
                periods = periods,
                selectedPeriod = selectedPeriod,
                onSetPeriod = onSetPeriod
            )
        }
        item {
            FilterRow(
                openSortSheet = openSortSheet,
                categories = categories,
                selectedCategories = selectedCategories,
                onToggleCategory = onToggleCategory,
                onClearSelectedCategories = onClearSelectedCategories,
                listState = filterRowListState
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
    onToggleCategory: (Category) -> Unit,
    onClearSelectedCategories: () -> Unit,
    listState: LazyListState
) {
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stickyHeader {
            Row(
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        0.95f to MaterialTheme.colorScheme.background,
                        1f to Color.Transparent,
                    )
                )
            ) {
                Spacer(modifier = Modifier.size(8.dp))
                if (selectedCategories.isNotEmpty()) {
                    IconButton(onClick = onClearSelectedCategories, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                }
                IconButton(onClick = openSortSheet, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Sort, contentDescription = null)
                }
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
    colors: List<Color>,
    periods: List<Int>,
    selectedPeriod: Period,
    onSetPeriod: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            shape = RoundedCornerShape(28.dp)
        ) {
            SegmentedButton(
                modifier = Modifier.padding(12.dp),
                items = periods.map { id -> stringResource(id = id) },
                selectedIndex = selectedPeriod.ordinal,
                indexChanged = onSetPeriod
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