package com.strand.finaid.ui.savings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.ext.idFromParameter
import com.strand.finaid.model.data.SavingsAccount
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.screenspec.SavingsDefaultAccountId
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

data class AddEditSavingsAccountUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val bank: String = "",
    val amount: String = "",
    val color: Color? = null,
    val deleted: Boolean = false
) {
    fun toSavingsAccount(): SavingsAccount? {
        return if (color != null && amount.toIntOrNull() != null)
            SavingsAccount(
                id = id,
                name = name,
                bank = bank,
                hexCode = String.format("%06X", color.toArgb() and 0xFFFFFF),
                amount = amount.toInt(),
                lastModified = Date.from(Instant.now()),
                deleted = deleted
            )
        else null
    }
}


@HiltViewModel
class AddEditSavingsViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : FinaidViewModel(logService) {

    var isEditMode by mutableStateOf(false)
        private set

    fun initialize(savingsAccountId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            if (savingsAccountId != SavingsDefaultAccountId) {
                isEditMode = true
                storageService.getSavingsAccount(
                    accountService.getUserId(), savingsAccountId.idFromParameter(), ::onError
                ) { savingsAccount ->
                    if (savingsAccount != null) {
                        uiState.value = savingsAccount.toAddEditSavingsAccountUiState()
                    }
                }
            }
        }
    }

    var uiState = mutableStateOf(AddEditSavingsAccountUiState())
        private set

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onBankChange(newValue: String) {
        uiState.value = uiState.value.copy(bank = newValue)
    }

    fun onAmountChange(newValue: String) {
        uiState.value = uiState.value.copy(amount = newValue)
    }

    fun onColorChange(newValue: Color) {
        uiState.value = uiState.value.copy(color = newValue)
    }

    val colors = listOf(
        Savings1, Savings2, Savings3, Savings4, Savings5,
        Savings6, Savings7, Savings8, Savings9, Savings10
    )

    fun saveSavingsAccount(onSuccess: () -> Unit) {
        val savingsAccount = uiState.value.toSavingsAccount()

        if (savingsAccount != null) {
            storageService.saveSavingsAccount(accountService.getUserId(), savingsAccount) { error ->
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
            storageService.moveSavingsAccountToTrash(accountService.getUserId(), savingsAccountId) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.savingsaccount_removed)
                else
                    onError(error)
            }
        }
    }

}