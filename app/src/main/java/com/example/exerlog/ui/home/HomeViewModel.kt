package com.example.exerlog.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exerlog.core.Routes
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.utils.Event
import com.example.exerlog.utils.UiEvent
import com.example.exerlog.utils.sortedListOfMuscleGroups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ExerRepository
) : ViewModel() {

    val sessions = combine(
        repo.getAllSessionExercises(), repo.getAllSessions()
    ) { sewes, sessions ->

        sessions.map { session ->
            val relatedExercises =
                sewes.filter { it.sessionExercise.parentSessionId == session.sessionId }
                    .map { it.exercise }

            val muscleGroups = relatedExercises.sortedListOfMuscleGroups()
            SessionWrapper(session, muscleGroups)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var sessionToDelete by mutableStateOf<SessionWrapper?>(null)

    fun onEvent(event: Event) {
        when (event) {
            is HomeEvent.SessionClicked -> {
                Timber.d("User clicked on session with id: ${event.sessionWrapper.session.sessionId}, name: ${event.sessionWrapper.session.sessionId}")
                sendUiEvent(UiEvent.Navigate("${Routes.SESSION}/${event.sessionWrapper.session.sessionId}"))
            }

            is HomeEvent.DeleteSessionRequested -> {
                sessionToDelete = event.sessionWrapper
            }

            is HomeEvent.ConfirmDeleteSession -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repo.deleteSessionById(event.sessionId)
                    Timber.d("Session ${event.sessionId} deleted.")
                    sessionToDelete = null
                }
            }

            is HomeEvent.OpenSettings -> {
                sendUiEvent(UiEvent.Navigate(Routes.SETTINGS))
            }

            is HomeEvent.NewSession -> {
                Timber.d("User clicked on new Session")
                viewModelScope.launch(Dispatchers.IO) {
                    repo.insertSession(Session())
                    val session = repo.getLastSession()
                    session?.let {
                        sendUiEvent(UiEvent.Navigate("${Routes.SESSION}/${it.sessionId}"))
                    }
                }
            }

            is HomeEvent.CheckUpdates -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val updated = repo.checkForUpdates()
                    val message = if (updated) "Base de datos actualizada" else "Ya está al día"
                    sendUiEvent(UiEvent.ShowSnackbar(message))
                }
            }
        }
    }
    suspend fun checkForUpdatesSuspend(lang: String = "es"): Boolean {
        val result = repo.checkForUpdates(lang)
        Timber.d("checkForUpdatesSuspend returned $result for lang=$lang")
        return result
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
