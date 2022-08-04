package com.strand.finaid.ui.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.strand.finaid.model.Result
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.SavingsAccountItem
import com.strand.finaid.ui.components.TransactionItem
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.transactions.CategoryUi
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.launch

@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val transactions by viewModel.transactions.collectAsState()
    val savingsAccounts by viewModel.savingsAccounts.collectAsState()

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
        )
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = indicator
        ) {
            viewModel.trashTypes.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title) }
                )
            }
        }

        HorizontalPager(
            count = viewModel.trashTypes.size,
            state = pagerState
        ) { page ->
            Column(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    TrashType.Savings.ordinal -> SavingsTrashScreen(
                        savingsAccounts = savingsAccounts,
                        onRestoreClick = viewModel::restoreSavingsAccountFromTrash
                    )
                    TrashType.Transactions.ordinal -> TransactionsTrashScreen(
                        transactions = transactions,
                        onRestoreClick = viewModel::restoreTransactionFromTrash
                    )
                    TrashType.Categories.ordinal -> CategoryTrashScreen(
                        uiState = viewModel.categoryUiState,
                        setIsCategoryRestoreDialogOpen = viewModel::setIsCategoryRestoreDialogOpen,
                        setIsCategoryDeleteDialogOpen = viewModel::setIsCategoryDeleteDialogOpen,
                        setSelectedCategory = viewModel::setSelectedCategory,
                        onRestoreClick = viewModel::restoreCategoryFromTrash,
                        onPermanentlyDeleteClick = viewModel::permanentlyDeleteCategory
                    )
                }
            }
        }
    }
}


@Composable
fun SavingsTrashScreen(
    savingsAccounts: Result<List<SavingsAccountUiState>>,
    onRestoreClick: (SavingsAccountUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (savingsAccounts) {
            is Result.Success -> {
                if (savingsAccounts.data.isNullOrEmpty())
                    Text(text = "Empty Content")
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(savingsAccounts.data, key = { it.id }) { transactionItem ->
                            SavingsAccountItem(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .padding(horizontal = 8.dp),
                                savingsAccount = transactionItem,
                                onEditClick = {  },
                                onDeleteClick = onRestoreClick
                            )
                        }
                    }
                }
            }
            is Result.Error -> { FullScreenError() }
            Result.Loading -> { FullScreenLoading() }
        }
    }
}

@Composable
fun TransactionsTrashScreen(
    transactions: Result<List<TransactionUiState>>,
    onRestoreClick: (TransactionUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (transactions) {
            is Result.Success -> {
                if (transactions.data.isNullOrEmpty())
                    Text(text = "Empty Content")
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(transactions.data, key = { it.id }) { transactionItem ->
                            TransactionItem(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .padding(horizontal = 8.dp),
                                transaction = transactionItem,
                                onEditClick = {  },
                                onDeleteClick = onRestoreClick
                            )
                        }
                    }
                }
            }
            is Result.Error -> { FullScreenError() }
            Result.Loading -> { FullScreenLoading() }
        }
    }
}

@Composable
fun CategoryTrashScreen(
    uiState: CategoryUiState,
    setIsCategoryRestoreDialogOpen: (Boolean) -> Unit,
    setIsCategoryDeleteDialogOpen: (Boolean) -> Unit,
    setSelectedCategory: (CategoryUi) -> Unit,
    onRestoreClick: (CategoryUi) -> Unit,
    onPermanentlyDeleteClick: (CategoryUi) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.deletedCategories) { deletedCategory ->
                TrashCategoryItem(
                    category = deletedCategory,
                    openRestoreDialog = {
                        setSelectedCategory(deletedCategory)
                        setIsCategoryRestoreDialogOpen(true)
                    },
                    openDeleteDialog =  {
                        setSelectedCategory(deletedCategory)
                        setIsCategoryDeleteDialogOpen(true)
                    }
                )
            }
        }
    }

    if (uiState.isRestoreDialogOpen) {
        uiState.selectedCategory?.let { category ->
            AlertDialog(
                onDismissRequest = { setIsCategoryRestoreDialogOpen(false) },
                title = { Text(text = "Återskapa kategorin?") },
                text = { Text(text = "Att återskapa kategorin gör den tillgänglig igen.") },
                dismissButton = {
                    TextButton(onClick = { setIsCategoryRestoreDialogOpen(false)}) {
                        Text(text = "Avbryt")
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onRestoreClick(category)
                            setIsCategoryRestoreDialogOpen(false)
                        }
                    ) { Text(text = "Återskapa") }
                }
            )
        }
    }

    if (uiState.isDeleteDialogOpen) {
        uiState.selectedCategory?.let { category ->
            AlertDialog(
                onDismissRequest = { setIsCategoryDeleteDialogOpen(false) },
                title = { Text(text = "Radera kategorin?") },
                text = { Text(text = "Kategorin raderas permanent.") },
                dismissButton = {
                    TextButton(onClick = { setIsCategoryDeleteDialogOpen(false)}) {
                        Text(text = "Avbryt")
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onPermanentlyDeleteClick(category)
                            setIsCategoryDeleteDialogOpen(false)
                        }
                    ) { Text(text = "Radera") }
                }
            )
        }
    }
}

@Composable
private fun TrashCategoryItem(
    category: CategoryUi,
    openRestoreDialog: () -> Unit,
    openDeleteDialog: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                tint = category.color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = category.name)
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Återskapa") },
                    onClick = {
                        openRestoreDialog()
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Restore,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ta bort") },
                    onClick = {
                        openDeleteDialog()
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.DeleteForever,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}