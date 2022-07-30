package com.strand.finaid.ui.savings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.model.Result
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SavingsAccountItem

@Composable
fun SavingsScreen(
    viewModel: SavingsViewModel = hiltViewModel(),
    navigateToEditScreen: (String) -> Unit
) {
    val savingsAccounts by viewModel.savingsAccounts.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val selectedSavingsAccountId = remember { mutableStateOf<String?>(null) }

    // Use intermediate variable to enable smart cast and ensure that it has the same value in the condition and the when branches
    when (val s = savingsAccounts) {
        is Result.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(s.data!!, key = { it.id }) { savingsAccountItem ->
                    SavingsAccountItem(
                        modifier = Modifier.animateItemPlacement(),
                        savingsAccount = savingsAccountItem,
                        onEditClick = navigateToEditScreen,
                        onDeleteClick = {
                            selectedSavingsAccountId.value = it
                            openDialog.value = true
                        }
                    )
                }
            }
        }
        is Result.Error -> { FullScreenError() }
        Result.Loading -> { FullScreenLoading() }
    }


    if (openDialog.value) {
        selectedSavingsAccountId.value?.let { savingsAccountId ->
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Radera sparkontot?") },
                text = { Text(text = "Att radera det här sparkontot tar bort det permanent.") },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "Avbryt")
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            viewModel.onDeleteSavingsAccountClick(savingsAccountId)
                            openDialog.value = false
                        }
                    ) { Text(text = "Radera") }
                }
            )
        }
    }
}