package com.example.myslog.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.db.entities.Workout
import com.example.myslog.db.entities.WorkoutExercise
import com.example.myslog.utils.Converters

@Database(
    entities = [
        Session::class,
        Exercise::class,
        SessionExercise::class,
        GymSet::class,
        Workout::class,
        WorkoutExercise::class
    ],
    autoMigrations = [
    ],
    version = 3, exportSchema = true
)

@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {
    abstract val dao: MysDAO

    companion object {

    }
}