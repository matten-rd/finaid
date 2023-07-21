package com.strand.finaid.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.ui.components.EmptyContentScreen
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.list_items.TransactionItem
import com.strand.finaid.ui.transactions.TransactionUiState

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit,
    openSortSheet: () -> Unit
) {
    val uiState: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    if (uiState.isError) {
        FullScreenError()
        return
    }

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()

    val transactionsListState = rememberLazyListState()
    val filterRowListState = rememberLazyListState()

    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        SearchTransactionsScreenContent(
            isLoading = uiState.isLoading,
            transactions = uiState.transactions ?: emptyList(),
            categories = categories,
            selectedCategories = selectedCategories,
            onToggleCategory = viewModel::toggleCategory,
            onClearSelectedCategories = viewModel::clearSelectedCategories,
            openSortSheet = openSortSheet,
            onEditClick = navigateToEditScreen,
            onDeleteClick = viewModel::onDeleteTransactionSwipe,
            onDuplicateClick = {
                viewModel.onDuplicateTransactionSwipe(transactionId = it, onSuccess = navigateToEditScreen)
            },
            transactionsListState = transactionsListState,
            filterRowListState = filterRowListState
        )
    }
}

@Composable
private fun SearchTransactionsScreenContent(
    isLoading: Boolean,
    transactions: List<TransactionUiState>,
    categories: List<Category>,
    selectedCategories: List<Category>,
    onToggleCategory: (Category) -> Unit,
    onClearSelectedCategories: () -> Unit,
    openSortSheet: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDuplicateClick: (String) -> Unit,
    transactionsListState: LazyListState,
    filterRowListState: LazyListState
) {
    FilterRow(
        openSortSheet = openSortSheet,
        categories = categories,
        selectedCategories = selectedCategories,
        onToggleCategory = onToggleCategory,
        onClearSelectedCategories = onClearSelectedCategories,
        listState = filterRowListState
    )
    if (transactions.isEmpty() && !isLoading)
        EmptyContentScreen(id = R.drawable.ostrich, text = "Inga sökresultat")
    else
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = transactionsListState,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(transactions, key = { it.id }) { transactionItem ->
                TransactionItem(
                    modifier = Modifier.animateItemPlacement(),
                    transaction = transactionItem,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDuplicateClick = onDuplicateClick
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
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
private fun SearchTransactionsContent(
    categories: List<Category>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.width(8.dp)) }

        items(categories, key = { it.id }) { category ->
            var selected by remember { mutableStateOf(false) }
            FilterChip(
                selected = selected,
                onClick = { selected = !selected },
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
