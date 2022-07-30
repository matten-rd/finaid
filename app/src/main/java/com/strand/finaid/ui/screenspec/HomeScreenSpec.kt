package com.strand.finaid.ui.screenspec

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.R
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.home.HomeScreen
import com.strand.finaid.ui.navigation.materialFadeThroughIn
import com.strand.finaid.ui.navigation.materialFadeThroughOut
import kotlinx.coroutines.CoroutineScope

object HomeScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Rounded.Home
    override val resourceId: Int = R.string.screen_home
    override val route: String = "main/home"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                val scrollFraction = scrollBehavior.scrollFraction
                val appBarContainerColor by TopAppBarDefaults.centerAlignedTopAppBarColors()
                    .containerColor(scrollFraction = scrollFraction)
                Surface(color = appBarContainerColor) {
                    FinaidMainTopAppBar(
                        modifier = Modifier.topBarPadding(),
                        onProfileClick = { navController.navigate(ProfileScreenSpec.route) },
                        onSearchClick = { navController.navigate(SearchScreenSpec.route) },
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                HomeScreen(
                    onNavigateToTransactions = { navController.navigate(TransactionsScreenSpec.route) },
                    onNavigateToSavings = { navController.navigate(SavingsScreenSpec.route) }
                )
            }
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