package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
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
    override val resourceId: Int = R.string.screen_transactions
    override val route: String = "main/transactions"

    @Composable
    override fun TopBar(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        FinaidMainTopAppBar(
            titleId = R.string.screen_transactions,
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
            TransactionsScreen(
                navigateToEditScreen = { id ->
                    navController.navigate("${AddEditTransactionScreenSpec.cleanRoute}?$TransactionId={$id}")
                },
                openSortSheet = { coroutineScope.launch { bottomSheetState.show() } }
            )
        }
    }

    @Composable
    override fun Fab(navController: NavController) {
        LargeFloatingActionButton(
            onClick = { navController.navigate(AddEditTransactionScreenSpec.cleanRoute) },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
        }
    }

    @Composable
    override fun BottomSheetContent(
        bottomSheetState: ModalBottomSheetState,
        navBackStackEntry: NavBackStackEntry,
        coroutineScope: CoroutineScope
    ) {
        // retrieve the instance of the ViewModel in Content
        val viewModel: TransactionsViewModel = hiltViewModel(navBackStackEntry)
        TransactionsSortBottomSheet(
            onClose = { coroutineScope.launch { bottomSheetState.hide() } },
            possibleSortOrders = viewModel.possibleSortOrders,
            selectedSortOrder = viewModel.sortFlow.collectAsState().value,
            onSelectedSortOrder = viewModel::onSetSortOrder
        )
    }


    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                HomeScreenSpec.route,
                SavingsScreenSpec.route -> materialFadeThroughIn()
                AddEditTransactionScreenSpec.route,
                ProfileScreenSpec.route -> materialSharedAxisZIn(forward = false)
                SearchScreenSpec.route -> materialElevationScaleIn()
                else -> super.enterTransition(this)
            }
        }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = enterTransition

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                HomeScreenSpec.route,
                SavingsScreenSpec.route -> materialFadeThroughOut()
                AddEditTransactionScreenSpec.route,
                ProfileScreenSpec.route -> materialSharedAxisZOut(forward = true)
                SearchScreenSpec.route -> materialElevationScaleOut()
                else -> super.exitTransition(this)
            }
        }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = exitTransition

}