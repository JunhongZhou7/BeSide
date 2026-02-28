package com.beside.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.beside.app.R
import com.beside.app.ui.screens.auth.AuthScreen
import com.beside.app.ui.screens.home.HomeScreen
import com.beside.app.ui.screens.pairing.PairingScreen
import com.beside.app.ui.screens.settings.SettingsScreen
import com.beside.app.ui.screens.timeline.TimelineScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Timeline : Screen("timeline")
    object Pairing : Screen("pairing")
    object Settings : Screen("settings")
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
                    title = { Text(stringResource(R.string.top_bar_title)) },
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
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Timeline.route,
                        onClick = { navController.navigate(Screen.Timeline.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Timeline, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_timeline)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Pairing.route,
                        onClick = { navController.navigate(Screen.Pairing.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_pairing)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_settings)) }
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
