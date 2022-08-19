package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R
import com.strand.finaid.model.Result
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SegmentedButton
import com.strand.finaid.ui.components.list_items.SavingsAccountItem

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val savingsAccounts by viewModel.savingsAccounts.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val selectedSavingsAccount = remember { mutableStateOf<SavingsAccountUiState?>(null) }

    // Use intermediate variable to enable smart cast and ensure that it has the same value in the condition and the when branches
    when (val s = savingsAccounts) {
        is Result.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                item { SavingsGraph() }
                items(s.data!!, key = { it.id }) { savingsAccountItem ->
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
        is Result.Error -> { FullScreenError() }
        Result.Loading -> { FullScreenLoading() }
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