package com.example.myslog.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myslog.core.Entities.WORKOUT

@Entity(tableName = WORKOUT)
data class Workout(
    @PrimaryKey(autoGenerate = true) val workoutId: Long = 0,
    val name: String
)
