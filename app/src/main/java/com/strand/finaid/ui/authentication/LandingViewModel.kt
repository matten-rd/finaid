package com.strand.finaid.ui.authentication

import com.google.android.gms.auth.api.identity.SignInClient
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    logService: LogService,
    accountService: AccountService,
    oneTapClient: SignInClient
) : AuthViewModel(logService, accountService, oneTapClient)