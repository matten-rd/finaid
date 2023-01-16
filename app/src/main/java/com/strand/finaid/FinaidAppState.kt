package com.strand.finaid

import android.content.res.Resources
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.strand.finaid.ui.screenspec.BottomNavScreenSpec
import com.strand.finaid.ui.snackbar.SnackbarManager
import com.strand.finaid.ui.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class FinaidAppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    val coroutineScope: CoroutineScope,
    val bottomSheetState: ModalBottomSheetState
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val data = snackbarMessage.toMessage(resources)
                val snackbarResult = snackbarHostState
                    .showSnackbar(
                        message = data.message,
                        withDismissAction = true,
                        actionLabel = data.actionLabel,
                    )

                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    data.actionPerformed
                }
            }
        }
    }

    val navBackStackEntry: State<NavBackStackEntry?>
        @Composable get() = navController.currentBackStackEntryAsState()

    val currentDestination: NavDestination?
        @Composable get() = navBackStackEntry.value?.destination

    val isBottomNavScreen: Boolean
        @Composable get() = currentDestination?.route in BottomNavScreenSpec.bottomNavItems.map { it.route }

}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberAnimatedNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
) = remember(snackbarHostState) {
    FinaidAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope, bottomSheetState)
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}