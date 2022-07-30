package com.strand.finaid.ui.screenspec

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.R
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.authentication.FinaidSignUp
import kotlinx.coroutines.CoroutineScope

object SignupScreenSpec : ScreenSpec {
    override val route: String = "authentication/signup"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = { Text(text = stringResource(id = R.string.create_account)) }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                FinaidSignUp(
                    navigateToLogin = { navController.navigate(LoginScreenSpec.route) },
                    navigateToHome = { navController.navigate(HomeScreenSpec.route) { popUpTo(0) } }
                )
            }
        }
    }
}