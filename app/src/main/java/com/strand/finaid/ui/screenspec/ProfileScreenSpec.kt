package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.R
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.profile.ProfileScreen
import kotlinx.coroutines.CoroutineScope

object ProfileScreenSpec : ScreenSpec {
    override val route: String = "main/profile"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = { Text(text = stringResource(id = R.string.screen_profile)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                ProfileScreen(
                    navigateToLanding = { navController.navigate(LandingScreenSpec.route) { popUpTo(0) } },
                    navigateToTrash = { navController.navigate(TrashScreenSpec.route) }
                )
            }
        }
    }

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                TrashScreenSpec.route -> super.enterTransition(this)
                else -> slideIntoContainer(AnimatedContentScope.SlideDirection.Up)
            }
        }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                TrashScreenSpec.route -> super.popEnterTransition(this)
                else -> slideIntoContainer(AnimatedContentScope.SlideDirection.Down)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                TrashScreenSpec.route -> super.exitTransition(this)
                else -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Up)
            }
        }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                TrashScreenSpec.route -> super.popExitTransition(this)
                else -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Down)
            }
        }
}