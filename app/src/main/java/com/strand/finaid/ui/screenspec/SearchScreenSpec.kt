package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.search.SearchField
import com.strand.finaid.ui.search.SearchScreen
import com.strand.finaid.ui.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope

object SearchScreenSpec : ScreenSpec {
    override val route: String = "main/search"

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        Scaffold(
            topBar = {
                val viewModel: SearchViewModel = hiltViewModel(navBackStackEntry)
                val query by viewModel.queryFlow.collectAsState()
                SmallTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = {
                        SearchField(
                            query = query,
                            onQueryChange = viewModel::onQueryChange,
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                SearchScreen()
            }
        }
    }

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { slideIntoContainer(AnimatedContentScope.SlideDirection.Up) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Up) }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { slideIntoContainer(AnimatedContentScope.SlideDirection.Down) }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Down) }
}