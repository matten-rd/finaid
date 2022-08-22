package com.strand.finaid.ui.savings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.strand.finaid.R
import com.strand.finaid.data.local.entities.SavingsAccountEntity
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.repository.SavingsRepository
import com.strand.finaid.domain.SavingsScreenUiState
import com.strand.finaid.domain.SavingsScreenUiStateUseCase
import com.strand.finaid.ui.FinaidViewModel
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

@HiltViewModel
class SavingsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val savingsRepository: SavingsRepository,
    savingsScreenUiStateUseCase: SavingsScreenUiStateUseCase
) : FinaidViewModel(logService) {
    val savingsAccountsUiState: StateFlow<SavingsScreenUiState> = savingsScreenUiStateUseCase()
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

    fun addListener() {
        viewModelScope.launch {
            savingsRepository.addSavingsAccountsListener(accountService.getUserId(), false, ::onDocumentEvent)
        }
    }

    fun removeListener() {
        viewModelScope.launch { savingsRepository.removeListener() }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, savingsAccount: SavingsAccountEntity) {
        viewModelScope.launch {
            savingsRepository.updateLocalDatabase(wasDocumentDeleted, savingsAccount)
        }
    }
}