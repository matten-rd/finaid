package com.strand.finaid.model.service.impl

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.strand.finaid.model.service.AccountService
import javax.inject.Inject
import javax.inject.Named

class AccountServiceImpl @Inject constructor(
    private var oneTapClient: SignInClient,
    @Named("SignInRequest") private var signInRequest: BeginSignInRequest,
    @Named("SignUpRequest") private var signUpRequest: BeginSignInRequest,
    private var signInClient: GoogleSignInClient
) : AccountService {
    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getUserId(): String {
        return Firebase.auth.currentUser?.uid.orEmpty()
    }

    override fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onResult: (Throwable?) -> Unit
    ) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onResult: (Throwable?) -> Unit
    ) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun signInWithCredential(credential: AuthCredential, onResult: (Task<AuthResult>) -> Unit) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task -> onResult(task) }
    }

    override fun oneTapSignInWithGoogle(onResult: (Task<BeginSignInResult>) -> Unit) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnCompleteListener { task -> onResult(task) }
    }

    override fun oneTapSignUpWithGoogle(onResult: (Task<BeginSignInResult>) -> Unit) {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnCompleteListener { task -> onResult(task) }
    }

    override fun sendRecoveryEmail(email: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun deleteUser(onResult: (Throwable?) -> Unit) {
        Firebase.auth.currentUser!!.delete()
            .addOnCompleteListener { task -> onResult(task.exception) }
    }

    override fun signOut(onResult: (Throwable?) -> Unit) {
        oneTapClient.signOut()
            .addOnCompleteListener { task -> onResult(task.exception) }
        Firebase.auth.signOut()
    }
}