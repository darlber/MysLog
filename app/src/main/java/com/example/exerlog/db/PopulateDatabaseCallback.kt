package com.example.exerlog.db

import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.exerlog.core.Constants
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

    private val supportedLanguages = listOf("en", "es")

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            prefetchAllLanguages()
            checkAndPopulateDatabase(getCurrentLanguage())
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        CoroutineScope(Dispatchers.IO).launch {
            checkAndPopulateDatabase(getCurrentLanguage())
        }
    }

    suspend fun prefetchAllLanguages() {
        supportedLanguages.forEach { lang ->
            val jsonUrl = Constants.getJsonUrlForLanguage(lang)
            val jsonString = downloadJsonFromGitHub(jsonUrl) ?: return@forEach
            val file = File(context.filesDir, "exercises_$lang.json")
            file.writeText(jsonString)
            Timber.d("JSON $lang prefetch guardado en ${file.absolutePath}")
        }
    }

    fun getCurrentLanguage(): String {
        val locale = context.resources.configuration.locales[0]
        return if (supportedLanguages.contains(locale.language)) locale.language else "en"
    }

    suspend fun checkAndPopulateDatabase(lang: String) {
        Timber.i("checkAndPopulateDatabase: Started for language $lang")
        val jsonFile = File(context.filesDir, "exercises_$lang.json")
        val jsonString = if (jsonFile.exists()) {
            jsonFile.readText().also { Timber.d("Usando JSON cacheado para $lang") }
        } else {
            val url = Constants.getJsonUrlForLanguage(lang)
            downloadJsonFromGitHub(url) ?: run {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No JSON disponible para $lang", Toast.LENGTH_SHORT).show()
                }
                Timber.w("No JSON disponible, abortando populate para $lang")
                return
            }
        }

        val typeToken = object : TypeToken<ExercisesVersion>() {}.type
        val exercisesVersion: ExercisesVersion = Gson().fromJson(jsonString, typeToken)
        val exercises = exercisesVersion.exercises

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val localVersion = prefs.getInt("${VERSION_KEY}_$lang", 0)

        // Forzamos siempre la actualización en runtime
        Timber.i("Updating database for $lang from version $localVersion to ${exercisesVersion.version}")
        exerciseDaoProvider.get().clearExercises()
        exerciseDaoProvider.get().insertAll(exercises)
        prefs.edit { putInt("${VERSION_KEY}_$lang", exercisesVersion.version) }
        Timber.i("${exercises.size} exercises insertados correctamente para $lang")
    }

    private fun downloadJsonFromGitHub(url: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Timber.d("JSON descargado desde GitHub: $url")
                response.body?.string()
            } else {
                Timber.w("Failed to download JSON, HTTP ${response.code}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error descargando JSON desde GitHub: $url")
            null
        }
    }
}
