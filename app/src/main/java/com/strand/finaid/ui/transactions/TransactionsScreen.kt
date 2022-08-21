package com.strand.finaid.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.models.Category
import com.strand.finaid.domain.TransactionScreenUiState
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
    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }

    val transactions: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<TransactionUiState?>(null) }

    // Use intermediate variable to enable smart cast and ensure that it has the same value in the condition and the when branches
    when (val t = transactions) {
        TransactionScreenUiState.Error -> { FullScreenError() }
        TransactionScreenUiState.Loading -> { FullScreenLoading() }
        is TransactionScreenUiState.Success -> {
            if (t.transactions.isNullOrEmpty())
                Text(text = "Empty Content")
            else {
                val c = categories
                TransactionsScreenContent(
                    transactions = t.transactions,
                    categories = if (c is Result.Success && !c.data.isNullOrEmpty()) c.data else emptyList(),
                    openSortSheet = openSortSheet,
                    onEditClick = navigateToEditScreen,
                    onDeleteClick = {
                        selectedTransaction.value = it
                        openDialog.value = true
                    }
                )
            }
        }
    }

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
                            viewModel.onDeleteTransactionClick(transaction)
                            openDialog.value = false
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
private fun TransactionsScreenContent(
    transactions: List<TransactionUiState>,
    categories: List<Category>,
    openSortSheet: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (TransactionUiState) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item { TransactionGraph() }
        item {
            FilterRow(
                openSortSheet = openSortSheet,
                categories = categories,
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
    categories: List<Category>
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
            var selected by remember { mutableStateOf(false) }
            FilterChip(
                selected = selected,
                onClick = { selected = !selected },
                label = { Text(text = category.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = category.color
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }
        item { Spacer(modifier = Modifier.width(8.dp)) }
    }
}

@Composable
private fun TransactionGraph() {
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

        }
    }
}