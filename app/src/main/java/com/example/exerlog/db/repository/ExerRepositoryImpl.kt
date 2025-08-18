package com.example.exerlog.db.repository

import com.example.exerlog.db.ExerDAO
import com.example.exerlog.db.GymDatabase
import com.example.exerlog.db.PopulateDatabaseCallback
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.db.entities.SessionExerciseWithExercise
import com.example.exerlog.di.DatabaseModule
import com.example.exerlog.ui.DatabaseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerRepositoryImpl @Inject constructor(
    private val dao: ExerDAO,
    private val  db: GymDatabase,
    private val populateDatabaseCallback: PopulateDatabaseCallback
) : ExerRepository {

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

    //TODO: alavergawey, habra que rehacer toda la base de datos para usar otros exercises.
   override fun getMuscleGroupsForSession(session: Session): Flow<List<String>> {
        return dao.getMuscleGroupsForSession(session.sessionId).mapNotNull {
            try {
                emptyList()
                //turnTargetIntoMuscleGroups(it)
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
        db.clearAllTables()
        dao.deletePrimaryKeyIndex()
        populateDatabaseCallback.prepopulateDatabase()

    }

    override suspend fun deleteSessionById(sessionId: Long) {
        dao.deleteSessionById(sessionId)
    }

    override fun getAllEquipment(): Flow<List<String>> {
        return dao.getAllEquipment()
    }
    override fun getAllMuscles(): Flow<List<String>> {
        return dao.getAllMuscles()
    }
    override fun getUsedExerciseIds(): Flow<List<String>> {
        return dao.getUsedExerciseIds()
    }

}