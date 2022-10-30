package com.strand.finaid.ui.screenspec

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.R
import com.strand.finaid.ui.trash.TrashBottomSheet
import com.strand.finaid.ui.trash.TrashScreen
import com.strand.finaid.ui.trash.TrashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TrashScreenSpec : ScreenSpec {
    override val route: String = "main/profile/trash"

    @Composable
    override fun TopBar(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        SmallTopAppBar(
            title = { Text(stringResource(id = R.string.screen_trash)) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
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
        val viewModel: TrashViewModel = hiltViewModel()

        Column {
            TrashScreen(
                viewModel = viewModel,
                openSheet = { coroutineScope.launch { bottomSheetState.show() } }
            )
        }
    }

    @Composable
    override fun BottomSheetContent(
        bottomSheetState: ModalBottomSheetState,
        navBackStackEntry: NavBackStackEntry,
        coroutineScope: CoroutineScope
    ) {
        // retrieve the instance of the ViewModel in Content
        val viewModel: TrashViewModel = hiltViewModel(navBackStackEntry)
        TrashBottomSheet(
            viewModel = viewModel,
            bottomSheetState = bottomSheetState,
            scope = coroutineScope
        )
    }
}