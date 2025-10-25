package com.example.myslog

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.myslog.db.MysDAO
import com.example.myslog.ui.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

//TODO: capitalizar json y traducir con googleSheets o no se k pollas
// Anotación para habilitar la inyección de dependencias con Hilt en esta actividad
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dao: MysDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Hace las barras totalmente transparentes y ajusta el tinte de los iconos
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(   // iconos claros u oscuros según tema
                lightScrim = Color.TRANSPARENT,
                darkScrim  = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim  = Color.TRANSPARENT
            )
        )

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

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