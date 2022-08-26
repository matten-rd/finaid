package com.strand.finaid.ui.splash

import androidx.lifecycle.ViewModel
import com.strand.finaid.data.network.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    fun onAppStart(
        onHasUser: () -> Unit,
        onHasNoUser: () -> Unit
    ) {
        if (accountService.hasUser())
            onHasUser()
        else
            onHasNoUser()
    }
}