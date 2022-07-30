package com.strand.finaid.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.model.Result
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.TransactionItem

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit,
    openSortSheet: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<TransactionUiState?>(null) }

    // Use intermediate variable to enable smart cast and ensure that it has the same value in the condition and the when branches
    when (val t = transactions) {
        is Result.Success -> {
            if (t.data.isNullOrEmpty())
                Text(text = "Empty Content")
            else {
                val c = categories
                TransactionsScreenContent(
                    transactions = t.data,
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
        is Result.Error -> { FullScreenError() }
        Result.Loading -> { FullScreenLoading() }
    }

    if (openDialog.value) {
        selectedTransaction.value?.let { transaction ->
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Radera transaktionen?") },
                text = { Text(text = "Att radera den här transaktionen tar bort den permanent.") },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "Avbryt")
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            viewModel.onDeleteTransactionClick(transaction)
                            openDialog.value = false
                        }
                    ) { Text(text = "Radera") }
                }
            )
        }
    }
}

@Composable
private fun TransactionsScreenContent(
    transactions: List<TransactionUiState>,
    categories: List<CategoryUi>,
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
                modifier = Modifier
                    .animateItemPlacement()
                    .padding(horizontal = 8.dp),
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
    categories: List<CategoryUi>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.width(0.dp)) }
        item {
            IconButton(onClick = openSortSheet) {
                Icon(imageVector = Icons.Rounded.Sort, contentDescription = null)
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
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = category.color
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = Icons.Filled.Done,
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