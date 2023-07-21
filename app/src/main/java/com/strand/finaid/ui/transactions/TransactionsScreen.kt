package com.strand.finaid.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ext.formatMonthYear
import com.strand.finaid.ext.formatYear
import com.strand.finaid.ext.rememberChartState
import com.strand.finaid.ui.components.*
import com.strand.finaid.ui.components.list_items.TransactionItem
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val uiState: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    if (uiState.isError) {
        FullScreenError()
        return
    }

    val periods = viewModel.periods
    val graphPeriodState by viewModel.periodStateFlow.collectAsStateWithLifecycle()

    val transactionsListState = rememberLazyListState()

    TransactionsScreenContent(
        isLoading = uiState.isLoading,
        transactions = uiState.transactions ?: emptyList(),
        groupedTransactions = uiState.groupedTransactions ?: emptyMap(),
        onEditClick = navigateToEditScreen,
        onDeleteClick = viewModel::onDeleteTransactionSwipe,
        onDuplicateClick = {
            viewModel.duplicateTransaction(transactionId = it, onSuccess = navigateToEditScreen)
        },
        transactionsListState = transactionsListState,
        periods = periods,
        periodState = graphPeriodState,
        onSetPeriod = viewModel::onSetPeriod,
        incrementYear = viewModel::incrementYear,
        decrementYear = viewModel::decrementYear,
        incrementMonth = viewModel::incrementMonth,
        decrementMonth = viewModel::decrementMonth
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
    periods: List<Int>,
    periodState: PeriodState,
    onSetPeriod: (Int) -> Unit,
    incrementYear: () -> Unit,
    decrementYear: () -> Unit,
    incrementMonth: () -> Unit,
    decrementMonth: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = transactionsListState,
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
                periodState = periodState,
                onSetPeriod = onSetPeriod,
                incrementYear = incrementYear,
                decrementYear = decrementYear,
                incrementMonth = incrementMonth,
                decrementMonth = decrementMonth
            )
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

@Composable
private fun TransactionGraph(
    displayValue: Int,
    proportions: List<Float>,
    colors: List<Color>,
    periods: List<Int>,
    periodState: PeriodState,
    onSetPeriod: (Int) -> Unit,
    incrementYear: () -> Unit,
    decrementYear: () -> Unit,
    incrementMonth: () -> Unit,
    decrementMonth: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            shape = RoundedCornerShape(28.dp)
        ) {
            SegmentedButton(
                modifier = Modifier.padding(12.dp),
                items = periods.map { id -> stringResource(id = id) },
                selectedIndex = periodState.period.ordinal,
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
                Column {
                    val dateText = when (periodState.period) {
                        Period.Month -> periodState.selectedMonth.formatMonthYear()
                        Period.Year -> periodState.selectedYear.formatYear()
                        Period.Total -> ""
                    }
                    Text(text = "${displayValue.formatAmount()} kr", style = MaterialTheme.typography.headlineMedium)
                    Text(text = dateText, style = MaterialTheme.typography.bodySmall)
                }
                
                if (periodState.period != Period.Total) {
                    Row {
                        val decrement = if (periodState.period == Period.Year) decrementYear else decrementMonth
                        val increment = if (periodState.period == Period.Year) incrementYear else incrementMonth
                        FilledIconButton(onClick = decrement) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        FilledIconButton(onClick = increment) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                        }
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