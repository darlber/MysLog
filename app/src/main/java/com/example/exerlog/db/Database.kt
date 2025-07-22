package com.example.exerlog.db;

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.exerlog.db.ExerDAO
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.utils.Converters

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
abstract class GymDatabase :

    RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val dao: ExerDAO

}
