package com.example.exerlog.db.repository

import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.db.entities.SessionExerciseWithExercise
import com.example.exerlog.ui.DatabaseModel
import kotlinx.coroutines.flow.Flow

interface ExerRepository {
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
}