package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.ui.navigation.materialSharedAxisZIn
import com.strand.finaid.ui.navigation.materialSharedAxisZOut
import com.strand.finaid.ui.search.SearchField
import com.strand.finaid.ui.search.SearchScreen
import com.strand.finaid.ui.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope

object SearchScreenSpec : ScreenSpec {
    override val route: String = "main/search"

    @Composable
    override fun TopBar(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val viewModel: SearchViewModel = hiltViewModel(navBackStackEntry)
        val query by viewModel.queryFlow.collectAsState()
        SmallTopAppBar(
            title = {
                SearchField(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
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
            SearchScreen()
        }
    }

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisZIn(forward = true) }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisZIn(forward = true) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisZOut(forward = false) }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisZOut(forward = false) }
}