package com.strand.finaid.ui.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.R
import com.strand.finaid.model.Result
import com.strand.finaid.model.data.SavingsAccount
import com.strand.finaid.model.data.Transaction
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.savings.SavingsAccountUiState
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.transactions.CategoryUi
import com.strand.finaid.ui.transactions.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TrashType(val title: String) {
    Savings("Sparkonton"),
    Transactions("Transaktioner"),
    Categories("Kategorier")
}

data class CategoryUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedCategory: CategoryUi? = null,
    val deletedCategories: SnapshotStateList<CategoryUi> = mutableStateListOf()
)

data class TransactionsUiState(
    val isRestoreDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val selectedTransaction: CategoryUi? = null,
    val deletedTransactions: SnapshotStateList<CategoryUi> = mutableStateListOf()
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService
) : FinaidViewModel(logService) {

    val trashTypes = TrashType.values().map { it.title }

    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    private fun getDeletedCategories() {
        categoryUiState.deletedCategories.clear()
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.getDeletedCategories(accountService.getUserId(), ::onError) { category ->
                categoryUiState.deletedCategories.add(category.toCategoryUi())
            }
        }
    }

    init { getDeletedCategories() }

    fun setIsCategoryRestoreDialogOpen(newValue: Boolean) {
        categoryUiState = categoryUiState.copy(isRestoreDialogOpen = newValue)
    }

    fun setIsCategoryDeleteDialogOpen(newValue: Boolean) {
        categoryUiState = categoryUiState.copy(isDeleteDialogOpen = newValue)
    }

    fun setSelectedCategory(newValue: CategoryUi) {
        categoryUiState = categoryUiState.copy(selectedCategory = newValue)
    }

    fun restoreCategoryFromTrash(category: CategoryUi) {
        storageService.restoreCategoryFromTrash(accountService.getUserId(), category.id) { error ->
            if (error == null) {
                SnackbarManager.showMessage(R.string.category_restored)
                getDeletedCategories()
            } else { onError(error) }
        }
    }

    fun permanentlyDeleteCategory(category: CategoryUi) {
        storageService.deleteTransactionCategoryPermanently(accountService.getUserId(), category.id) { error ->
            if (error == null) {
                SnackbarManager.showMessage(R.string.category_permanently_deleted)
                getDeletedCategories()
            } else { onError(error) }
        }
    }

    private val transactionResult: Flow<Result<List<TransactionUiState>>> = storageService
        .addTransactionsListener(accountService.getUserId(), deleted = true)
        .map { result ->
            when (result) {
                is Result.Success -> Result.Success(
                    result.data?.toObjects<Transaction>()?.map { it.toTransactionUiState() }
                )
                Result.Loading -> Result.Loading
                is Result.Error -> {
                    onError(result.exception)
                    result
                }
            }
        }

    val transactions: StateFlow<Result<List<TransactionUiState>>> = transactionResult
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun restoreTransactionFromTrash(transaction: TransactionUiState) {
        storageService.restoreTransactionFromTrash(accountService.getUserId(), transaction.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.transaction_restored) else onError(error)
        }
    }

    private val savingsAccountResponse: Flow<Result<List<SavingsAccountUiState>>> = storageService
        .addSavingsListener(accountService.getUserId(), deleted = true)
        .map { res ->
            when (res) {
                is Result.Success -> {
                    Result.Success(
                        res.data?.toObjects<SavingsAccount>()?.map { it.toSavingsAccountUiState() })
                }
                is Result.Loading -> { Result.Loading }
                is Result.Error -> {
                    onError(res.exception)
                    res
                }
            }
        }

    val savingsAccounts: StateFlow<Result<List<SavingsAccountUiState>>> = savingsAccountResponse
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun restoreSavingsAccountFromTrash(savingsAccount: SavingsAccountUiState) {
        storageService.restoreSavingsAccountFromTrash(accountService.getUserId(), savingsAccount.id) { error ->
            if (error == null) SnackbarManager.showMessage(R.string.savingsaccount_restored) else onError(error)
        }
    }
}