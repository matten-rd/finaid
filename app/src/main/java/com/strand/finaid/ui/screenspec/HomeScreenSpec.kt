package com.strand.finaid.ui.screenspec

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.home.HomeScreen
import com.strand.finaid.ui.navigation.materialFadeThroughIn
import com.strand.finaid.ui.navigation.materialFadeThroughOut
import kotlinx.coroutines.CoroutineScope

object HomeScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Outlined.Home
    override val selectedIcon: ImageVector = Icons.Default.Home
    override val resourceId: Int = R.string.screen_home
    override val route: String = "main/home"

    @Composable
    override fun TopBar(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        FinaidMainTopAppBar(
            titleId = R.string.screen_home,
            onProfileClick = { navController.navigate(ProfileScreenSpec.route) },
            onSearchClick = { navController.navigate(SearchScreenSpec.route) },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        Column {
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

    @Composable
    override fun Fab(navController: NavController) {}

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                SavingsScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughIn() //slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
                ProfileScreenSpec.route,
                SearchScreenSpec.route -> scaleIn(initialScale = 0.9f) + fadeIn()
                else -> super.enterTransition(this)
            }
        }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = enterTransition

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                SavingsScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughOut()
                ProfileScreenSpec.route,
                SearchScreenSpec.route -> scaleOut(targetScale = 0.9f) + fadeOut()
                else -> super.exitTransition(this)
            }
        }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = exitTransition


}