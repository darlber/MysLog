package com.example.exerlog.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * A workout-session contains multiple SessionExercises. Has a start and end-time.
 */
//TODO: end time is not used in the app, but could be useful for tracking session duration
@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0L,
    val start: LocalDateTime = LocalDateTime.now(),
    val end: LocalDateTime? = null
)