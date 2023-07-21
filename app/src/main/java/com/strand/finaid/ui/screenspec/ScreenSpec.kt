package com.strand.finaid.ui.screenspec

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SheetState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.ui.navigation.materialSharedAxisXIn
import com.strand.finaid.ui.navigation.materialSharedAxisXOut
import kotlinx.coroutines.CoroutineScope

sealed interface ScreenSpec {

    companion object {
        val allScreens: Map<String, ScreenSpec> = listOf<ScreenSpec>(
            HomeScreenSpec,
            SavingsScreenSpec,
            TransactionsScreenSpec,
            ProfileScreenSpec,
            SearchScreenSpec,
            AddEditTransactionScreenSpec,
            AddEditSavingsScreenSpec,
            TrashScreenSpec,
            ExportDataScreenSpec
        ).associateBy { it.route }
    }

    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()


    val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
        get() = {
            fadeIn(animationSpec = tween(300))
        }

    val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
        get() = {
            fadeOut(animationSpec = tween(300))
        }
}

sealed interface BottomNavScreenSpec : ScreenSpec {

    companion object {
        val bottomNavItems: List<BottomNavScreenSpec> =
            ScreenSpec.allScreens.values.filterIsInstance<BottomNavScreenSpec>()
    }

    val icon: ImageVector
    val selectedIcon: ImageVector

    @get:StringRes
    val resourceId: Int

}