package com.example.exerlog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exerlog.core.Routes
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.ui.SessionWrapper
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

    // Combine sesiones y ejercicios de la sesión para generar SessionWrapper con músculos ordenados
    val sessions = combine(
        repo.getAllSessionExercises(),
        repo.getAllSessions()
    ) { sewes, sessions ->

        sessions.map { session ->
            // Filtra SessionExerciseWithExercise que pertenecen a la sesión actual
            val relatedExercises = sewes.filter { it.sessionExercise.parentSessionId == session.sessionId }
                .map { it.exercise }

            // Ahora, usando tu función para sacar músculos ordenados
            val muscleGroups = relatedExercises.sortedListOfMuscleGroups()

            SessionWrapper(session, muscleGroups)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Canal para eventos UI
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SessionClicked -> {
                Timber.d("User clicked on session with id: ${event.sessionWrapper.session.sessionId}, name: ${event.sessionWrapper.session.sessionId}")
                sendUiEvent(UiEvent.Navigate("${Routes.SESSION}/${event.sessionWrapper.session.sessionId}"))
            }
            is HomeEvent.OpenSettings -> {
                sendUiEvent(UiEvent.Navigate(Routes.SETTINGS))
            }
            is HomeEvent.NewSession -> {
                Timber.d("User clicked on new Session" )
                viewModelScope.launch(Dispatchers.IO) {
                    repo.insertSession(Session())
                    val session = repo.getLastSession()
                    session?.let {
                        sendUiEvent(UiEvent.Navigate("${Routes.SESSION}/${it.sessionId}"))
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
