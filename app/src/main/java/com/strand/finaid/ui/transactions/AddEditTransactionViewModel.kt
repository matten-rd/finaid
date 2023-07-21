package com.strand.finaid.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.mappers.asAddEditTransactionUiState
import com.strand.finaid.data.mappers.asTransaction
import com.strand.finaid.data.models.Category
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.CategoriesRepository
import com.strand.finaid.data.repository.TransactionsRepository
import com.strand.finaid.domain.GetTransactionColorsUseCase
import com.strand.finaid.ext.asHexCode
import com.strand.finaid.ext.idFromParameter
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.screenspec.TransactionDefaultId
import com.strand.finaid.ui.screenspec.TransactionId
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
    savedStateHandle: SavedStateHandle,
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
    getTransactionColorsUseCase: GetTransactionColorsUseCase
) : FinaidViewModel(logService) {

    val transactionTypes = TransactionType.values().map { it.title }

    var transactionType = mutableStateOf(TransactionType.Expense)
        private set

    fun onTransactionTypeChange(newValue: Int) {
        val newType = TransactionType.values()[newValue]
        addEditCategoryDialogUiState = when (newType) {
            TransactionType.Income -> addEditCategoryDialogUiState.copy(availableColors = transactionColors.incomeColors)
            TransactionType.Expense -> addEditCategoryDialogUiState.copy(availableColors = transactionColors.expenseColors)
        }
        transactionType.value = newType
    }

    val incomeCategories: StateFlow<List<Category>> = categoriesRepository.getIncomeCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val expenseCategories: StateFlow<List<Category>> = categoriesRepository.getExpenseCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val transactionId: String = savedStateHandle[TransactionId] ?: TransactionDefaultId

    var isEditMode by mutableStateOf(false)
        private set

    init {
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

    private val transactionColors = getTransactionColorsUseCase()

    var addEditCategoryDialogUiState by mutableStateOf(
            AddEditCategoryDialogUiState(
                availableColors = transactionColors.expenseColors,
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
            .copy(id = UUID.randomUUID().toString(), isOpen = false, isEdit = false, color = null, isColorError = false, name = "")
    }

    fun addTransactionCategory() {
        // No need to check if name is blank and color not null since button is disabled if they are
        val category = Category(
            id = addEditCategoryDialogUiState.id,
            name = addEditCategoryDialogUiState.name,
            color = addEditCategoryDialogUiState.color!!,
            transactionType = transactionType.value
        )
        viewModelScope.launch(showErrorExceptionHandler) {
            categoriesRepository.addCategory(category)
        }
        if (addEditCategoryDialogUiState.isEdit) {
            viewModelScope.launch {
                categoriesRepository.updateTransactionsWithNewCategory(
                    id = category.id,
                    name = category.name,
                    hexCode = category.color.asHexCode()
                )
            }
        }

        uiState = when (transactionType.value) {
            TransactionType.Income -> uiState.copy(incomeCategory = null)
            TransactionType.Expense -> uiState.copy(expenseCategory = null)
        }
        dismissDialog()
        SnackbarManager.showMessage(R.string.category_saved)
    }

    fun moveTransactionCategoryToTrash(categoryId: String) {
        setConfirmDeleteCategoryAlertDialogUiState(false)
        viewModelScope.launch(showErrorExceptionHandler) {
            categoriesRepository.moveCategoryToTrash(categoryId)
        }

        uiState = when (transactionType.value) {
            TransactionType.Income -> uiState.copy(incomeCategory = null)
            TransactionType.Expense -> uiState.copy(expenseCategory = null)
        }
        SnackbarManager.showMessage(R.string.category_removed)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val transaction = uiState.asTransaction(transactionType.value)

        if (transaction != null) {
            viewModelScope.launch { transactionsRepository.saveTransaction(transaction) }
            onSuccess()
        } else { SnackbarManager.showMessage(R.string.form_error) }
    }

    var isDeleteTransactionDialogOpen by mutableStateOf(false)
        private set

    fun setIsDeleteTransactionDialogOpen(newValue: Boolean) {
        isDeleteTransactionDialogOpen = newValue
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            transactionsRepository.moveTransactionToTrash(transactionId = transactionId)
        }
    }
}