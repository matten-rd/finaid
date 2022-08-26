package com.strand.finaid.ui.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asSavingsAccountUiState
import com.strand.finaid.data.mapper.asTransactionUiState
import com.strand.finaid.data.model.Category
import com.strand.finaid.data.model.SavingsAccount
import com.strand.finaid.data.model.Transaction
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.savings.SavingsScreenUiState
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.transactions.TransactionScreenUiState
import com.strand.finaid.ui.transactions.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class TrashType(val title: String) {
    Savings("Sparkonton"),
    Transactions("Transaktioner"),
    Categories("Kategorier")
}

data class TrashCategoryUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedCategory: Category? = null,
)

data class TrashTransactionsUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedTransaction: TransactionUiState? = null
)

data class TrashSavingsAccountsUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedSavingsAccount: SavingsAccountUiState? = null
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    private val savingsRepository: SavingsRepository,
) : FinaidViewModel(logService) {

    val trashTypes = TrashType.values().map { it.title }

    var selectedTrashType by mutableStateOf(TrashType.Savings)
        private set

    fun onSelectedTrashTypeChange(newValue: Int) {
        selectedTrashType = TrashType.values()[newValue]
    }

    /**
     * Categories
     */
    val categories: StateFlow<Result<List<Category>>> =
        categoriesRepository.getDeletedCategoriesStream(accountService.getUserId())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )

    var trashCategoryUiState by mutableStateOf(TrashCategoryUiState())
        private set

    fun setIsCategoryRestoreDialogOpen(newValue: Boolean) {
        trashCategoryUiState = trashCategoryUiState.copy(isRestoreDialogOpen = newValue)
    }

    fun setIsCategoryDeleteDialogOpen(newValue: Boolean) {
        trashCategoryUiState = trashCategoryUiState.copy(isDeleteDialogOpen = newValue)
    }

    fun setSelectedCategory(newValue: Category) {
        trashCategoryUiState = trashCategoryUiState.copy(selectedCategory = newValue)
    }

    fun restoreCategoryFromTrash(category: Category) {
        categoriesRepository.restoreCategoryFromTrash(accountService.getUserId(), category.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.category_restored) else onError(error)
        }
    }

    fun permanentlyDeleteCategory(category: Category) {
        categoriesRepository.deleteCategoryPermanently(accountService.getUserId(), category.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.category_permanently_deleted) else onError(error)
        }
    }

    /**
     * Transactions
     */
    var trashTransactionsUiState by mutableStateOf(TrashTransactionsUiState())
        private set

    fun setIsTransactionRestoreDialogOpen(newValue: Boolean) {
        trashTransactionsUiState = trashTransactionsUiState.copy(isRestoreDialogOpen = newValue)
    }

    fun setIsTransactionDeleteDialogOpen(newValue: Boolean) {
        trashTransactionsUiState = trashTransactionsUiState.copy(isDeleteDialogOpen = newValue)
    }

    fun setSelectedTransaction(newValue: TransactionUiState) {
        trashTransactionsUiState = trashTransactionsUiState.copy(selectedTransaction = newValue)
    }

    private val _transactionsUiState: Flow<TransactionScreenUiState> = transactionsRepository
        .getDeletedTransactionsStream(accountService.getUserId())
        .map { result: Result<List<Transaction>> ->
            when (result) {
                is Result.Success -> TransactionScreenUiState.Success(
                    result.data?.map { it.asTransactionUiState() }
                )
                Result.Loading -> TransactionScreenUiState.Loading
                is Result.Error -> {
                    onError(result.exception)
                    TransactionScreenUiState.Error
                }
            }
        }

    val transactionsUiState: StateFlow<TransactionScreenUiState> = _transactionsUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionScreenUiState.Loading
        )

    fun restoreTransactionFromTrash(transaction: TransactionUiState) {
        transactionsRepository.restoreTransactionFromTrash(accountService.getUserId(), transaction.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.transaction_restored) else onError(error)
        }
    }

    fun permanentlyDeleteTransaction(transaction: TransactionUiState) {
        transactionsRepository.deleteTransactionPermanently(accountService.getUserId(), transaction.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.transaction_permanently_deleted) else onError(error)
        }
    }

    /**
     * Savings
     */
    var trashSavingsAccountsUiState by mutableStateOf(TrashSavingsAccountsUiState())
        private set

    fun setIsSavingsAccountRestoreDialogOpen(newValue: Boolean) {
        trashSavingsAccountsUiState = trashSavingsAccountsUiState.copy(isRestoreDialogOpen = newValue)
    }

    fun setIsSavingsAccountDeleteDialogOpen(newValue: Boolean) {
        trashSavingsAccountsUiState = trashSavingsAccountsUiState.copy(isDeleteDialogOpen = newValue)
    }

    fun setSelectedSavingsAccount(newValue: SavingsAccountUiState) {
        trashSavingsAccountsUiState = trashSavingsAccountsUiState.copy(selectedSavingsAccount = newValue)
    }

    private val _savingsAccountsUiState: Flow<SavingsScreenUiState> = savingsRepository
        .getDeletedSavingsAccountsStream(accountService.getUserId())
        .map { result: Result<List<SavingsAccount>> ->
            when (result) {
                is Result.Success -> {
                    SavingsScreenUiState.Success(result.data?.map { it.asSavingsAccountUiState() })
                }
                Result.Loading -> SavingsScreenUiState.Loading
                is Result.Error -> {
                    onError(result.exception)
                    SavingsScreenUiState.Error
                }
            }
        }

    val savingsAccountsUiState: StateFlow<SavingsScreenUiState> = _savingsAccountsUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavingsScreenUiState.Loading
        )

    fun restoreSavingsAccountFromTrash(savingsAccount: SavingsAccountUiState) {
        savingsRepository.restoreSavingsAccountFromTrash(accountService.getUserId(), savingsAccount.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.savingsaccount_restored) else onError(error)
        }
    }

    fun permanentlyDeleteSavingsAccount(savingsAccount: SavingsAccountUiState) {
        savingsRepository.deleteSavingsAccountPermanently(accountService.getUserId(), savingsAccount.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.savingsaccount_permanently_deleted) else onError(error)
        }
    }
}