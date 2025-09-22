package com.example.myslog.db.repository

import android.content.Context
import com.example.myslog.db.GymDatabase
import com.example.myslog.db.MysDAO
import com.example.myslog.db.PopulateDatabaseCallback
import com.example.myslog.db.entities.*
import com.example.myslog.ui.DatabaseModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getExercisesFlow(): Flow<List<Exercise>> =
        _currentLanguage.flatMapLatest { dao.getAllExercises() }

    override suspend fun switchLanguage(lang: String) {
        withContext(Dispatchers.IO) {
            _currentLanguage.value = lang
            clearDatabaseInternal()
            populateDatabaseCallback.populateFromAssets(lang)
        }
    }

    private suspend fun clearDatabaseInternal() {
        withContext(Dispatchers.IO) {
            Timber.i("Clearing exercises")
            dao.clearExercises()
        }
    }

    override suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            Timber.i("Clearing all tables")
            db.clearAllTables()
            dao.deletePrimaryKeyIndex()
            populateDatabaseCallback.populateFromAssets(_currentLanguage.value)
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
        dao.getMuscleGroupsForSession(session.sessionId)
            .map { it.split("|").filter { s -> s.isNotEmpty() } }

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

    override fun getAllWorkouts(): Flow<List<Workout>> = dao.getAllWorkouts()

    override fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExercise>> =
        dao.getExercisesForWorkout(workoutId)

    override suspend fun insertWorkout(workout: Workout): Long = dao.insertWorkout(workout)
    override suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long =
        dao.insertWorkoutExercise(workoutExercise)
    override suspend fun deleteWorkout(workout: Workout) = dao.deleteWorkout(workout)
    override suspend fun deleteWorkoutById(workoutId: Long) = dao.deleteWorkoutById(workoutId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getExercisesForWorkoutExercises(workoutId: Long): Flow<List<Exercise>> =
        getExercisesForWorkout(workoutId).flatMapLatest { workoutExercises ->
            val exerciseIds = workoutExercises.map { it.exerciseId }
            getExercisesFlow().map { exercises ->
                exercises.filter { it.id in exerciseIds }
            }
        }

    override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise) {
        dao.deleteWorkoutExercise(workoutExercise)
    }
}
