package com.strand.finaid.ui.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.SavingsScreenUiState
import com.strand.finaid.domain.SavingsScreenUiStateUseCase
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionsUseCase
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.transactions.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TrashType(val title: String) {
    Savings("Sparkonton"),
    Transactions("Transaktioner"),
    Categories("Kategorier")
}

data class TrashCategoryUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedCategory: Category? = null
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
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    private val savingsRepository: SavingsRepository,
    transactionsUseCase: TransactionsUseCase,
    savingsScreenUiStateUseCase: SavingsScreenUiStateUseCase
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
    val categories: StateFlow<List<Category>> = categoriesRepository.getDeletedCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
        viewModelScope.launch(showErrorExceptionHandler) {
            categoriesRepository.restoreCategoryFromTrash(category.id)
            SnackbarManager.showMessage(R.string.category_restored)
        }
    }

    fun permanentlyDeleteCategory(category: Category) {
        viewModelScope.launch {
            categoriesRepository.deleteCategoryPermanently(category.id)
            SnackbarManager.showMessage(R.string.category_permanently_deleted)
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

    val transactionsUiState: StateFlow<TransactionScreenUiState> = transactionsUseCase(deleted = true)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionScreenUiState(isLoading = true)
        )

    fun restoreTransactionFromTrash(transaction: TransactionUiState) {
        viewModelScope.launch {
            transactionsRepository.restoreTransactionFromTrash(transactionId = transaction.id)
        }
    }

    fun permanentlyDeleteTransaction(transaction: TransactionUiState) {
        viewModelScope.launch {
            transactionsRepository.deleteTransactionPermanently(transactionId = transaction.id)
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

    val savingsAccountsUiState: StateFlow<SavingsScreenUiState> = savingsScreenUiStateUseCase(deleted = true)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavingsScreenUiState(isLoading = true)
        )

    fun restoreSavingsAccountFromTrash(savingsAccount: SavingsAccountUiState) {
        viewModelScope.launch {
            savingsRepository.restoreSavingsAccountFromTrash(savingsAccountId = savingsAccount.id)
        }
    }

    fun permanentlyDeleteSavingsAccount(savingsAccount: SavingsAccountUiState) {
        viewModelScope.launch {
            savingsRepository.deleteSavingsAccountPermanently(savingsAccountId = savingsAccount.id)
        }
    }

}