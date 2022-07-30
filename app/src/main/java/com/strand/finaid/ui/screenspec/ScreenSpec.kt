package com.strand.finaid.ui.screenspec

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

sealed interface ScreenSpec {

    companion object {
        val allScreens: Map<String, ScreenSpec> = listOf<ScreenSpec>(
            SplashScreenSpec,
            HomeScreenSpec,
            SavingsScreenSpec,
            TransactionsScreenSpec,
            ProfileScreenSpec,
            SearchScreenSpec,
            LandingScreenSpec,
            LoginScreenSpec,
            SignupScreenSpec,
            AddEditTransactionScreenSpec,
            AddEditSavingsScreenSpec,
            TrashScreenSpec
        ).associateBy { it.route }
    }

    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left) }

    val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { slideIntoContainer(AnimatedContentScope.SlideDirection.Right) }

    val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Left) }

    val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right) }

    @Composable
    fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    )

    @Composable
    fun BottomSheetContent(
        bottomSheetState: ModalBottomSheetState,
        navBackStackEntry: NavBackStackEntry,
        coroutineScope: CoroutineScope
    ) { Spacer(modifier = Modifier.height(1.dp)) }

}

sealed interface BottomNavScreenSpec : ScreenSpec {

    companion object {
        val bottomNavItems: List<BottomNavScreenSpec> =
            ScreenSpec.allScreens.values.filterIsInstance<BottomNavScreenSpec>()
    }

    val icon: ImageVector

    @get:StringRes
    val resourceId: Int

    @Composable
    fun Fab(navController: NavController)

}