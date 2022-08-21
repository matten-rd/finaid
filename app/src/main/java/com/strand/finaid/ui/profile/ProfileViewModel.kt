package com.strand.finaid.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService
) : FinaidViewModel(logService) {

    var isThemeSelectionDialogOpen by mutableStateOf(false)
        private set

    fun setIsThemeSelectionDialogOpen(newValue: Boolean) {
        isThemeSelectionDialogOpen = newValue
    }

    fun onSignOutClick(onSuccess: () -> Unit) {
        viewModelScope.launch {
            accountService.signOut { error ->
                if (error == null)
                    onSuccess()
                else
                    onError(error)
            }
        }
    }
}