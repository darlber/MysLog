package com.example.exerlog

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.exerlog.ui.AppNavHost
import com.example.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import timber.log.Timber.DebugTree


// Anotación para habilitar la inyección de dependencias con Hilt en esta actividad
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        setContent {
            AppTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        0
                    )
                }
                // Permitir que el contenido ocupe toda la pantalla
                WindowCompat.setDecorFitsSystemWindows(window, false)

                // Controlador de navegación
                val navController = rememberNavController()

                // Configuración del NavHost
                AppNavHost(navController = navController)

                }

            }
        }
    }
