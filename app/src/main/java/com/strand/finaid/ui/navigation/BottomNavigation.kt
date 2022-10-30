package com.strand.finaid.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.strand.finaid.ui.screenspec.BottomNavScreenSpec

@Composable
fun FinaidBottomNavigation(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    NavigationBar {
        BottomNavScreenSpec.bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = { Icon(imageVector = if (selected) screen.selectedIcon else screen.icon, contentDescription = null) },
                label = { Text(text = stringResource(id = screen.resourceId)) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}