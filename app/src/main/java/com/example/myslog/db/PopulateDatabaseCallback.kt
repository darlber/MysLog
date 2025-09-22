package com.example.myslog.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myslog.db.entities.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Provider

class PopulateDatabaseCallback @Inject constructor(
    private val exerciseDaoProvider: Provider<MysDAO>,
    @ApplicationContext private val context: Context
) : RoomDatabase.Callback() {

    private val supportedLanguages = listOf("en", "es")

    companion object {
        private val gson = Gson()
        private val exercisesType = object : TypeToken<List<Exercise>>() {}.type
    }

    fun getCurrentLanguage(): String {
        val locale = context.resources.configuration.locales[0]
        return if (locale.language in supportedLanguages) locale.language else "en"
    }

    private fun triggerPopulation() {
        CoroutineScope(Dispatchers.IO).launch {
            populateFromAssets(getCurrentLanguage())
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

    suspend fun populateFromAssets(lang: String) {
        Timber.i("populateFromAssets: iniciando para idioma $lang")

        val json = loadJsonFromAssets("exercises_${lang}.json") ?: run {
            Timber.w("No se encontró JSON en assets para $lang")
            return
        }

        val exercises: List<Exercise> = gson.fromJson(json, exercisesType)

        Timber.i("Insertando ${exercises.size} ejercicios para $lang")
        exerciseDaoProvider.get().insertAll(exercises)
        Timber.i("Insertados todos los ejercicios para $lang")
    }

    private fun loadJsonFromAssets(fileName: String): String? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error leyendo JSON desde assets: $fileName")
            null
        }
    }
}
