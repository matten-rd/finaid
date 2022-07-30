package com.strand.finaid.ui.authentication

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.GoogleAuthProvider
import com.strand.finaid.ui.components.textfield.FinaidTextField
import com.strand.finaid.ui.components.textfield.TextFieldState

@Composable
fun AuthProviders(
    navigateToHome: () -> Unit,
    viewModel: AuthViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
            val googleIdToken = credentials.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            viewModel.signInWithCredential(firebaseCredential) { navigateToHome() }
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.onOneTapSignInWithGoogleClick(onSuccess = { launch(it) }) }
        ) {
            Text(text = "Fortsätt med Google")
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Fortsätt som gäst")
        }
    }
}

@Composable
fun Email(emailState: TextFieldState) {
    FinaidTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) emailState.enableShowErrors()
            },
        value = emailState.text,
        onValueChange = { emailState.text = it },
        isError = emailState.showErrors(),
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Email,
        label = "Email",
        errorMessage = emailState.getError()
    )
}

@Composable
fun Password(
    passwordState: TextFieldState,
    label: String
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    FinaidTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) passwordState.enableShowErrors()
            },
        value = passwordState.text,
        onValueChange = { passwordState.text = it },
        label = label,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordHidden)
            PasswordVisualTransformation() else VisualTransformation.None,
        isError = passwordState.showErrors(),
        errorMessage = passwordState.getError(),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon = if (passwordHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                Icon(imageVector = visibilityIcon, contentDescription = null)
            }
        }
    )
}