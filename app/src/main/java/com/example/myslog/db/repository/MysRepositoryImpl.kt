package com.example.myslog.db.repository

import android.content.Context
import androidx.core.content.edit
import com.example.myslog.core.Constants
import com.example.myslog.core.Constants.Companion.VERSION_KEY
import com.example.myslog.db.MysDAO
import com.example.myslog.db.GymDatabase
import com.example.myslog.db.PopulateDatabaseCallback
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.ExercisesVersion
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.db.entities.SessionExerciseWithExercise
import com.example.myslog.ui.DatabaseModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MysRepositoryImpl @Inject constructor(
    private val dao: MysDAO,
    private val db: GymDatabase,
    private val populateDatabaseCallback: PopulateDatabaseCallback,
    @ApplicationContext private val context: Context
) : MysRepository {

    private val _currentLanguage = MutableStateFlow(populateDatabaseCallback.getCurrentLanguage())
    override val currentLanguage: Flow<String> = _currentLanguage

    override fun getExercisesFlow(): Flow<List<Exercise>> =
        _currentLanguage.flatMapLatest { dao.getAllExercises() }

    override suspend fun checkForUpdates(lang: String): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val localVersion = prefs.getFloat("${VERSION_KEY}_$lang", 0F)

        val remoteUrl = Constants.getJsonUrlForLanguage(lang)
        val remoteJson = populateDatabaseCallback.downloadJsonFromGitHub(remoteUrl) ?: return false

        val exercisesVersion = Gson().fromJson(remoteJson, ExercisesVersion::class.java)
        val remoteVersion = exercisesVersion.version

        return if (remoteVersion > localVersion) {
            populateDatabaseCallback.populateFromJson(remoteJson, lang, localVersion, prefs, force = true)
            true
        } else {
            Timber.i("BD ya actualizada para $lang (v$localVersion)")
            false
        }
    }

    override suspend fun switchLanguage(lang: String) {
        withContext(Dispatchers.IO) {
            // Guardar idioma en preferencias
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("current_lang", lang).apply()
            _currentLanguage.value = lang

            // Limpiar la DB
            clearDatabaseInternal()

            // Descargar JSON y poblar DB
            val remoteUrl = Constants.getJsonUrlForLanguage(lang)
            val remoteJson = populateDatabaseCallback.downloadJsonFromGitHub(remoteUrl)
                ?: return@withContext

            populateDatabaseCallback.populateFromJson(remoteJson, lang, 0F, prefs, force = true)
        }
    }

    private suspend fun clearDatabaseInternal() {
        withContext(Dispatchers.IO) {
            Timber.i("Clearing exercises")
            dao.clearExercises()
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit { putInt(VERSION_KEY, 0) }
        }
    }

    override suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            clearDatabaseInternal()
            populateDatabaseCallback.checkAndPopulateDatabase(_currentLanguage.value)
        }
    }

    override fun getSessionById(sessionId: Long) = dao.getSessionById(sessionId)
    override fun getAllSessions() = dao.getAllSessions()
    override fun getAllSets() = dao.getAllSets()
    override fun getAllExercises() = dao.getAllExercises()
    override fun getLastSession() = dao.getLastSession()
    override fun getAllSessionExercises() = dao.getAllSessionExercises()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getExercisesForSession(session: Flow<Session>) =
        session.flatMapLatest { dao.getExercisesForSession(it.sessionId) }

    override fun getExercisesForSession(session: Session): Flow<List<SessionExerciseWithExercise>> =
        dao.getExercisesForSession(session.sessionId)

    override fun getSetsForExercise(sessionExerciseId: Long) = dao.getSetsForExercise(sessionExerciseId)

    override fun getMuscleGroupsForSession(session: Session): Flow<List<String>> =
        dao.getMuscleGroupsForSession(session.sessionId).mapNotNull {
            try { emptyList() } catch (_: Exception) { Timber.d("Error target"); emptyList() }
        }

    override suspend fun insertExercise(exercise: Exercise) = dao.insertExercise(exercise)
    override suspend fun insertSession(session: Session) = dao.insertSession(session)
    override suspend fun removeSession(session: Session) = dao.removeSession(session)
    override suspend fun updateSession(session: Session) = dao.updateSession(session)
    override suspend fun insertSessionExercise(sessionExercise: SessionExercise) = dao.insertSessionExercise(sessionExercise)
    override suspend fun removeSessionExercise(sessionExercise: SessionExercise) = dao.removeSessionExercise(sessionExercise)
    override suspend fun insertSet(gymSet: GymSet) = dao.insertSet(gymSet)
    override suspend fun updateSet(set: GymSet) = dao.updateSet(set)
    override suspend fun deleteSet(set: GymSet) = dao.deleteSet(set)
    override suspend fun createSet(sessionExercise: SessionExercise): Long =
        dao.insertSet(GymSet(parentSessionExerciseId = sessionExercise.sessionExerciseId))

    override fun getDatabaseModel(): DatabaseModel =
        DatabaseModel(
            sessions = dao.getSessionList(),
            exercises = dao.getExerciseList(),
            sessionExercises = dao.getSessionExerciseList(),
            sets = dao.getSetList()
        )

    override suspend fun deleteSessionById(sessionId: Long) = dao.deleteSessionById(sessionId)
    override fun getAllEquipment(): Flow<List<String>> = dao.getAllEquipment()
    override fun getAllMuscles(): Flow<List<String>> = dao.getAllMuscles()
    override fun getUsedExerciseIds(): Flow<List<String>> = dao.getUsedExerciseIds()
    override fun getSessionExerciseById(id: Long): SessionExercise = dao.getSessionExerciseById(id)
}
