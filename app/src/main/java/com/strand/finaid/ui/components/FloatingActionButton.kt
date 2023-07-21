package com.strand.finaid.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.strand.finaid.ui.screenspec.*

@Composable
fun FinaidFloatingActionButton(
    currentDestination: NavDestination?,
    navController: NavController
) {
    if (currentDestination != null) {
        val visible = remember(currentDestination.route) {
            MutableTransitionState(false)
                .apply { targetState = true }
        }
        val transition = updateTransition(transitionState = visible, label = "")
        transition.AnimatedVisibility(
            visible = { state -> state },
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            when (currentDestination.route) {
                SavingsScreenSpec.route -> {
                    LargeFloatingActionButton(
                        onClick = { navController.navigate(AddEditSavingsScreenSpec.cleanRoute) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
                    }
                }
                TransactionsScreenSpec.route -> {
                    LargeFloatingActionButton(
                        onClick = { navController.navigate(AddEditTransactionScreenSpec.cleanRoute) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
                    }
                }
                else -> Unit
            }
        }
    }
}