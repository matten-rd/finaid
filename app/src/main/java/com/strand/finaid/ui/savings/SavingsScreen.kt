package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.domain.SavingsScreenUiState
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ext.rememberChartState
import com.strand.finaid.ui.components.AnimatedBarchart
import com.strand.finaid.ui.components.EmptyContentScreen
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.list_items.SavingsAccountItem
import com.strand.finaid.R

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val uiState: SavingsScreenUiState by viewModel.savingsAccountsUiState.collectAsStateWithLifecycle()

    if (uiState.isError) {
        FullScreenError()
        return
    }

    SavingsScreenContent(
        savingsAccounts = uiState.savingsAccounts ?: emptyList(),
        navigateToEditScreen = navigateToEditScreen,
        onDeleteClick = viewModel::onDeleteSavingsAccountSwipe
    )

}

@Composable
private fun SavingsScreenContent(
    savingsAccounts: List<SavingsAccountUiState>,
    navigateToEditScreen: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (savingsAccounts.isEmpty())
        EmptyContentScreen(id = R.drawable.lost_keys, text = "Inga sparkonton än")
    else
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                val chartState = rememberChartState(
                    items = savingsAccounts,
                    colors = { account -> account.color },
                    amounts = { account -> account.amount }
                )
                SavingsGraph(
                    displayValue = savingsAccounts.sumOf { it.amount },
                    proportions = chartState.percentageProportions,
                    colors = chartState.colors
                )
            }
            items(savingsAccounts, key = { it.id }) { savingsAccountItem ->
               SavingsAccountItem(
                   modifier = Modifier.animateItemPlacement(),
                   savingsAccount = savingsAccountItem,
                   onEditClick = navigateToEditScreen,
                   onDeleteClick = onDeleteClick
               )
            }
            item { Spacer(modifier = Modifier.height(128.dp)) }
        }
}

@Composable
private fun SavingsGraph(
    displayValue: Int,
    proportions: List<Float>,
    colors: List<Color>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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