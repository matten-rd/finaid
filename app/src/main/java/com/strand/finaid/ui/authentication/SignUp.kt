package com.strand.finaid.ui.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R

@Composable
fun FinaidSignUp(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Email(emailState = viewModel.emailState)
            Password(passwordState = viewModel.passwordState, label = stringResource(id = R.string.password))
            Password(passwordState = viewModel.confirmPasswordState, label = stringResource(id = R.string.confirm_password))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.onSignUpWithEmailAndPasswordClick(onSuccess = navigateToHome) }
            ) {
                Text(text = stringResource(id = R.string.create_account))
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthProviders(navigateToHome = navigateToHome, viewModel = viewModel)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.already_have_account),
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(onClick = navigateToLogin) {
                    Text(text = stringResource(id = R.string.sign_in))
                }
            }
        }
    }
}