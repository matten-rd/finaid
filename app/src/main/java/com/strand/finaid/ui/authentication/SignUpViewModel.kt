package com.strand.finaid.ui.authentication

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.strand.finaid.R
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.ui.components.textfield.ConfirmPasswordState
import com.strand.finaid.ui.components.textfield.EmailState
import com.strand.finaid.ui.components.textfield.PasswordState
import com.strand.finaid.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    oneTapClient: SignInClient
) : AuthViewModel(logService, accountService, oneTapClient) {

    val emailState = EmailState()
    val passwordState = PasswordState()
    val confirmPasswordState = ConfirmPasswordState(passwordState = passwordState)

    fun onSignUpWithEmailAndPasswordClick(onSuccess: () -> Unit) {
        if (!emailState.isValid) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (!passwordState.isValid) {
            SnackbarManager.showMessage(R.string.password_match_error)
            return
        }

        if (!confirmPasswordState.isValid) {
            SnackbarManager.showMessage(R.string.password_match_error)
            return
        }

        viewModelScope.launch {
            accountService.createUserWithEmailAndPassword(emailState.text, passwordState.text) { error ->
                if (error == null) {
                    onSuccess()
                } else {
                    onError(error)
                }
            }
        }
    }

}