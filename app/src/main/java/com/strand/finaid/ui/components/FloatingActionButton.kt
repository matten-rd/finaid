package com.strand.finaid.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.strand.finaid.ui.screenspec.BottomNavScreenSpec

@Composable
fun FinaidFAB(
    navController: NavController,
    currentDestination: NavDestination?
) {
    if (currentDestination != null) {
        val visible = remember(currentDestination.route) {
            MutableTransitionState(false)
                .apply { targetState = true }
        }
        val transition = updateTransition(transitionState = visible, label = "")
        transition.AnimatedVisibility(
            visible = { state -> state },
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            BottomNavScreenSpec.bottomNavItems
                .first { it.route == currentDestination.route }
                .Fab(navController = navController)
        }
    }
}