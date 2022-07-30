package com.strand.finaid.ui.authentication

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.strand.finaid.model.service.AccountService
import com.strand.finaid.model.service.LogService
import com.strand.finaid.ui.FinaidViewModel
import kotlinx.coroutines.launch


open class AuthViewModel(
    logService: LogService,
    private val accountService: AccountService,
    val oneTapClient: SignInClient
) : FinaidViewModel(logService,) {

    fun onOneTapSignInWithGoogleClick(onSuccess: (BeginSignInResult) -> Unit) {
        viewModelScope.launch {
            accountService.oneTapSignInWithGoogle { task ->
                if (task.isSuccessful) {
                    onSuccess(task.result)
                } else {
                    onOneTapSignUpWithGoogleClick(onSuccess)
                }
            }
        }
    }

    private fun onOneTapSignUpWithGoogleClick(onSuccess: (BeginSignInResult) -> Unit) {
        viewModelScope.launch {
            accountService.oneTapSignUpWithGoogle { task ->
                if (task.isSuccessful) {
                    onSuccess(task.result)
                } else {
                    task.exception?.let { onError(it) }
                }
            }
        }
    }

    fun signInWithCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        viewModelScope.launch {
            accountService.signInWithCredential(credential) { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let { onError(it) }
                }
            }
        }
    }

}