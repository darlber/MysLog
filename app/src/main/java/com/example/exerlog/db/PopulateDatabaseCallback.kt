package com.example.exerlog.db

import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.exerlog.core.Constants.Companion.BASE_IMAGE_URL
import com.example.exerlog.core.Constants.Companion.CACHE_FILENAME
import com.example.exerlog.core.Constants.Companion.GITHUB_URL
import com.example.exerlog.core.Constants.Companion.VERSION_KEY
import com.example.exerlog.db.entities.ExercisesVersion
import com.example.exerlog.db.repository.ExerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

class PopulateDatabaseCallback @Inject constructor(
    private val repository: Provider<ExerRepository>,
    @ApplicationContext private val context: Context,
    private val exerciseDaoProvider: Provider<ExerDAO>
) : RoomDatabase.Callback() {


    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            checkAndPopulateDatabase()
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        CoroutineScope(Dispatchers.IO).launch {
            checkAndPopulateDatabase()
        }
    }

    suspend fun checkAndPopulateDatabase() {
        try {
            val jsonString = downloadJsonFromGitHub(GITHUB_URL) ?: readCache() ?: run {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No JSON disponible", Toast.LENGTH_SHORT).show()
                }
                Timber.w("No JSON available, aborting prepopulation")
                return
            }

            val typeToken = object : TypeToken<ExercisesVersion>() {}.type
            val exercisesVersion: ExercisesVersion = Gson().fromJson(jsonString, typeToken)
            val exercises = exercisesVersion.exercises

            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val localVersion = prefs.getInt(VERSION_KEY, 0)

            if (exercisesVersion.version > localVersion) {
                Timber.i("Updating database from version $localVersion to ${exercisesVersion.version}")
                exerciseDaoProvider.get().insertAll(exercises)

                // Guardar versión y cache
                prefs.edit { putInt(VERSION_KEY, exercisesVersion.version) }
                writeCache(jsonString)

                // Descargar y cachear imágenes
                exercises.forEach { exercise ->
                    exercise.images.forEach { imageName ->
                        val localFile = File(context.filesDir, "exercises/$imageName")
                        if (!localFile.exists()) {
                            val imageUrl = BASE_IMAGE_URL + imageName
                            downloadAndCacheImage(imageUrl, localFile)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Base de datos actualizada a la versión ${exercisesVersion.version}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Timber.i("${exercises.size} exercises inserted successfully.")
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Base de datos ya está actualizada", Toast.LENGTH_SHORT)
                        .show()
                }
                Timber.d("Database is already up-to-date. Version: $localVersion")
            }

        } catch (e: Exception) {
            Timber.e(e, "Exception during database prepopulation")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al actualizar base de datos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun readCache(): String? {
        val file = File(context.filesDir, CACHE_FILENAME)
        return if (file.exists()) {
            Timber.d("JSON cargado desde cache local")
            file.readText()
        } else null
    }

    private fun writeCache(jsonString: String) {
        val file = File(context.filesDir, CACHE_FILENAME)
        file.writeText(jsonString)
        Timber.d("JSON guardado en cache local")
    }

    private fun downloadJsonFromGitHub(url: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Timber.d("JSON descargado desde GitHub")
                response.body?.string()
            } else {
                Timber.w("Failed to download JSON, HTTP ${response.code}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error downloading JSON from GitHub")
            null
        }
    }

    private fun downloadAndCacheImage(imageUrl: String, localFile: File) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(imageUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bytes = response.body?.bytes()
                if (bytes != null) {
                    localFile.parentFile?.mkdirs() // Crear directorios si no existen
                    localFile.writeBytes(bytes)
                    Timber.d("Imagen guardada en cache: ${localFile.absolutePath}")
                }
            } else {
                Timber.w("Fallo al descargar imagen $imageUrl, HTTP ${response.code}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error descargando imagen $imageUrl")
        }
    }
}