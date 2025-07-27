package com.example.exerlog.di
import android.content.Context
import androidx.room.Room
import com.example.exerlog.core.Constants.Companion.DATABASE_NAME
import com.example.exerlog.db.ExerDAO
import com.example.exerlog.db.GymDatabase
import com.example.exerlog.db.PopulateDatabaseCallback
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.db.repository.ExerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        populateDatabaseCallback: PopulateDatabaseCallback
    ): GymDatabase {
        Timber.d("Creando instancia de la base de datos...") // Log antes de crear la BD
        val database = Room.databaseBuilder(
            context.applicationContext,
            GymDatabase::class.java,
            DATABASE_NAME
        )
            .addCallback(populateDatabaseCallback)
            .fallbackToDestructiveMigration(false)
            .build()
        Timber.d("Instancia de la base de datos creada exitosamente.") // Log después de crear la BD
        return database
    }

    @Provides
    fun provideExerciseDao(db: GymDatabase): ExerDAO {
        Timber.d("Proporcionando ExerDAO.") // Log al proveer el DAO
        return db.dao
    }
    @Provides
    fun provideRepositoryImpl(dao: ExerDAO):  ExerRepository = ExerRepositoryImpl(dao) // Proporciona la implementación del repositorio


}