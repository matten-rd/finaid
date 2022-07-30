package com.strand.finaid.ui.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.strand.finaid.R

@Composable
fun FinaidLanding(
    navigateToLogin: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: LandingViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(R.string.landing),
            style = MaterialTheme.typography.displayLarge
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthProviders(navigateToHome = navigateToHome, viewModel = viewModel)

            Divider(color = MaterialTheme.colorScheme.outline)

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navigateToSignUp
            ) {
                Text(text = stringResource(R.string.create_account))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(onClick = navigateToLogin) {
                    Text(text = stringResource(R.string.sign_in))
                }
            }
        }
    }
}