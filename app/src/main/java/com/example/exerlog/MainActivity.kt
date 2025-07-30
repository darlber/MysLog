package com.example.exerlog

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.exerlog.db.ExerDAO
import com.example.exerlog.ui.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


// Anotación para habilitar la inyección de dependencias con Hilt en esta actividad
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dao: ExerDAO

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
// NO llamar a controller.hide(...)


        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        // Hide the status and navigation bars
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        setContent {
            AppTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
                    )
                }
                // Permitir que el contenido ocupe toda la pantalla

                // Controlador de navegación
                val navController = rememberNavController()
                Surface {
                    AppNavHost(navController = navController)
                }
            }

        }
    }
}