package com.strand.finaid.ui.screenspec

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.strand.finaid.R
import com.strand.finaid.ui.components.FinaidMainTopAppBar
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import com.strand.finaid.ui.savings.AddEditSavingsScreen
import com.strand.finaid.ui.savings.AddEditSavingsViewModel
import com.strand.finaid.ui.transactions.TransactionsScreen
import kotlinx.coroutines.CoroutineScope

object AddEditSavingsScreenSpec : ScreenSpec {
    const val cleanRoute = "main/savings/add_edit/"
    override val route: String = "$cleanRoute$SavingsAccountIdArg"
    override val arguments: List<NamedNavArgument>
        get() = listOf(navArgument(SavingsAccountId) { defaultValue = SavingsDefaultAccountId })

    override val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { materialSharedAxisXIn(forward = true) }

    override val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { materialSharedAxisXOut(forward = false) }

}

const val SavingsAccountId = "SavingsAccountId"
const val SavingsDefaultAccountId = "-2"
private const val SavingsAccountIdArg = "?$SavingsAccountId={$SavingsAccountId}"

fun NavGraphBuilder.addEditSavingsScreen(
    navController: NavController
) {
    composable(
        route = AddEditSavingsScreenSpec.route,
        arguments = AddEditSavingsScreenSpec.arguments,
        enterTransition = AddEditSavingsScreenSpec.enterTransition,
        exitTransition = AddEditSavingsScreenSpec.exitTransition
    ) {
        val viewModel: AddEditSavingsViewModel = hiltViewModel()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = if (viewModel.isEditMode) R.string.edit_savingsaccount else R.string.add_savingsaccount)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        if (viewModel.isEditMode)
                            IconButton(onClick = { viewModel.setIsDeleteSavingsAccountDialogOpen(true) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0,0,0,0)
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                AddEditSavingsScreen(
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    }
}