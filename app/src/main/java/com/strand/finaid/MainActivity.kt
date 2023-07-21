package com.strand.finaid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.strand.finaid.ui.components.FinaidFloatingActionButton
import com.strand.finaid.ui.navigation.FinaidBottomNavigation
import com.strand.finaid.ui.navigation.FinaidNavGraph
import com.strand.finaid.ui.theme.FinaidTheme
import com.strand.finaid.ui.theme.isAppInDarkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val darkTheme = isAppInDarkTheme()
            val systemUiController = rememberSystemUiController()
            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.setSystemBarsColor(Color.Transparent, !darkTheme)
                onDispose {}
            }

            FinaidTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FinaidApp()
                }
            }
        }
    }
}

@Composable
fun FinaidApp() {
    val appState = rememberAppState()
    val navController = appState.navController
    val currentDestination = appState.currentDestination

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = appState.snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = appState.isBottomNavScreen,
                enter = fadeIn() + slideInVertically { it },
                exit = slideOutVertically { it } + fadeOut()
            ) {
                FinaidBottomNavigation(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        },
        floatingActionButton = {
           if (appState.isFabVisible) {
               FinaidFloatingActionButton(
                   currentDestination = currentDestination,
                   navController = navController
               )
           }
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FinaidNavGraph(navController)
        }
    }
}