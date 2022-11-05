package com.strand.finaid.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.strand.finaid.data.network.LogService
import com.strand.finaid.domain.HomeScreenUiState
import com.strand.finaid.domain.HomeScreenUiStateUseCase
import com.strand.finaid.ui.FinaidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    logService: LogService,
    private val homeScreenUiStateUseCase: HomeScreenUiStateUseCase
) : FinaidViewModel(logService) {

    var uiState: HomeScreenUiState by mutableStateOf(HomeScreenUiState.Loading)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        uiState = HomeScreenUiState.Error
        onError(throwable)
    }

    private fun initHomeScreenUiState() {
        viewModelScope.launch(exceptionHandler) {
            uiState = homeScreenUiStateUseCase()
        }
    }

    init {
        initHomeScreenUiState()
    }

}