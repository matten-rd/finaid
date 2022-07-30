package com.strand.finaid.ui.screenspec

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.strand.finaid.topBarPadding
import com.strand.finaid.ui.trash.TrashScreen
import kotlinx.coroutines.CoroutineScope

object TrashScreenSpec : ScreenSpec {
    override val route: String = "main/profile/trash"


    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        bottomSheetState: ModalBottomSheetState,
        coroutineScope: CoroutineScope
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.topBarPadding(),
                    title = { Text("Papperskorgen") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                TrashScreen()
            }
        }

    }

}