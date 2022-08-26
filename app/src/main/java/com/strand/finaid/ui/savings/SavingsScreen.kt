package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strand.finaid.R
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.list_items.SavingsAccountItem

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val savingsAccounts: SavingsScreenUiState by viewModel.savingsAccountsUiState.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }
    val selectedSavingsAccount = remember { mutableStateOf<SavingsAccountUiState?>(null) }

    // Use intermediate variable to enable smart cast and ensure that it has the same value in the condition and the when branches
    when (val s = savingsAccounts) {
        SavingsScreenUiState.Error -> { FullScreenError() }
        SavingsScreenUiState.Loading -> { FullScreenLoading() }
        is SavingsScreenUiState.Success -> {
            if (s.savingsAccounts.isNullOrEmpty())
                Text(text = "Empty Content")
            else
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item { SavingsGraph() }
                    items(s.savingsAccounts, key = { it.id }) { savingsAccountItem ->
                        SavingsAccountItem(
                            modifier = Modifier.animateItemPlacement(),
                            savingsAccount = savingsAccountItem,
                            onEditClick = navigateToEditScreen
                        ) {
                            selectedSavingsAccount.value = it
                            openDialog.value = true
                        }
                    }
                    item { Spacer(modifier = Modifier.height(128.dp)) }
                }
        }
    }

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
                            viewModel.onDeleteSavingsAccountClick(savingsAccount)
                            openDialog.value = false
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
private fun SavingsGraph() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            shape = RoundedCornerShape(28.dp)
        ) {
            SegmentedButton(
                modifier = Modifier.padding(12.dp),
                items = listOf("Konto", "År"),
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