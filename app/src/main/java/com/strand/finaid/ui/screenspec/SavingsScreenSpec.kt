package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.R
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.navigation.*
import com.strand.finaid.ui.savings.SavingsScreen
import kotlinx.coroutines.CoroutineScope

object SavingsScreenSpec : BottomNavScreenSpec {
    override val icon: ImageVector = Icons.Outlined.Wallet
    override val selectedIcon: ImageVector = Icons.Default.Wallet
    override val resourceId: Int = R.string.screen_savings
    override val route: String = "main/savings"

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
                SavingsScreen(navigateToEditScreen = { id ->
                    navController.navigate("${AddEditSavingsScreenSpec.cleanRoute}?$SavingsAccountId={$id}")
                })
            }
        }
    }

    @Composable
    override fun Fab(navController: NavController) {
        LargeFloatingActionButton(
            onClick = { navController.navigate(AddEditSavingsScreenSpec.cleanRoute) },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
        }
    }

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                HomeScreenSpec.route,
                TransactionsScreenSpec.route -> materialFadeThroughIn()
                AddEditSavingsScreenSpec.route -> materialSharedAxisZIn(forward = false)
                ProfileScreenSpec.route,
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
                TransactionsScreenSpec.route -> materialFadeThroughOut()
                AddEditSavingsScreenSpec.route -> materialSharedAxisZOut(forward = true)
                ProfileScreenSpec.route,
                SearchScreenSpec.route -> materialElevationScaleOut()
                else -> super.exitTransition(this)
            }
        }
    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = exitTransition


}