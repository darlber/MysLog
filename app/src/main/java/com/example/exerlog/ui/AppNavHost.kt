package com.example.exerlog.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exerlog.core.Routes
import com.example.exerlog.ui.home.HomeScreen
import com.example.exerlog.utils.UiEvent

@Composable
fun AppNavHost(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigate = { navController.navigationEvent(event = it) },
            )
        }

    }
}

private fun NavHostController.navigationEvent(event: UiEvent.Navigate) {}
