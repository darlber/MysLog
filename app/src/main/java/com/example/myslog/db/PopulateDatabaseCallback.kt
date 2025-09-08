package com.example.myslog.db

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myslog.core.Constants
import com.example.myslog.core.Constants.Companion.VERSION_KEY
import com.example.myslog.db.entities.ExercisesVersion
import com.example.myslog.db.entities.Exercise
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

    fun getCurrentLanguage(): String {
        val locale = context.resources.configuration.locales[0]
        return if (supportedLanguages.contains(locale.language)) locale.language else "en"
    }

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.i("Room onCreate callback ejecutado")
        CoroutineScope(Dispatchers.IO).launch {
            val lang = getCurrentLanguage()
            checkAndPopulateDatabase(lang)
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Timber.d("Room onOpen callback ejecutado")
        // Opcional: actualizar ejercicios al abrir
        CoroutineScope(Dispatchers.IO).launch {
            val lang = getCurrentLanguage()
            checkAndPopulateDatabase(lang)
        }
    }

    suspend fun checkAndPopulateDatabase(lang: String) {
        Timber.i("checkAndPopulateDatabase: iniciando para idioma $lang")

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val localVersion = prefs.getFloat("${VERSION_KEY}_$lang", 0F)

        val cacheFile = File(context.filesDir, "exercises_$lang.json")
        var localJson: String? = null

        if (cacheFile.exists()) {
            localJson = cacheFile.readText()
            Timber.d("Usando JSON cacheado para $lang")
        }

        // Descargar JSON remoto
        val remoteUrl = Constants.getJsonUrlForLanguage(lang)
        val remoteJson = downloadJsonFromGitHub(remoteUrl)

        if (remoteJson == null) {
            if (localJson != null) {
                Timber.d("No se pudo descargar remoto, usando cache local")
                populateFromJson(localJson, lang, localVersion, prefs, force = false)
            } else {
                Timber.w("No hay JSON disponible ni en remoto ni en cache, abortando populate para $lang")
            }
            return
        }

        val typeToken = object : TypeToken<ExercisesVersion>() {}.type
        val remoteExercisesVersion: ExercisesVersion = Gson().fromJson(remoteJson, typeToken)
        val remoteVersion = remoteExercisesVersion.version

        if (remoteVersion > localVersion) {
            cacheFile.writeText(remoteJson)
            Timber.i("Actualizando BD $lang de versión $localVersion a $remoteVersion")
            populateFromJson(remoteJson, lang, localVersion, prefs, force = true)
        } else {
            Timber.i("BD ya actualizada para $lang (v$localVersion)")
            if (localJson != null) {
                populateFromJson(localJson, lang, localVersion, prefs, force = false)
            }
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
        val typeToken = object : TypeToken<ExercisesVersion>() {}.type
        val exercisesVersion: ExercisesVersion = Gson().fromJson(json, typeToken)
        val exercises = exercisesVersion.exercises

        if (force || exercisesVersion.version > localVersion) {
            Timber.i("Insertando ${exercises.size} ejercicios para $lang")
            exerciseDaoProvider.get().insertAll(exercises)
            prefs.edit { putFloat("${VERSION_KEY}_$lang", exercisesVersion.version.toFloat()) }
            Timber.i("Insertados todos los ejercicios para $lang")
        }
    }

    fun downloadJsonFromGitHub(url: String): String? {
        return try {
            val client = OkHttpClient()
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
