package com.strand.finaid.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.ext.rememberTransactionChartState
import com.strand.finaid.ui.components.EmptyContentScreen
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.charts.AnimatedBarchart
import com.strand.finaid.ui.components.charts.PeriodStateHolder
import com.strand.finaid.ui.components.list_items.TransactionItem
import com.strand.finaid.ui.components.widgets.TransactionBaseWidget

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val uiState: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    if (uiState.isError) {
        FullScreenError()
        return
    }

    val transactionsListState = rememberLazyListState()

    TransactionsScreenContent(
        isLoading = uiState.isLoading,
        transactions = uiState.transactions ?: emptyList(),
        groupedTransactions = uiState.groupedTransactions ?: emptyMap(),
        onEditClick = navigateToEditScreen,
        onDeleteClick = viewModel::onDeleteTransactionSwipe,
        onDuplicateClick = {
            viewModel.onDuplicateTransactionSwipe(transactionId = it, onSuccess = navigateToEditScreen)
        },
        transactionsListState = transactionsListState,
        periodStateHolder = viewModel.periodStateHolder
    )
}

@Composable
private fun TransactionsScreenContent(
    isLoading: Boolean,
    transactions: List<TransactionUiState>,
    groupedTransactions: Map<String, List<TransactionUiState>>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDuplicateClick: (String) -> Unit,
    transactionsListState: LazyListState,
    periodStateHolder: PeriodStateHolder
) {
    val chartState = rememberTransactionChartState(transactions = transactions)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = transactionsListState,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            TransactionBaseWidget(
                displayValue = transactions.sumOf { it.amount },
                periodStateHolder = periodStateHolder
            ) {
                AnimatedBarchart(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    values = chartState.values,
                    colors = chartState.categories.map { it.color }
                )
            }
        }
        if (transactions.isEmpty() && !isLoading)
            item { EmptyContentScreen(id = R.drawable.lost_keys, text = "Inga transaktioner än", size = 150.dp) }
        else
            groupedTransactions.forEach { (dateHeader, transactions) ->
                stickyHeader {
                    Row(modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()) {
                        Text(
                            text = dateHeader,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

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

        item { Spacer(modifier = Modifier.height(128.dp)) }
    }
}