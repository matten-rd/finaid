package com.strand.finaid.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ui.components.list_items.HomeScreenSavingsAccountItem
import com.strand.finaid.ui.components.list_items.HomeScreenTransactionItem


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTransactions: () -> Unit,
    onNavigateToSavings: () -> Unit
) {
    val transactions = viewModel.transactions
    val savingsAccounts = viewModel.savingsAccounts
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        HomeScreenCard(
            header = "Ditt sparande",
            amount = 123456,
            onShowMoreClick = onNavigateToSavings
        ) {
            savingsAccounts.forEach { savingsAccount ->
                HomeScreenSavingsAccountItem(savingsAccount = savingsAccount)
            }
        }

        HomeScreenCard(
            header = "Totalt netto",
            amount = 12345,
            onShowMoreClick = onNavigateToTransactions
        ) {
            transactions.forEach { transaction ->
                HomeScreenTransactionItem(transaction = transaction)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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

            content()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onShowMoreClick) {
                    Text(text = "Visa mer")
                }
            }
        }
    }

}


