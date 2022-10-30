package com.strand.finaid.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.strand.finaid.data.network.LogService
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    logService: LogService
) : FinaidViewModel(logService) {

    var isThemeSelectionDialogOpen by mutableStateOf(false)
        private set

    fun setIsThemeSelectionDialogOpen(newValue: Boolean) {
        isThemeSelectionDialogOpen = newValue
    }

}