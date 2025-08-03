package com.example.exerlog.ui


import android.util.Log
import com.example.exerlog.ui.exercisepicker.ExercisePicker
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.exerlog.core.Routes
import com.example.exerlog.ui.home.HomeScreen
import com.example.exerlog.ui.session.SessionScreen
import com.example.exerlog.ui.settings.SettingsScreen
import com.example.exerlog.utils.UiEvent
import timber.log.Timber

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
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(
            route = "${Routes.EXERCISE_PICKER}/{session_id}",
            arguments = listOf(
                navArgument("session_id") {
                    type = NavType.LongType
                }
            )
        ) {backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("session_id")
            Timber.tag("NavigationDebug").d("Entrando a ExercisePicker con sessionId = $sessionId")
            ExercisePicker(
               navController = navController,
            )
        }
        composable(
            route = "${Routes.SESSION}/{session_id}",
            arguments = listOf(navArgument("session_id") {

                type = NavType.LongType
            })
        ) {
            SessionScreen(
                onNavigate = { navController.navigationEvent(event = it) },
            )
        }

    }
}

private fun NavHostController.navigationEvent(event: UiEvent.Navigate) {
    navigate(event.route) {
        if (event.popBackStack) currentDestination?.route?.let { popUpTo(it) { inclusive = true } }
        launchSingleTop = true
    }
}
