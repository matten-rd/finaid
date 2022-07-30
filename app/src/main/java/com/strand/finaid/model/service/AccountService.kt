package com.strand.finaid.model.service

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface AccountService {
    fun hasUser(): Boolean
    fun getUserId(): String
    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun createUserWithEmailAndPassword(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun signInWithCredential(credential: AuthCredential, onResult: (Task<AuthResult>) -> Unit)
    fun oneTapSignInWithGoogle(onResult: (Task<BeginSignInResult>) -> Unit)
    fun oneTapSignUpWithGoogle(onResult: (Task<BeginSignInResult>) -> Unit)
    fun sendRecoveryEmail(email: String, onResult: (Throwable?) -> Unit)
    fun deleteUser(onResult: (Throwable?) -> Unit)
    fun signOut(onResult: (Throwable?) -> Unit)
}