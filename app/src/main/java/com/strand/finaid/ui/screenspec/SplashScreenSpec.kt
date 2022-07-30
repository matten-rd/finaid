package com.strand.finaid.ui.screenspec

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.ui.splash.SplashScreen
import com.strand.finaid.ui.splash.SplashViewModel
import kotlinx.coroutines.CoroutineScope

object SplashScreenSpec : ScreenSpec {
    override val route: String = "splash"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        val viewModel: SplashViewModel = hiltViewModel(navBackStackEntry)
        LaunchedEffect(Unit) {
            viewModel.onAppStart(
                onHasUser = { navController.navigate(HomeScreenSpec.route) { popUpTo(0) } },
                onHasNoUser = { navController.navigate(LandingScreenSpec.route) { popUpTo(0) } }
            )
        }
        SplashScreen()
    }
}