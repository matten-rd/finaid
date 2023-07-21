package com.strand.finaid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.strand.finaid.FinaidAppState
import com.strand.finaid.ui.screenspec.*

@Composable
fun FinaidNavGraph(
    navController: NavHostController
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = HomeScreenSpec.route
    ) {
        mainGraph(navController)

        addEditSavingsScreen(navController)

        addEditTransactionScreen(navController)

        profileScreen(navController)
        exportDataScreen(navController)
        trashScreen(navController)

        searchScreen(navController)
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    homeScreen(navController)
    savingsScreen(navController)
    transactionsScreen(navController)
}