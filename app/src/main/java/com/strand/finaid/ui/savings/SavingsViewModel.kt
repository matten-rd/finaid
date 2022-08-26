package com.strand.finaid.ui.savings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.Result
import com.strand.finaid.data.mapper.asSavingsAccountUiState
import com.strand.finaid.data.model.SavingsAccount
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavingsAccountUiState(
    val id: String,
    val icon: ImageVector = Icons.Default.Wallet,
    val color: Color,
    val amount: Int,
    val name: String,
    val bank: String,
)

sealed interface SavingsScreenUiState {
    data class Success(val savingsAccounts: List<SavingsAccountUiState>?) : SavingsScreenUiState
    object Error : SavingsScreenUiState
    object Loading : SavingsScreenUiState
}

@HiltViewModel
class SavingsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val savingsRepository: SavingsRepository
) : FinaidViewModel(logService) {
    private val _savingsAccountsUiState: Flow<SavingsScreenUiState> = savingsRepository
        .getSavingsAccountsStream(accountService.getUserId())
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

    fun onDeleteSavingsAccountClick(savingsAccount: SavingsAccountUiState) {
        viewModelScope.launch(showErrorExceptionHandler) {
            savingsRepository.moveSavingsAccountToTrash(accountService.getUserId(), savingsAccount.id) { error ->
                if (error == null)
                    SnackbarManager.showMessage(R.string.savingsaccount_removed)
                else
                    onError(error)
            }
        }
    }
}