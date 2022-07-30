package com.strand.finaid.ui.savings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import com.strand.finaid.R
import com.strand.finaid.model.Result
import com.strand.finaid.model.data.SavingsAccount
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.model.service.StorageService
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavingsAccountUiState(
    val id: String,
    val icon: ImageVector = Icons.Rounded.Wallet,
    val color: Color,
    val amount: Int,
    val name: String,
    val bank: String,
)

@HiltViewModel
class SavingsViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : FinaidViewModel(logService) {
    private val savingsAccountResponse: Flow<Result<List<SavingsAccountUiState>>> = storageService
        .addSavingsListener(accountService.getUserId())
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

    fun onDeleteSavingsAccountClick(savingsAccountId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.deleteSavingsAccount(accountService.getUserId(), savingsAccountId) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.savingsaccount_deleted)
                else
                    onError(error)
            }
        }
    }
}