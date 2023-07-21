package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import com.strand.finaid.ui.search.SearchField
import com.strand.finaid.ui.search.SearchScreen
import com.strand.finaid.ui.search.SearchViewModel
import com.strand.finaid.ui.transactions.TransactionsSortBottomSheet
import kotlinx.coroutines.launch

object SearchScreenSpec : ScreenSpec {
    override val route: String = "main/search"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            when (initialState.destination.route) {
                AddEditTransactionScreenSpec.route -> materialSharedAxisXIn(forward = false)
                else -> materialSharedAxisXIn(forward = true)
            }
        }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            when (targetState.destination.route) {
                AddEditTransactionScreenSpec.route -> materialSharedAxisXOut(forward = true)
                else -> materialSharedAxisXOut(forward = false)
            }
        }
}


fun NavGraphBuilder.searchScreen(
    navController: NavController
) {
    composable(
        route = SearchScreenSpec.route,
        arguments = SearchScreenSpec.arguments,
        enterTransition = SearchScreenSpec.enterTransition,
        exitTransition = SearchScreenSpec.exitTransition
    ) {
        val viewModel: SearchViewModel = hiltViewModel()
        val query by viewModel.queryFlow.collectAsState()
        val selectedSortOrder by viewModel.sortFlow.collectAsState()

        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        val openBottomSheet = remember { mutableStateOf(false) }

        if (openBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet.value = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                TransactionsSortBottomSheet(
                    onClose = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet.value = false
                            }
                        }
                    },
                    possibleSortOrders = viewModel.possibleSortOrders,
                    selectedSortOrder = selectedSortOrder,
                    onSelectedSortOrder = viewModel::onSetSortOrder
                )
                Spacer(modifier = Modifier.padding(bottom = 24.dp)) // FIXME: Change to adapt to navigationBar height
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        SearchField(
                            query = query,
                            onQueryChange = viewModel::onQueryChange
                        ) { navController.navigateUp() }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                SearchScreen(
                    viewModel = viewModel,
                    navigateToEditScreen = { id ->
                        navController.navigate("${AddEditTransactionScreenSpec.cleanRoute}?$TransactionId={$id}")
                    },
                    openSortSheet = { openBottomSheet.value = !openBottomSheet.value }
                )
            }
        }
    }
}