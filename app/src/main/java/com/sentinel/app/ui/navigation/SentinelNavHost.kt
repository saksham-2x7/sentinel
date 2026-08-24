package com.sentinel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sentinel.app.ui.screens.*
import com.sentinel.app.ui.viewmodel.ScanViewModel

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Home : Screen("home")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Result : Screen("result/{scanId}") {
        fun createRoute(scanId: Long) = "result/$scanId"
    }
}

@Composable
fun SentinelNavHost(viewModel: ScanViewModel) {
    val navController = rememberNavController()
    val isModelReady by viewModel.isModelReady.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isModelReady) Screen.Home.route else Screen.Setup.route
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                viewModel = viewModel,
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onScanComplete = { scanId ->
                    navController.navigate(Screen.Result.createRoute(scanId))
                },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("scanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0L
            ResultScreen(
                scanId = scanId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNewScan = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onScanClick = { scanId -> navController.navigate(Screen.Result.createRoute(scanId)) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { 
                    if (!viewModel.isModelReady.value) {
                        navController.navigate(Screen.Setup.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack() 
                    }
                }
            )
        }
    }
}
