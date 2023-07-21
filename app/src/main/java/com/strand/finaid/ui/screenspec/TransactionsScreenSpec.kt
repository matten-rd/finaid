package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.navigation.*
import com.strand.finaid.ui.transactions.TransactionsScreen
import com.strand.finaid.ui.transactions.TransactionsSortBottomSheet
import com.strand.finaid.ui.transactions.TransactionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TransactionsScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Outlined.CurrencyExchange
    override val selectedIcon: ImageVector = Icons.Default.CurrencyExchange
    override val resourceId: Int = R.string.transactions
    override val route: String = "main/transactions"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                HomeScreenSpec.route,
                SavingsScreenSpec.route -> materialFadeThroughIn()
                else -> materialSharedAxisXIn(forward = false)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                HomeScreenSpec.route,
                SavingsScreenSpec.route -> materialFadeThroughOut()
                else -> materialSharedAxisXOut(forward = true)
            }
        }

}

fun NavGraphBuilder.transactionsScreen(
    navController: NavController
) {
    composable(
        route = TransactionsScreenSpec.route,
        arguments = TransactionsScreenSpec.arguments,
        enterTransition = TransactionsScreenSpec.enterTransition,
        exitTransition = TransactionsScreenSpec.exitTransition
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FinaidMainTopAppBar(
                    titleId = R.string.transactions,
                    onProfileClick = { navController.navigate(ProfileScreenSpec.route) },
                    onSearchClick = { navController.navigate(SearchScreenSpec.route) },
                    scrollBehavior = scrollBehavior
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TransactionsScreen { id ->
                    navController.navigate("${AddEditTransactionScreenSpec.cleanRoute}?$TransactionId={$id}")
                }
            }
        }
    }
}