package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import com.strand.finaid.ui.profile.ProfileScreen

object ProfileScreenSpec : ScreenSpec {
    override val route: String = "main/profile"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                TrashScreenSpec.route,
                ExportDataScreenSpec.route -> materialSharedAxisXIn(forward = false)
                else -> materialSharedAxisXIn(forward = true)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                TrashScreenSpec.route,
                ExportDataScreenSpec.route -> materialSharedAxisXOut(forward = true)
                else -> materialSharedAxisXOut(forward = false)
            }
        }

}

fun NavGraphBuilder.profileScreen(
    navController: NavController
) {
    composable(
        route = ProfileScreenSpec.route,
        arguments = ProfileScreenSpec.arguments,
        enterTransition = ProfileScreenSpec.enterTransition,
        exitTransition = ProfileScreenSpec.exitTransition
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { Text(text = stringResource(id = R.string.settings)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ProfileScreen(
                    navigateToTrash = { navController.navigate(TrashScreenSpec.route) },
                    navigateToExport = { navController.navigate(ExportDataScreenSpec.route) }
                )
            }
        }
    }
}