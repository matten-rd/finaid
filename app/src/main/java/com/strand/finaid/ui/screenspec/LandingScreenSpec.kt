package com.strand.finaid.ui.screenspec

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.ui.authentication.FinaidLanding
import kotlinx.coroutines.CoroutineScope

object LandingScreenSpec : ScreenSpec {
    override val route: String = "authentication/landing"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        FinaidLanding(
            navigateToLogin = { navController.navigate(LoginScreenSpec.route) },
            navigateToSignUp = { navController.navigate(SignupScreenSpec.route) },
            navigateToHome = { navController.navigate(HomeScreenSpec.route) { popUpTo(0) } }
        )
    }
}