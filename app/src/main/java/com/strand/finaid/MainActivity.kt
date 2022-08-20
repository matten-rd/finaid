package com.strand.finaid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.strand.finaid.ui.components.FinaidFAB
import com.strand.finaid.ui.navigation.FinaidBottomNavigation
import com.strand.finaid.ui.screenspec.ScreenSpec
import com.strand.finaid.ui.screenspec.SplashScreenSpec
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
    val navBackStackEntry by appState.navBackStackEntry
    val currentDestination = appState.currentDestination
    val bottomSheetState = appState.bottomSheetState
    val scope = appState.coroutineScope

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            if (navBackStackEntry != null) {
                ScreenSpec.allScreens[currentDestination?.route]
                    ?.BottomSheetContent(bottomSheetState, navBackStackEntry!!, scope)
            } else {
                Spacer(modifier = Modifier.height(1.dp)) // Used to fix anchor error with empty content
            }
        },
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
    ) {
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
                if (appState.isBottomNavScreen) {
                    FinaidFAB(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            }
        ) { innerPadding ->
            AnimatedNavHost(
                navController = navController,
                startDestination = SplashScreenSpec.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                ScreenSpec.allScreens.values.forEach { screen ->
                    composable(
                        route = screen.route,
                        arguments = screen.arguments,
                        enterTransition = screen.enterTransition,
                        exitTransition = screen.exitTransition,
                        popEnterTransition = screen.popEnterTransition,
                        popExitTransition = screen.popExitTransition
                    ) { navBackStackEntry ->
                        screen.Content(
                            navController,
                            navBackStackEntry,
                            bottomSheetState,
                            scope
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.topBarPadding() = composed {
    this.padding(
        WindowInsets.statusBars
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            .asPaddingValues()
    )
}
