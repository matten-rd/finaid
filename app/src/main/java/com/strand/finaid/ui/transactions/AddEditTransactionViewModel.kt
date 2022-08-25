package com.strand.finaid.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.mappers.asAddEditTransactionUiState
import com.strand.finaid.data.mappers.asTransaction
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.ext.idFromParameter
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.screenspec.TransactionDefaultId
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

data class AddEditTransactionUiState(
    val id: String = UUID.randomUUID().toString(),
    val memo: String = "",
    val amount: String = "",
    val incomeCategory: Category? = null,
    val expenseCategory: Category? = null,
    val date: LocalDate = LocalDate.now(),
    val deleted: Boolean = false
)

data class AddEditCategoryDialogUiState(
    val id: String = UUID.randomUUID().toString(),
    val isOpen: Boolean = false,
    val isEdit: Boolean = false,
    val name: String = "",
    val color: Color? = null,
    val isColorError: Boolean = false,
    val availableColors: List<Color> = emptyList(),
    val disabledColors: List<Color> = emptyList()
)

data class DeleteCategoryDialogUiState(
    val isOpen: Boolean = false,
    val category: Category? = null
)

enum class TransactionType(val title: String) {
    Expense("Utgift"),
    Income("Inkomst")
}

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    logService: LogService,
    private val categoriesRepository: CategoriesRepository,
    private val accountService: AccountService,
    private val transactionsRepository: TransactionsRepository
) : FinaidViewModel(logService) {

    val transactionTypes = TransactionType.values().map { it.title }

    var transactionType = mutableStateOf(TransactionType.Expense)
        private set

    fun onTransactionTypeChange(newValue: Int) {
        val newType = TransactionType.values()[newValue]
        addEditCategoryDialogUiState = when (newType) {
            TransactionType.Income -> addEditCategoryDialogUiState.copy(availableColors = incomeColors)
            TransactionType.Expense -> addEditCategoryDialogUiState.copy(availableColors = expenseColors)
        }
        transactionType.value = newType
    }

    private val allCategories: Flow<Result<List<Category>>> = categoriesRepository
        .addCategoriesListener(accountService.getUserId())

    val incomeCategories: StateFlow<List<Category>> = allCategories.mapNotNull { result ->
        when (result) {
            is Result.Error -> listOf<Category>()
            Result.Loading -> listOf<Category>()
            is Result.Success -> result.data?.filter { it.transactionType == TransactionType.Income }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf<Category>()
    )

    val expenseCategories: StateFlow<List<Category>> = allCategories.mapNotNull { result ->
        when (result) {
            is Result.Error -> listOf<Category>()
            Result.Loading -> listOf<Category>()
            is Result.Success -> result.data?.filter { it.transactionType == TransactionType.Expense }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf<Category>()
    )

    var isEditMode by mutableStateOf(false)
        private set

    fun initialize(transactionId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            if (transactionId != TransactionDefaultId) {
                isEditMode = true
                val transaction = transactionsRepository.getTransactionById(transactionId.idFromParameter())
                transactionType.value = transaction.getTransactionType()
                uiState = transaction.asAddEditTransactionUiState()
            }
        }
    }

    var uiState by mutableStateOf(AddEditTransactionUiState())
        private set

    fun onMemoChange(newValue: String) {
        uiState = uiState.copy(memo = newValue)
    }

    fun onAmountChange(newValue: String) {
        uiState = uiState.copy(amount = newValue)
    }

    fun onIncomeCategoryChange(newValue: Category) {
        uiState = uiState.copy(incomeCategory = newValue)
    }

    fun onExpenseCategoryChange(newValue: Category) {
        uiState = uiState.copy(expenseCategory = newValue)
    }

    fun onDateChange(newValue: LocalDate) {
        uiState = uiState.copy(date = newValue)
    }

    var deleteCategoryDialogUiState by mutableStateOf(DeleteCategoryDialogUiState())

    fun setConfirmDeleteCategoryAlertDialogUiState(isOpen: Boolean, category: Category? = null) {
        if (category == null && isOpen) {
            SnackbarManager.showMessage(R.string.generic_error)
            return
        }
        deleteCategoryDialogUiState = deleteCategoryDialogUiState.copy(isOpen = isOpen, category = category)
    }

    private val incomeColors = listOf(
        Income1, Income2, Income3, Income4, Income5,
        Income6, Income7, Income8, Income9, Income10
    )

    private val expenseColors = listOf(
        Expense1, Expense2, Expense3, Expense4, Expense5,
        Expense6, Expense7, Expense8, Expense9, Expense10
    )

    var addEditCategoryDialogUiState by mutableStateOf(
            AddEditCategoryDialogUiState(
                availableColors = expenseColors,
                disabledColors = (incomeCategories.value + expenseCategories.value).map { it.color }
            ))
        private set

    fun setEditCategoryDialog(category: Category) {
        addEditCategoryDialogUiState = addEditCategoryDialogUiState
            .copy(isEdit = true, id = category.id, name = category.name, color = category.color)
        setIsAddEditCategoryDialogOpen(true)
    }

    fun setIsAddEditCategoryDialogOpen(newValue: Boolean) {
        addEditCategoryDialogUiState = addEditCategoryDialogUiState.copy(isOpen = newValue)
    }

    fun onColorSelected(newValue: Color) {
        val isColorError = newValue in (incomeCategories.value + expenseCategories.value).map { it.color }
        addEditCategoryDialogUiState = addEditCategoryDialogUiState.copy(color = newValue, isColorError = isColorError)
    }

    fun onCategoryNameChange(newValue: String) {
        addEditCategoryDialogUiState = addEditCategoryDialogUiState.copy(name = newValue)
    }

    fun dismissDialog() {
        addEditCategoryDialogUiState = addEditCategoryDialogUiState
            .copy(isOpen = false, isEdit = false, color = null, isColorError = false, name = "")
    }

    fun addTransactionCategory() {
        // No need to check if name is blank and color not null since button is disabled if they are
        val category = Category(
            id = addEditCategoryDialogUiState.id,
            name = addEditCategoryDialogUiState.name,
            color = addEditCategoryDialogUiState.color!!,
            transactionType = transactionType.value
        )

        categoriesRepository.addCategory(accountService.getUserId(), category) { error ->
            if (error == null) {
                SnackbarManager.showMessage(R.string.category_saved)
                uiState = when (transactionType.value) {
                    TransactionType.Income -> uiState.copy(incomeCategory = null)
                    TransactionType.Expense -> uiState.copy(expenseCategory = null)
                }
            } else { onError(error) }
        }
        dismissDialog()
    }

    fun moveTransactionCategoryToTrash(category: Category) {
        setConfirmDeleteCategoryAlertDialogUiState(false)
        categoriesRepository.moveCategoryToTrash(accountService.getUserId(), category.id) { error ->
            if (error == null) {
                SnackbarManager.showMessage(R.string.category_removed)
                uiState = when (transactionType.value) {
                    TransactionType.Income -> uiState.copy(incomeCategory = null)
                    TransactionType.Expense -> uiState.copy(expenseCategory = null)
                }
            } else { onError(error) }
        }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val transaction = uiState.asTransaction(transactionType.value)

        if (transaction != null) {
            transactionsRepository.saveTransaction(accountService.getUserId(), transaction) { error ->
                if (error == null) onSuccess() else onError(error)
            }
        } else { SnackbarManager.showMessage(R.string.form_error) }
    }

    var isDeleteTransactionDialogOpen by mutableStateOf(false)
        private set

    fun setIsDeleteTransactionDialogOpen(newValue: Boolean) {
        isDeleteTransactionDialogOpen = newValue
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(accountService.getUserId(), transactionId) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.transaction_removed)
                else
                    onError(error)
            }
        }
    }
}