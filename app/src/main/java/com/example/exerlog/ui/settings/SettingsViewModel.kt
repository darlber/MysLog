package com.example.exerlog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exerlog.core.Routes
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.utils.Event
import com.example.exerlog.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: ExerRepository,
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is SettingsEvent.ImportDatabase -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //         importDatabase(event.uri, event.context)
                }
            }

            is SettingsEvent.ExportDatabase -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //            exportDatabase(event.uri, event.context)
                }
            }

            is SettingsEvent.CreateFile -> {
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                //          sendUiEvent(UiEvent.FileCreated("workout_db_$date.json"))
            }

            is SettingsEvent.ClearDatabase -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repo.clearDatabase()
                    Timber.d("Database cleared")
                    _uiEvent.send(UiEvent.Navigate(Routes.HOME))  // Volver al Home
                }
            }
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

//    private fun exportDatabase(uri: Uri, context: Context) {
//        val gson = Converters.registerAll(GsonBuilder().setPrettyPrinting()).create()
//        val databaseModel = repo.getDatabaseModel()
//        val ob = gson.toJson(databaseModel)
//        saveToFile(uri, context.contentResolver, ob)
//    }
//
//    private fun importDatabase(uri: Uri, context: Context) {
//        viewModelScope.launch {
//            val gson = Converters.registerAll(GsonBuilder().setPrettyPrinting()).create()
//            loadFromFile(uri, context.contentResolver)?.let {
//                val importedDatabase = gson.fromJson(it, DatabaseModel::class.java)
//                Timber.d("$importedDatabase")
//                importedDatabase.sessions.forEach { session ->
//                    repo.insertSession(session)
//                }
//                importedDatabase.exercises.forEach { exercise ->
//                    repo.insertExercise(exercise)
//                }
//                importedDatabase.sessionExercises.forEach { sessionExercise ->
//                    repo.insertSessionExercise(sessionExercise)
//                }
//                importedDatabase.sets.forEach { set ->
//                    repo.insertSet(set)
//                }
//            }
//        }
//    }
//
//    private fun saveToFile(uri: Uri, contentResolver: ContentResolver, content: String) {
//        try {
//            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
//                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
//                    it.write(content.toByteArray())
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun loadFromFile(uri: Uri, contentResolver: ContentResolver): String? {
//        try {
//            contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
//                FileInputStream(parcelFileDescriptor.fileDescriptor).use {
//                    return it.readBytes().decodeToString()
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return null
//    }
}