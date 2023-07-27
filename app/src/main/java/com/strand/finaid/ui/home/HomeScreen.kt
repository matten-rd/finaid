package com.strand.finaid.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R
import com.strand.finaid.domain.HomeScreenUiState
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.list_items.HomeScreenSavingsAccountItem
import com.strand.finaid.ui.components.list_items.HomeScreenTransactionItem


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTransactions: () -> Unit,
    onNavigateToSavings: () -> Unit
) {
    val uiState = viewModel.uiState
    val refreshing by viewModel.isRefreshing

    val pullRefreshState = rememberPullRefreshState(refreshing, { viewModel.refresh() })

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
    ) {
        HomeScreenDisplay(
            uiState = uiState,
            onNavigateToTransactions = onNavigateToTransactions,
            onNavigateToSavings = onNavigateToSavings
        )

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}

@Composable
private fun HomeScreenDisplay(
    uiState: HomeScreenUiState,
    onNavigateToTransactions: () -> Unit,
    onNavigateToSavings: () -> Unit,
) {
    when (uiState) {
        HomeScreenUiState.Error -> { FullScreenError() }
        HomeScreenUiState.Loading -> { FullScreenLoading() }
        is HomeScreenUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeScreenCard(
                    header = stringResource(id = R.string.your_savings),
                    amount = uiState.savingsAccountSum,
                    onShowMoreClick = onNavigateToSavings
                ) {
                    uiState.savingsAccounts.forEachIndexed { idx, savingsAccount ->
                        HomeScreenSavingsAccountItem(
                            savingsAccount = savingsAccount,
                            isFirst = idx == 0,
                            isLast = idx == uiState.savingsAccounts.lastIndex
                        )
                    }
                }

                HomeScreenCard(
                    header = stringResource(id = R.string.total_net),
                    amount = uiState.transactionSum,
                    onShowMoreClick = onNavigateToTransactions
                ) {
                    uiState.transactions.forEachIndexed { idx, transaction ->
                        HomeScreenTransactionItem(
                            transaction = transaction,
                            isFirst = idx == 0,
                            isLast = idx == uiState.transactions.lastIndex
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HomeScreenCard(
    header: String,
    amount: Int,
    onShowMoreClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = header, style = MaterialTheme.typography.labelLarge)
                    Text(text = "${amount.formatAmount()} kr", style = MaterialTheme.typography.displaySmall)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                content()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onShowMoreClick) {
                    Text(text = stringResource(id = R.string.show_more))
                }
            }
        }
    }

}


