package com.strand.finaid.ui.screenspec

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.TopAppBarScrollBehavior
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
            HomeScreenSpec,
            SavingsScreenSpec,
            TransactionsScreenSpec,
            ProfileScreenSpec,
            SearchScreenSpec,
            AddEditTransactionScreenSpec,
            AddEditSavingsScreenSpec,
            TrashScreenSpec
        ).associateBy { it.route }

        val subScreens: List<ScreenSpec> = listOf(
            ProfileScreenSpec,
            SearchScreenSpec,
            AddEditTransactionScreenSpec,
            AddEditSavingsScreenSpec,
            TrashScreenSpec
        )
    }

    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        }

    val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        }

    val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        }

    val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        }

    @Composable
    fun TopBar(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope,
        scrollBehavior: TopAppBarScrollBehavior
    )

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
    val selectedIcon: ImageVector

    @get:StringRes
    val resourceId: Int

    @Composable
    fun Fab(navController: NavController)

}