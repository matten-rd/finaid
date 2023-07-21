package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import com.strand.finaid.ui.transactions.TransactionsScreen
import com.strand.finaid.ui.transactions.TransactionsSortBottomSheet
import com.strand.finaid.ui.trash.TrashBottomSheet
import com.strand.finaid.ui.trash.TrashScreen
import com.strand.finaid.ui.trash.TrashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TrashScreenSpec : ScreenSpec {
    override val route: String = "main/profile/trash"

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisXIn(forward = true) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisXOut(forward = false) }

}


fun NavGraphBuilder.trashScreen(
    navController: NavController
) {
    composable(
        route = TrashScreenSpec.route,
        arguments = TrashScreenSpec.arguments,
        enterTransition = TrashScreenSpec.enterTransition,
        exitTransition = TrashScreenSpec.exitTransition
    ) {
        val viewModel: TrashViewModel = hiltViewModel()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        val openBottomSheet = remember { mutableStateOf(false) }

        if (openBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet.value = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                TrashBottomSheet(
                    viewModel = viewModel,
                    onClose = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet.value = false
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.padding(bottom = 24.dp)) // FIXME: Change to adapt to navigationBar height
            }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.screen_trash)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TrashScreen(
                    viewModel = viewModel,
                    openSheet = { openBottomSheet.value = !openBottomSheet.value }
                )
            }
        }
    }
}