package com.example.myslog.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myslog.core.Entities.GYMSET
import com.example.myslog.core.TipoSet


/**
 * A SessionExercise can have multiple Sets associated with it.
 * Each Set is a number of reps with a specific weight (if applicable)
 */
@Entity(tableName = GYMSET)
data class GymSet(
    @PrimaryKey(autoGenerate = true)
    val setId: Long = 0L,
    @ColumnInfo(index = true)
    val parentSessionExerciseId: Long,
    val reps: Int? = null,
    val weight: Float? = null,
    // TODO: time is not used in the app, but could be useful for HIIT or other timed exercises
    val time: Long? = null,

    //TODO: distance is not used in the app, but could be useful for running or cycling
    val distance: Float? = null,
    val tipoSet: Int = TipoSet.NORMAL

)