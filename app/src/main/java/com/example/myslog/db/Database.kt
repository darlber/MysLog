package com.example.myslog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.db.entities.Workout
import com.example.myslog.db.entities.WorkoutExercise
import com.example.myslog.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

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
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(
            context: Context,
            callback: PopulateDatabaseCallback
        ): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "mys_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val lang = callback.getCurrentLanguage()
                                Timber.i("Creando BD por primera vez → Poblando con $lang")
                                callback.checkAndPopulateDatabase(lang)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}