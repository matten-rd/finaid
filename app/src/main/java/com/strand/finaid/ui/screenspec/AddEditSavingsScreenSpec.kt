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
import com.strand.finaid.ui.savings.AddEditSavingsScreen
import com.strand.finaid.ui.savings.AddEditSavingsViewModel
import kotlinx.coroutines.CoroutineScope

object AddEditSavingsScreenSpec : ScreenSpec {
    const val cleanRoute = "main/savings/add_edit/"
    override val route: String = "$cleanRoute$SavingsAccountIdArg"
    override val arguments: List<NamedNavArgument>
        get() = listOf(navArgument(SavingsAccountId) { defaultValue = SavingsDefaultAccountId })

    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        val viewModel: AddEditSavingsViewModel = hiltViewModel()
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = { Text(text = if (viewModel.isEditMode) "Redigera sparkonto" else "Lägg till sparkonto") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        if (viewModel.isEditMode)
                            IconButton(onClick = { viewModel.setIsDeleteSavingsAccountDialogOpen(true) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                AddEditSavingsScreen(
                    viewModel = viewModel,
                    savingsAccountId = navBackStackEntry.arguments?.getString(SavingsAccountId) ?: SavingsDefaultAccountId,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
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

const val SavingsAccountId = "SavingsAccountId"
const val SavingsDefaultAccountId = "-2"
private const val SavingsAccountIdArg = "?$SavingsAccountId={$SavingsAccountId}"