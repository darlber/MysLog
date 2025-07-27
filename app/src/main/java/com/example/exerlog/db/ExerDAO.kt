package com.example.exerlog.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.exerlog.core.Entities.EXERCISE
import com.example.exerlog.core.Entities.GYMSET
import com.example.exerlog.core.Entities.SESSIONEXERCISE
import com.example.exerlog.core.Entities.SESSIONWORKOUT
import com.example.exerlog.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Query("SELECT * FROM $SESSIONWORKOUT WHERE sessionId = :sessionId")
    fun getSessionById(sessionId: Long): Flow<Session>

    @Query("SELECT * FROM $GYMSET ORDER BY setId ASC")
    fun getAllSets(): Flow<List<GymSet>>

    @Query("SELECT * FROM $SESSIONWORKOUT ORDER BY start DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM $SESSIONWORKOUT ORDER BY sessionId DESC LIMIT 1")
    fun getLastSession(): Session

    @Query("SELECT * FROM $EXERCISE ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM $SESSIONEXERCISE join $EXERCISE ON $SESSIONEXERCISE.parentExerciseId = $EXERCISE.id")
    fun getAllSessionExercises(): Flow<List<SessionExerciseWithExercise>>

    @Query("SELECT * FROM $SESSIONEXERCISE JOIN $EXERCISE ON $SESSIONEXERCISE.parentExerciseId = $EXERCISE.id WHERE parentSessionId = :sessionId")
    fun getExercisesForSession(sessionId: Long): Flow<List<SessionExerciseWithExercise>>

    @Query("SELECT * FROM $GYMSET WHERE parentSessionExerciseId = :id ORDER BY setId ASC")
    fun getSetsForExercise(id: Long): Flow<List<GymSet>>

    @Query("SELECT GROUP_CONCAT(primaryMuscles,'|') FROM $EXERCISE as e JOIN $SESSIONEXERCISE as se ON e.id = se.parentExerciseId  WHERE se.parentSessionId = :sessionId")
    fun getMuscleGroupsForSession(sessionId: Long): Flow<String>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSession(session: Session): Long

    @Delete
    suspend fun removeSession(session: Session)

    @Update
    suspend fun updateSession(session: Session)
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSessionExercise(sessionExercise: SessionExercise): Long

    @Delete
    suspend fun removeSessionExercise(sessionExercise: SessionExercise)

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSet(set: GymSet): Long

    @Update
    suspend fun updateSet(set: GymSet)

    @Delete
    suspend fun deleteSet(set: GymSet)

    @Query("SELECT * FROM $SESSIONWORKOUT")
    fun getSessionList(): List<Session>

    @Query("SELECT * FROM $EXERCISE")
    fun getExerciseList(): List<Exercise>

    @Query("SELECT * FROM $SESSIONEXERCISE")
    fun getSessionExerciseList(): List<SessionExercise>

    @Query("SELECT * FROM $GYMSET")
    fun getSetList(): List<GymSet>

    @Query("DELETE FROM $SESSIONWORKOUT")
    suspend fun clearSessions()

    @Query("DELETE FROM $SESSIONEXERCISE")
    suspend fun clearSessionExercises()

    @Query("DELETE FROM $GYMSET")
    suspend fun clearSets()

    @Query("DELETE FROM $EXERCISE")
    suspend fun clearExercises()
}