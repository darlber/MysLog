package com.example.myslog.db

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myslog.core.Constants
import com.example.myslog.core.Constants.Companion.VERSION_KEY
import com.example.myslog.db.entities.ExercisesVersion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

class PopulateDatabaseCallback @Inject constructor(
    private val exerciseDaoProvider: Provider<MysDAO>,
    @ApplicationContext private val context: Context
) : RoomDatabase.Callback() {

    private val supportedLanguages = listOf("en", "es")

    companion object {
        private val gson = Gson()
        private val exercisesType = object : TypeToken<ExercisesVersion>() {}.type
        private val client by lazy { OkHttpClient() }
    }

    fun getCurrentLanguage(): String {
        val locale = context.resources.configuration.locales[0]
        return if (locale.language in supportedLanguages) locale.language else "en"
    }

    private fun triggerPopulation() {
        CoroutineScope(Dispatchers.IO).launch {
            checkAndPopulateDatabase(getCurrentLanguage())
        }
    }

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.i("Room onCreate callback ejecutado")
        triggerPopulation()
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Timber.d("Room onOpen callback ejecutado")
        triggerPopulation()
    }

    suspend fun checkAndPopulateDatabase(lang: String) {
        Timber.i("checkAndPopulateDatabase: iniciando para idioma $lang")

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val localVersion = prefs.getFloat("${VERSION_KEY}_$lang", 0F)
        val cacheFile = File(context.filesDir, "exercises_$lang.json")

        val localJson = cacheFile.takeIf { it.exists() }?.readText()
        val remoteJson = downloadJsonFromGitHub(Constants.getJsonUrlForLanguage(lang))

        val jsonToUse = remoteJson ?: localJson
        if (jsonToUse == null) {
            Timber.w("No hay JSON disponible ni en remoto ni en cache, abortando populate para $lang")
            return
        }

        val remoteExercises = gson.fromJson<ExercisesVersion>(jsonToUse, exercisesType)
        val remoteVersion = remoteExercises.version

        if (remoteJson != null && remoteVersion > localVersion) {
            cacheFile.writeText(remoteJson)
            Timber.i("Actualizando BD $lang de versión $localVersion a $remoteVersion")
            populateFromJson(remoteJson, lang, localVersion, prefs, force = true)
        } else {
            Timber.i("BD ya actualizada para $lang (v$localVersion)")
            populateFromJson(jsonToUse, lang, localVersion, prefs, force = false)
        }
    }

    /**
     * Inserta ejercicios en la BD.
     * Mantiene IDs iguales para no perder datos del usuario.
     */
    suspend fun populateFromJson(
        json: String,
        lang: String,
        localVersion: Float,
        prefs: SharedPreferences,
        force: Boolean
    ) {
        val exercisesVersion: ExercisesVersion = gson.fromJson(json, exercisesType)
        val exercises = exercisesVersion.exercises

        if (force || exercisesVersion.version > localVersion) {
            Timber.i("Insertando ${exercises.size} ejercicios para $lang")
            exerciseDaoProvider.get().insertAll(exercises)
            prefs.edit {
                putFloat("${VERSION_KEY}_$lang", exercisesVersion.version.toFloat())
            }
            Timber.i("Insertados todos los ejercicios para $lang")
        }
    }

    fun downloadJsonFromGitHub(url: String): String? {
        return try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Timber.d("JSON descargado desde GitHub: $url")
                response.body?.string()
            } else {
                Timber.w("Error descargando JSON, HTTP ${response.code}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Excepción descargando JSON desde GitHub: $url")
            null
        }
    }
}
