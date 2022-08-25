package com.strand.finaid.ui.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.local.entities.TransactionEntity
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.SavingsScreenUiState
import com.strand.finaid.domain.SavingsScreenUiStateUseCase
import com.strand.finaid.domain.TransactionScreenUiState
import com.strand.finaid.domain.TransactionScreenUiStateUseCase
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
    private val accountService: AccountService,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    private val savingsRepository: SavingsRepository,
    transactionScreenUiStateUseCase: TransactionScreenUiStateUseCase,
    savingsScreenUiStateUseCase: SavingsScreenUiStateUseCase
) : FinaidViewModel(logService) {

    val trashTypes = TrashType.values().map { it.title }

    var selectedTrashType by mutableStateOf(TrashType.Savings)
        private set

    fun onSelectedTrashTypeChange(newValue: Int) {
        selectedTrashType = TrashType.values()[newValue]
    }

    fun addListener() {
        viewModelScope.launch {
            val lastModifiedTransactionDate = transactionsRepository.getLastModifiedDate()
            val lastModifiedSavingsAccountDate = savingsRepository.getLastModifiedDate()
            transactionsRepository.addTransactionsListener(accountService.getUserId(), lastModifiedTransactionDate,true, ::onTransactionDocumentEvent)
            savingsRepository.addSavingsAccountsListener(accountService.getUserId(), lastModifiedSavingsAccountDate,true, ::onSavingsAccountDocumentEvent)
        }
    }

    fun removeListener() {
        viewModelScope.launch {
            transactionsRepository.removeListener()
            savingsRepository.removeListener()
        }
    }

    /**
     * Categories
     */
    val categories: StateFlow<Result<List<Category>>> = categoriesRepository
        .addCategoriesListener(accountService.getUserId(), true)
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

    val transactionsUiState: StateFlow<TransactionScreenUiState> = transactionScreenUiStateUseCase(deleted = true)
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

    private fun onTransactionDocumentEvent(wasDocumentDeleted: Boolean, transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionsRepository.updateLocalDatabase(wasDocumentDeleted, transaction)
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

    private fun onSavingsAccountDocumentEvent(wasDocumentDeleted: Boolean, savingsAccount: SavingsAccountEntity) {
        viewModelScope.launch {
            savingsRepository.updateLocalDatabase(wasDocumentDeleted, savingsAccount)
        }
    }
}