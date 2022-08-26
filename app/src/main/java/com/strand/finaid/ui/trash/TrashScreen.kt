package com.strand.finaid.ui.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.model.Category
import com.strand.finaid.ui.components.FullScreenError
import com.strand.finaid.ui.components.FullScreenLoading
import com.strand.finaid.ui.components.list_items.BaseItem
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.savings.SavingsScreenUiState
import com.strand.finaid.ui.transactions.TransactionScreenUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import kotlinx.coroutines.launch

@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
    openSheet: () -> Unit
) {
    val initialPage = viewModel.selectedTrashType.ordinal
    val pagerState = rememberPagerState(initialPage = initialPage)
    val scope = rememberCoroutineScope()
    val transactions: TransactionScreenUiState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    val savingsAccounts: SavingsScreenUiState by viewModel.savingsAccountsUiState.collectAsStateWithLifecycle()
    val categories: Result<List<Category>> by viewModel.categories.collectAsStateWithLifecycle()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onSelectedTrashTypeChange(page)
        }
    }

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
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
                        uiState = viewModel.trashSavingsAccountsUiState,
                        openSheet = openSheet,
                        setIsSavingsAccountRestoreDialogOpen = viewModel::setIsSavingsAccountRestoreDialogOpen,
                        setIsSavingsAccountDeleteDialogOpen = viewModel::setIsSavingsAccountDeleteDialogOpen,
                        setSelectedSavingsAccount = viewModel::setSelectedSavingsAccount,
                        onRestoreClick = viewModel::restoreSavingsAccountFromTrash,
                        onPermanentlyDeleteClick = viewModel::permanentlyDeleteSavingsAccount
                    )
                    TrashType.Transactions.ordinal -> TransactionsTrashScreen(
                        transactions = transactions,
                        uiState = viewModel.trashTransactionsUiState,
                        openSheet = openSheet,
                        setIsTransactionRestoreDialogOpen = viewModel::setIsTransactionRestoreDialogOpen,
                        setIsTransactionDeleteDialogOpen = viewModel::setIsTransactionDeleteDialogOpen,
                        setSelectedTransaction = viewModel::setSelectedTransaction,
                        onRestoreClick = viewModel::restoreTransactionFromTrash,
                        onPermanentlyDeleteClick = viewModel::permanentlyDeleteTransaction
                    )
                    TrashType.Categories.ordinal -> CategoryTrashScreen(
                        categories = categories,
                        uiState = viewModel.trashCategoryUiState,
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
    savingsAccounts: SavingsScreenUiState,
    uiState: TrashSavingsAccountsUiState,
    openSheet: () -> Unit,
    setIsSavingsAccountRestoreDialogOpen: (Boolean) -> Unit,
    setIsSavingsAccountDeleteDialogOpen: (Boolean) -> Unit,
    setSelectedSavingsAccount: (SavingsAccountUiState) -> Unit,
    onRestoreClick: (SavingsAccountUiState) -> Unit,
    onPermanentlyDeleteClick: (SavingsAccountUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (savingsAccounts) {
            SavingsScreenUiState.Error -> { FullScreenError() }
            SavingsScreenUiState.Loading -> { FullScreenLoading() }
            is SavingsScreenUiState.Success -> {
                if (savingsAccounts.savingsAccounts.isNullOrEmpty())
                    Text(text = "Empty Content")
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(savingsAccounts.savingsAccounts, key = { it.id }) { savingsAccount ->
                            BaseItem(
                                modifier = Modifier.animateItemPlacement(),
                                icon = savingsAccount.icon,
                                color = savingsAccount.color,
                                header = savingsAccount.name,
                                subhead = savingsAccount.bank,
                                amount = savingsAccount.amount
                            ) {
                                setSelectedSavingsAccount(savingsAccount)
                                openSheet()
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.isRestoreDialogOpen) {
        uiState.selectedSavingsAccount?.let { savingsAccount ->
            AlertDialog(
                onDismissRequest = { setIsSavingsAccountRestoreDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.restore_savingsaccount)) },
                text = { Text(text = stringResource(id = R.string.savingsaccount_is_restored)) },
                dismissButton = {
                    TextButton(onClick = { setIsSavingsAccountRestoreDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onRestoreClick(savingsAccount)
                            setIsSavingsAccountRestoreDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.restore)) }
                }
            )
        }
    }

    if (uiState.isDeleteDialogOpen) {
        uiState.selectedSavingsAccount?.let { savingsAccount ->
            AlertDialog(
                onDismissRequest = { setIsSavingsAccountDeleteDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.delete_savingsaccount)) },
                text = { Text(text = stringResource(id = R.string.savingsaccount_will_be_permanently_deleted)) },
                dismissButton = {
                    TextButton(onClick = { setIsSavingsAccountDeleteDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onPermanentlyDeleteClick(savingsAccount)
                            setIsSavingsAccountDeleteDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
fun TransactionsTrashScreen(
    transactions: TransactionScreenUiState,
    uiState: TrashTransactionsUiState,
    openSheet: () -> Unit,
    setIsTransactionRestoreDialogOpen: (Boolean) -> Unit,
    setIsTransactionDeleteDialogOpen: (Boolean) -> Unit,
    setSelectedTransaction: (TransactionUiState) -> Unit,
    onRestoreClick: (TransactionUiState) -> Unit,
    onPermanentlyDeleteClick: (TransactionUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (transactions) {
            TransactionScreenUiState.Error -> { FullScreenError() }
            TransactionScreenUiState.Loading -> { FullScreenLoading() }
            is TransactionScreenUiState.Success -> {
                if (transactions.transactions.isNullOrEmpty())
                    Text(text = "Empty Content")
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(transactions.transactions, key = { it.id }) { transactionItem ->
                            BaseItem(
                                modifier = Modifier.animateItemPlacement(),
                                icon = transactionItem.icon,
                                color = transactionItem.color,
                                header = transactionItem.memo,
                                subhead = "${transactionItem.date} \u2022 ${transactionItem.category}",
                                amount = transactionItem.amount
                            ) {
                                setSelectedTransaction(transactionItem)
                                openSheet()
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.isRestoreDialogOpen) {
        uiState.selectedTransaction?.let { transaction ->
            AlertDialog(
                onDismissRequest = { setIsTransactionRestoreDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.restore_transaction)) },
                text = { Text(text = stringResource(id = R.string.transaction_is_restored)) },
                dismissButton = {
                    TextButton(onClick = { setIsTransactionRestoreDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onRestoreClick(transaction)
                            setIsTransactionRestoreDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.restore)) }
                }
            )
        }
    }

    if (uiState.isDeleteDialogOpen) {
        uiState.selectedTransaction?.let { transaction ->
            AlertDialog(
                onDismissRequest = { setIsTransactionDeleteDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.delete_transaction)) },
                text = { Text(text = stringResource(id = R.string.transaction_will_be_permanently_deleted)) },
                dismissButton = {
                    TextButton(onClick = { setIsTransactionDeleteDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onPermanentlyDeleteClick(transaction)
                            setIsTransactionDeleteDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
fun CategoryTrashScreen(
    categories: Result<List<Category>>,
    uiState: TrashCategoryUiState,
    setIsCategoryRestoreDialogOpen: (Boolean) -> Unit,
    setIsCategoryDeleteDialogOpen: (Boolean) -> Unit,
    setSelectedCategory: (Category) -> Unit,
    onRestoreClick: (Category) -> Unit,
    onPermanentlyDeleteClick: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (categories) {
            is Result.Error -> { FullScreenError() }
            Result.Loading -> { FullScreenLoading() }
            is Result.Success -> {
                if (categories.data.isNullOrEmpty())
                    Text(text = "Empty content")
                else
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories.data) { deletedCategory ->
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
        }
    }

    if (uiState.isRestoreDialogOpen) {
        uiState.selectedCategory?.let { category ->
            AlertDialog(
                onDismissRequest = { setIsCategoryRestoreDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.restore_category)) },
                text = { Text(text = stringResource(id = R.string.category_is_restored)) },
                dismissButton = {
                    TextButton(onClick = { setIsCategoryRestoreDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onRestoreClick(category)
                            setIsCategoryRestoreDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.restore)) }
                }
            )
        }
    }

    if (uiState.isDeleteDialogOpen) {
        uiState.selectedCategory?.let { category ->
            AlertDialog(
                onDismissRequest = { setIsCategoryDeleteDialogOpen(false) },
                title = { Text(text = stringResource(id = R.string.delete_category)) },
                text = { Text(text = stringResource(id = R.string.category_will_be_permanently_deleted)) },
                dismissButton = {
                    TextButton(onClick = { setIsCategoryDeleteDialogOpen(false)}) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            onPermanentlyDeleteClick(category)
                            setIsCategoryDeleteDialogOpen(false)
                        }
                    ) { Text(text = stringResource(id = R.string.delete)) }
                }
            )
        }
    }
}

@Composable
private fun TrashCategoryItem(
    category: Category,
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
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                tint = category.color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = category.name)
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.restore)) },
                    onClick = {
                        openRestoreDialog()
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.delete)) },
                    onClick = {
                        openDeleteDialog()
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
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