package com.strand.finaid.ui.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun FinaidLogin(
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Email(emailState = viewModel.emailState)
            Password(passwordState = viewModel.passwordState, label = stringResource(id = R.string.password))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = viewModel::onForgotPasswordClick) {
                    Text(text = stringResource(R.string.forgot_password))
                }
                Button(
                    onClick = { viewModel.onSignInWithEmailAndPassword(onSuccess = navigateToHome) }
                ) {
                    Text(text = stringResource(id = R.string.sign_in))
                }
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
                    text = stringResource(R.string.no_account_yet),
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(onClick = navigateToSignUp) {
                    Text(text = stringResource(R.string.create_account))
                }
            }
        }
    }
}