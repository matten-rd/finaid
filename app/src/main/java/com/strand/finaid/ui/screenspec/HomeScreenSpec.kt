package com.strand.finaid.ui.screenspec

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.home.HomeScreen
import com.strand.finaid.ui.navigation.materialFadeThroughIn
import com.strand.finaid.ui.navigation.materialFadeThroughOut
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import kotlinx.coroutines.CoroutineScope

object HomeScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Outlined.Home
    override val selectedIcon: ImageVector = Icons.Default.Home
    override val resourceId: Int = R.string.screen_home
    override val route: String = "main/home"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                SavingsScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughIn()
                else -> materialSharedAxisXIn(forward = false)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                SavingsScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughOut()
                else -> materialSharedAxisXOut(forward = true)
            }
        }

}

fun NavGraphBuilder.homeScreen(
    navController: NavController
) {
    composable(
        route = HomeScreenSpec.route,
        arguments = HomeScreenSpec.arguments,
        enterTransition = HomeScreenSpec.enterTransition,
        exitTransition = HomeScreenSpec.exitTransition
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FinaidMainTopAppBar(
                    titleId = R.string.screen_home,
                    onProfileClick = { navController.navigate(ProfileScreenSpec.route) },
                    onSearchClick = { navController.navigate(SearchScreenSpec.route) },
                    scrollBehavior = scrollBehavior
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                HomeScreen(
                    onNavigateToTransactions = {
                        navController.navigate(TransactionsScreenSpec.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSavings = {
                        navController.navigate(SavingsScreenSpec.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}