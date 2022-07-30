package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.navArgument
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.navigation.materialSharedAxisZIn
import com.strand.finaid.ui.navigation.materialSharedAxisZOut
import com.strand.finaid.ui.transactions.AddEditTransactionScreen
import com.strand.finaid.ui.transactions.AddEditTransactionViewModel
import com.strand.finaid.ui.transactions.CategoryBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AddEditTransactionScreenSpec : ScreenSpec {
    const val cleanRoute = "main/transactions/add_edit/"
    override val route: String = "$cleanRoute$TransactionIdArg"
    override val arguments: List<NamedNavArgument>
        get() = listOf(navArgument(TransactionId) { defaultValue = TransactionDefaultId })


    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        val viewModel: AddEditTransactionViewModel = hiltViewModel()

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = { Text(text = if (viewModel.isEditMode) "Redigera transaktion" else "Lägg till transaktion") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        if (viewModel.isEditMode) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null)
                            }
                            IconButton(onClick = { viewModel.setIsDeleteTransactionDialogOpen(true) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                AddEditTransactionScreen(
                    viewModel = viewModel,
                    transactionId = navBackStackEntry.arguments?.getString(TransactionId) ?: TransactionDefaultId,
                    openSheet = { coroutineScope.launch { bottomSheetState.show() } },
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    }


    @Composable
    override fun BottomSheetContent(
        bottomSheetState: ModalBottomSheetState,
        navBackStackEntry: NavBackStackEntry,
        coroutineScope: CoroutineScope
    ) {
        // retrieve the instance of the ViewModel in Content
        val viewModel: AddEditTransactionViewModel = hiltViewModel(navBackStackEntry)
        CategoryBottomSheet(
            viewModel = viewModel,
            bottomSheetState = bottomSheetState,
            scope = coroutineScope
        )
    }

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisZIn(forward = true) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisZOut(forward = false) }

    override val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisZIn(forward = true) }

    override val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisZOut(forward = false) }
}

const val TransactionId = "TransactionId"
const val TransactionDefaultId = "-1"
private const val TransactionIdArg = "?$TransactionId={$TransactionId}"