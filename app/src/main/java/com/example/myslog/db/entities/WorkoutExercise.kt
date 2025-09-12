package com.example.myslog.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myslog.core.Entities.WORKOUTEXERCISE

@Entity(tableName = WORKOUTEXERCISE)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentWorkoutId: Long,
    val exerciseId: String
)