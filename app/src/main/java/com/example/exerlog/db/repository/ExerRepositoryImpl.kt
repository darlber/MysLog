package com.example.exerlog.db.repository

import android.content.Context
import androidx.core.content.edit
import com.example.exerlog.core.Constants
import com.example.exerlog.core.Constants.Companion.VERSION_KEY
import com.example.exerlog.db.ExerDAO
import com.example.exerlog.db.GymDatabase
import com.example.exerlog.db.PopulateDatabaseCallback
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.ExercisesVersion
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.db.entities.SessionExerciseWithExercise
import com.example.exerlog.ui.DatabaseModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerRepositoryImpl @Inject constructor(
    private val dao: ExerDAO,
    private val db: GymDatabase,
    private val populateDatabaseCallback: PopulateDatabaseCallback,
    @ApplicationContext private val context: Context
) : ExerRepository {
    override suspend fun checkForUpdates(lang: String): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val localVersion = prefs.getFloat("${Constants.VERSION_KEY}_$lang", 0F)

        val remoteUrl = Constants.getJsonUrlForLanguage(lang)
        val remoteJson = populateDatabaseCallback.downloadJsonFromGitHub(remoteUrl) ?: return false

        val exercisesVersion = Gson().fromJson(remoteJson, ExercisesVersion::class.java)
        val remoteVersion = exercisesVersion.version

        return if (remoteVersion > localVersion) {
            populateDatabaseCallback.populateFromJson(remoteJson, lang, localVersion, prefs, force = true)
            true
        } else {
            false
        }
    }
    private val _currentLanguage = MutableStateFlow(populateDatabaseCallback.getCurrentLanguage())
    override val currentLanguage: Flow<String> = _currentLanguage

    override fun getExercisesFlow(): Flow<List<Exercise>> =
        _currentLanguage.flatMapLatest { dao.getAllExercises() }

    override suspend fun switchLanguage(lang: String) {
        if (lang != _currentLanguage.value) {
            Timber.i("Switching exercises to language: $lang")
            populateDatabaseCallback.checkAndPopulateDatabase(lang)
            _currentLanguage.value = lang
            Timber.i("Language switch to $lang completed")
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

    override fun getExercisesForSession(session: Session): Flow<List<SessionExerciseWithExercise>> {
        Timber.d("Retrieving exercises for session: $session")
        return dao.getExercisesForSession(session.sessionId)
    }

    override fun getSetsForExercise(sessionExerciseId: Long) = dao.getSetsForExercise(sessionExerciseId)

    override fun getMuscleGroupsForSession(session: Session): Flow<List<String>> {
        return dao.getMuscleGroupsForSession(session.sessionId).mapNotNull {
            try {
                emptyList()
            } catch (_: Exception) {
                Timber.d("Error when converting target.")
                emptyList()
            }
        }
    }

    override suspend fun insertExercise(exercise: Exercise) = dao.insertExercise(exercise)

    override suspend fun insertSession(session: Session) = dao.insertSession(session)

    override suspend fun removeSession(session: Session) = dao.removeSession(session)

    override suspend fun updateSession(session: Session) = dao.updateSession(session)

    override suspend fun insertSessionExercise(sessionExercise: SessionExercise) =
        dao.insertSessionExercise(sessionExercise)

    override suspend fun removeSessionExercise(sessionExercise: SessionExercise) =
        dao.removeSessionExercise(sessionExercise)

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

    override suspend fun clearDatabase() {
        Timber.i("Clearing and repopulating database")
        db.clearAllTables()
        dao.deletePrimaryKeyIndex()
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit { putInt(VERSION_KEY, 0) }
        populateDatabaseCallback.checkAndPopulateDatabase(_currentLanguage.value)
    }
    override suspend fun deleteSessionById(sessionId: Long) {
        dao.deleteSessionById(sessionId)
    }

    override fun getAllEquipment(): Flow<List<String>> = dao.getAllEquipment()

    override fun getAllMuscles(): Flow<List<String>> = dao.getAllMuscles()

    override fun getUsedExerciseIds(): Flow<List<String>> = dao.getUsedExerciseIds()

    override fun getSessionExerciseById(id: Long): SessionExercise = dao.getSessionExerciseById(id)

}
