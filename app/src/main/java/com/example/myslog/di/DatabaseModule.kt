package com.example.myslog.di

import android.content.Context
import androidx.room.Room
import com.example.myslog.core.Constants.Companion.DATABASE_NAME
import com.example.myslog.db.GymDatabase
import com.example.myslog.db.MysDAO
import com.example.myslog.db.PopulateDatabaseCallback
import com.example.myslog.db.repository.MysRepository
import com.example.myslog.db.repository.MysRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        populateDatabaseCallback: PopulateDatabaseCallback
    ): GymDatabase {
        Timber.d("Creando instancia de la base de datos...")
        return Room.databaseBuilder(
            context.applicationContext,
            GymDatabase::class.java,
            DATABASE_NAME
        )
            .addCallback(populateDatabaseCallback) // <--- Esto asegura onCreate
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideExerciseDao(db: GymDatabase): MysDAO {
        Timber.d("Proporcionando ExerDAO.")
        return db.dao
    }

    @Provides
    fun provideRepositoryImpl(
        @ApplicationContext context: Context,
        db: GymDatabase,
        dao: MysDAO,
        pd: PopulateDatabaseCallback
    ): MysRepository = MysRepositoryImpl(dao, db, pd, context)
}
