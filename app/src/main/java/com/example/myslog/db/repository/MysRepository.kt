package com.example.myslog.db.repository

import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.db.entities.SessionExerciseWithExercise
import com.example.myslog.db.entities.Workout
import com.example.myslog.db.entities.WorkoutExercise
import com.example.myslog.ui.DatabaseModel
import kotlinx.coroutines.flow.Flow

interface MysRepository {
    fun getSessionById(sessionId: Long): Session
    fun getAllSessions(): Flow<List<Session>>
    fun getAllSets(): Flow<List<GymSet>>
    fun getAllExercises(): Flow<List<Exercise>>
    fun getLastSession(): Session?
    fun getAllSessionExercises(): Flow<List<SessionExerciseWithExercise>>
    fun getExercisesForSession(session: Flow<Session>): Flow<List<SessionExerciseWithExercise>>
    fun getExercisesForSession(session: Session): Flow<List<SessionExerciseWithExercise>>
    fun getSetsForExercise(sessionExerciseId: Long): Flow<List<GymSet>>
    fun getMuscleGroupsForSession(session: Session): Flow<List<String>>
    suspend fun insertExercise(exercise: Exercise): Long
    suspend fun insertSession(session: Session): Long
    suspend fun removeSession(session: Session)
    suspend fun updateSession(session: Session)
    suspend fun insertSessionExercise(sessionExercise: SessionExercise): Long
    suspend fun removeSessionExercise(sessionExercise: SessionExercise)
    suspend fun insertSet(gymSet: GymSet): Long
    suspend fun updateSet(set: GymSet)
    suspend fun deleteSet(set: GymSet)
    suspend fun createSet(sessionExercise: SessionExercise): Long
    fun getDatabaseModel(): DatabaseModel
    suspend fun clearDatabase()
    suspend fun deleteSessionById(sessionId: Long)
    fun getAllEquipment(): Flow<List<String>>
    fun getAllMuscles(): Flow<List<String>>
    fun getUsedExerciseIds(): Flow<List<String>>
    fun getSessionExerciseById(id: Long): SessionExercise
    val currentLanguage: Flow<String> // <-- expone idioma actual
    fun getExercisesFlow(): Flow<List<Exercise>>
    suspend fun switchLanguage(lang: String)

    fun getAllWorkouts(): Flow<List<Workout>>
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExercise>>

    suspend fun insertWorkout(workout: Workout): Long
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long
    suspend fun deleteWorkout(workout: Workout)
    suspend fun deleteWorkoutById(workoutId: Long)
    fun getExercisesForWorkoutExercises(workoutId: Long): Flow<List<Exercise>>
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)

}