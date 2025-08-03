package com.example.exerlog.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.exerlog.R
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.repository.ExerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider

class PopulateDatabaseCallback @Inject constructor(
    private val repository: Provider<ExerRepository>,
    @ApplicationContext private val context: Context,

    // Usamos Provider para evitar dependencias cíclicas con Hilt
    private val exerciseDaoProvider: Provider<ExerDAO>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            prepopulateDatabase()
        }
    }

    suspend fun prepopulateDatabase() {
        try {
            Timber.d("prepopulateDatabase called") // Usando Timber.d para debug logs
            val inputStream =
                context.resources.openRawResource(R.raw.exercises)  // O el nombre que tenga tu archivo en res/raw
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            Timber.d("JSON String loaded successfully. Length: ${jsonString.length}")

            val typeToken = object : TypeToken<List<Exercise>>() {}.type
            val exercises: List<Exercise> = Gson().fromJson(jsonString, typeToken)
            Timber.d("JSON parsed. Number of exercises: ${exercises.size}")

            if (exercises.isNotEmpty()) {
                exerciseDaoProvider.get().insertAll(exercises)
                Timber.i("${exercises.size} exercises inserted successfully.") // Timber.i para info logs
            } else {
                Timber.w("No exercises found in JSON to insert.") // Timber.w para warnings
            }

        } catch (e: IOException) {
            Timber.e(
                e,
                "IOException during database prepopulation"
            ) // Timber.e para errores, incluye la excepción
        } catch (e: Exception) {
            Timber.e(e, "Generic exception during database prepopulation")
        }
    }
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        CoroutineScope(Dispatchers.IO).launch {
            val count = exerciseDaoProvider.get().countExercises()
            if (count == 0) {
                prepopulateDatabase()
                Timber.i("Database prepopulated with exercises.")
            }
        }
    }

}