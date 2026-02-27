package com.beside.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.beside.app.ui.screens.auth.AuthScreen
import com.beside.app.ui.screens.home.HomeScreen
import com.beside.app.ui.screens.pairing.PairingScreen
import com.beside.app.ui.screens.settings.SettingsScreen
import com.beside.app.ui.screens.timeline.TimelineScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String, val title: String) {
    object Auth : Screen("auth", "登录")
    object Home : Screen("home", "首页")
    object Timeline : Screen("timeline", "ta的一天")
    object Pairing : Screen("pairing", "配对")
    object Settings : Screen("settings", "设置")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeSideNavHost() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDest = if (isLoggedIn) Screen.Home.route else Screen.Auth.route

    val showBars = currentRoute != Screen.Auth.route

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = { Text("在你身边 💕") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = { navController.navigate(Screen.Home.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("首页") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Timeline.route,
                        onClick = { navController.navigate(Screen.Timeline.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Timeline, contentDescription = null) },
                        label = { Text("ta的一天") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Pairing.route,
                        onClick = { navController.navigate(Screen.Pairing.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text("配对") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        label = { Text("设置") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Timeline.route) { TimelineScreen() }
            composable(Screen.Pairing.route) { PairingScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
