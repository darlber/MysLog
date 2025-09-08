package com.example.myslog.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.utils.Converters

@Database(
    entities = [
        Session::class,
        Exercise::class,
        SessionExercise::class,
        GymSet::class
    ],
    autoMigrations = [
    ],
    version = 2, exportSchema = true
)

@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val dao: MysDAO

}
