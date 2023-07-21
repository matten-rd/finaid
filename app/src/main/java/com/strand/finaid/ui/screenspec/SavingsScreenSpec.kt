package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.navigation.*
import com.strand.finaid.ui.savings.SavingsScreen
import kotlinx.coroutines.CoroutineScope

object SavingsScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Outlined.Wallet
    override val selectedIcon: ImageVector = Icons.Default.Wallet
    override val resourceId: Int = R.string.savings
    override val route: String = "main/savings"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                HomeScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughIn()
                else -> materialSharedAxisXIn(forward = false)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                HomeScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughOut()
                else -> materialSharedAxisXOut(forward = true)
            }
        }

}

fun NavGraphBuilder.savingsScreen(
    navController: NavController
) {
    composable(
        route = SavingsScreenSpec.route,
        arguments = SavingsScreenSpec.arguments,
        enterTransition = SavingsScreenSpec.enterTransition,
        exitTransition = SavingsScreenSpec.exitTransition
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FinaidMainTopAppBar(
                    titleId = R.string.savings,
                    onProfileClick = { navController.navigate(ProfileScreenSpec.route) },
                    onSearchClick = { navController.navigate(SearchScreenSpec.route) },
                    scrollBehavior = scrollBehavior
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                SavingsScreen(navigateToEditScreen = { id ->
                    navController.navigate("${AddEditSavingsScreenSpec.cleanRoute}?$SavingsAccountId={$id}")
                })
            }
        }
    }
}