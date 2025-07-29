package com.example.exerlog.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.ui.ExerciseWrapper
import com.example.exerlog.ui.SessionWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repo: ExerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["session_id"])

    private val _session = MutableStateFlow(SessionWrapper(Session(), emptyList()))
    val session = _session.asStateFlow()

    val exercises = MutableStateFlow<List<ExerciseWrapper>>(emptyList())
    val expandedExercise = MutableStateFlow<ExerciseWrapper?>(null)
    val selectedExercises = MutableStateFlow<List<ExerciseWrapper>>(emptyList())
    val muscleGroups = MutableStateFlow<List<String>>(emptyList())

    init {
        // Aquí puedes cargar la sesión, ejercicios, etc, usando sessionId y repo
        // Por ejemplo:
        // viewModelScope.launch {
        //     val data = repo.getSessionWithExercises(sessionId)
        //     _session.value = data
        //     // cargar más estados según sea necesario
        // }
    }
}
