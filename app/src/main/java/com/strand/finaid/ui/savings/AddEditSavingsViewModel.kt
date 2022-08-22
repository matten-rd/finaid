package com.strand.finaid.ui.savings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.mappers.asAddEditSavingsAccountUiState
import com.strand.finaid.data.mappers.asSavingsAccount
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.ext.idFromParameter
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.screenspec.SavingsDefaultAccountId
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class AddEditSavingsAccountUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val bank: String = "",
    val amount: String = "",
    val color: Color? = null,
    val deleted: Boolean = false
)

@HiltViewModel
class AddEditSavingsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val savingsRepository: SavingsRepository
) : FinaidViewModel(logService) {

    var isEditMode by mutableStateOf(false)
        private set

    fun initialize(savingsAccountId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            if (savingsAccountId != SavingsDefaultAccountId) {
                isEditMode = true
                val savingsAccount = savingsRepository.getSavingsAccountById(savingsAccountId.idFromParameter())
                uiState = savingsAccount.asAddEditSavingsAccountUiState()
            }
        }
    }

    var uiState by mutableStateOf(AddEditSavingsAccountUiState())
        private set

    fun onNameChange(newValue: String) {
        uiState = uiState.copy(name = newValue)
    }

    fun onBankChange(newValue: String) {
        uiState = uiState.copy(bank = newValue)
    }

    fun onAmountChange(newValue: String) {
        uiState = uiState.copy(amount = newValue)
    }

    fun onColorChange(newValue: Color) {
        uiState = uiState.copy(color = newValue)
    }

    val colors = listOf(
        Savings1, Savings2, Savings3, Savings4, Savings5,
        Savings6, Savings7, Savings8, Savings9, Savings10
    )

    fun saveSavingsAccount(onSuccess: () -> Unit) {
        val savingsAccount = uiState.asSavingsAccount()

        if (savingsAccount != null) {
            savingsRepository.saveSavingsAccount(accountService.getUserId(), savingsAccount) { error ->
                if (error == null) onSuccess() else onError(error)
            }
        } else {
            SnackbarManager.showMessage(R.string.form_error)
        }
    }

    var isDeleteSavingsAccountDialogOpen by mutableStateOf(false)
        private set

    fun setIsDeleteSavingsAccountDialogOpen(newValue: Boolean) {
        isDeleteSavingsAccountDialogOpen = newValue
    }

    fun onDeleteSavingsAccountClick(savingsAccountId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            savingsRepository.moveSavingsAccountToTrash(accountService.getUserId(), savingsAccountId) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.savingsaccount_removed)
                else
                    onError(error)
            }
        }
    }

}