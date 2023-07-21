package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import com.strand.finaid.ui.transactions.AddEditTransactionScreen
import com.strand.finaid.ui.transactions.AddEditTransactionViewModel
import com.strand.finaid.ui.transactions.CategoryBottomSheet
import kotlinx.coroutines.launch

object AddEditTransactionScreenSpec : ScreenSpec {
    const val cleanRoute = "main/transactions/add_edit/"
    override val route: String = "$cleanRoute$TransactionIdArg"
    override val arguments: List<NamedNavArgument>
        get() = listOf(navArgument(TransactionId) { defaultValue = TransactionDefaultId })

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisXIn(forward = true) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisXOut(forward = false) }

}

const val TransactionId = "TransactionId"
const val TransactionDefaultId = "-1"
private const val TransactionIdArg = "?$TransactionId={$TransactionId}"


fun NavGraphBuilder.addEditTransactionScreen(
    navController: NavController
) {
    composable(
        route = AddEditTransactionScreenSpec.route,
        arguments = AddEditTransactionScreenSpec.arguments,
        enterTransition = AddEditTransactionScreenSpec.enterTransition,
        exitTransition = AddEditTransactionScreenSpec.exitTransition
    ) {
        val viewModel: AddEditTransactionViewModel = hiltViewModel()
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        val openBottomSheet = remember { mutableStateOf(false) }

        if (openBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet.value = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                CategoryBottomSheet(
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
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = if (viewModel.isEditMode) R.string.edit_transaction else R.string.add_transaction)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        if (viewModel.isEditMode) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                            }
                            IconButton(onClick = { viewModel.setIsDeleteTransactionDialogOpen(true) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                AddEditTransactionScreen(
                    viewModel = viewModel,
                    openSheet = { openBottomSheet.value = !openBottomSheet.value },
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    }
}