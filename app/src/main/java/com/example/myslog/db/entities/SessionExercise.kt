package com.example.myslog.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myslog.core.Entities.SESSIONEXERCISE

@Entity(tableName = SESSIONEXERCISE)
data class SessionExercise(
    //Primary key es una llave primaria,
    // autoGenerate significa que se autogenera un id único para cada
    // de ejercicio.
    @PrimaryKey(autoGenerate = true)
    val sessionExerciseId: Long = 0,
    // ColumnInfo(index = true) significa que se crea un índice para esta columna
    @ColumnInfo(index = true)
    val parentSessionId: Long,
    @ColumnInfo(index = true)
    val parentExerciseId: String,
    val comment: String? = null
)

/**
 * Holds a sessionExercise and it's associated exercise. Embedded = bad? it works though.
 */
//TODO: usar @Relation instead of @Embedded
data class SessionExerciseWithExercise(
    @Embedded
    val sessionExercise: SessionExercise,
    @Embedded
    val exercise: Exercise
)

data class SessionWithSessionExerciseWithExercise(
    @Embedded val session: Session,
    @Embedded val sessionExercise: SessionExercise,
    @Embedded val exercise: Exercise
)