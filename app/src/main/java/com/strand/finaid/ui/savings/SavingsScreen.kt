package com.strand.finaid.ui.savings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.strand.finaid.domain.SavingsScreenUiState
import com.strand.finaid.ext.formatAmount
import com.strand.finaid.ext.rememberChartState
import com.strand.finaid.ui.components.AnimatedBarchart
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.list_items.SavingsAccountItem

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val uiState: SavingsScreenUiState by viewModel.savingsAccountsUiState.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }
    val selectedSavingsAccount = remember { mutableStateOf<SavingsAccountUiState?>(null) }

    SavingsScreenDisplay(
        uiState = uiState,
        navigateToEditScreen = navigateToEditScreen,
        onDeleteClick = {
            selectedSavingsAccount.value = it
            openDialog.value = true
        }
    )

    if (openDialog.value) {
        selectedSavingsAccount.value?.let { savingsAccount ->
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = stringResource(id = R.string.delete_savingsaccount)) },
                text = { Text(text = stringResource(id = R.string.savingsaccount_will_be_moved_to_trash)) },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            viewModel.onDeleteSavingsAccountClick(savingsAccount.id)
                            openDialog.value = false
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
fun SavingsScreenDisplay(
    uiState: SavingsScreenUiState,
    navigateToEditScreen: (String) -> Unit,
    onDeleteClick: (SavingsAccountUiState) -> Unit
) {
    Crossfade(targetState = uiState) { screen ->
        when (screen) {
            SavingsScreenUiState.Error -> { FullScreenError() }
            SavingsScreenUiState.Loading -> { FullScreenLoading() }
            is SavingsScreenUiState.Success -> {
                if (screen.savingsAccounts.isNullOrEmpty())
                    Text(text = "Empty Content")
                else
                    SavingsScreenContent(
                        savingsAccounts = screen.savingsAccounts,
                        navigateToEditScreen = navigateToEditScreen,
                        onDeleteClick = onDeleteClick
                    )
            }
        }
    }
}

@Composable
private fun SavingsScreenContent(
    savingsAccounts: List<SavingsAccountUiState>,
    navigateToEditScreen: (String) -> Unit,
    onDeleteClick: (SavingsAccountUiState) -> Unit
) {
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