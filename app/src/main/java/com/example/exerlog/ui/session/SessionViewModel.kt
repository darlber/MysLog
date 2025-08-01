package com.example.exerlog.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exerlog.core.Routes
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.ui.ExerciseWrapper
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.utils.Event
import com.example.exerlog.utils.UiEvent
import com.example.exerlog.utils.sortedListOfMuscleGroups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repo: ExerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["session_id"])
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _session = MutableStateFlow(Session())
    val session = _session.asStateFlow().map {
        SessionWrapper(it, emptyList())
    }

    private val _expandedExercise = MutableStateFlow<ExerciseWrapper?>(null)
    val expandedExercise = _expandedExercise.asStateFlow()

    private val _selectedExercises = MutableStateFlow<List<ExerciseWrapper>>(emptyList())
    val selectedExercises = _selectedExercises.asStateFlow()

    val exercises = combine(
        repo.getExercisesForSession(_session),
        repo.getAllSets()
    ) { exercises, sets ->
        exercises.map { sewe ->
            ExerciseWrapper(
                sessionExercise = sewe.sessionExercise,
                exercise = sewe.exercise,
                sets = sets.filter { set ->
                    set.parentSessionExerciseId == sewe.sessionExercise.sessionExerciseId
                }
            )
        }
    }

    val muscleGroups = exercises.map { exercises ->
        exercises.map { it.exercise }.sortedListOfMuscleGroups()
    }

    init {
        savedStateHandle.get<Long>("session_id")?.let { sessionId ->
            Timber.d("Session ID: $sessionId")
            viewModelScope.launch(Dispatchers.IO) {
                repo.getSessionById(sessionId)
                    .collect { sessionValue ->
                        _session.value = sessionValue
                    }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is SessionEvent.ExerciseExpanded -> {
                event.exercise.let { se ->
                    if (se.sessionExercise.sessionExerciseId == _expandedExercise.value?.sessionExercise?.sessionExerciseId) {
                        _expandedExercise.value = null
                    } else {
                        _expandedExercise.value = se
                    }
                    _selectedExercises.value = emptyList()
                }
            }

            is SessionEvent.ExerciseSelected -> {
                _selectedExercises.value = buildList {
                    if (_selectedExercises.value.contains(event.exercise)) {
                        addAll(_selectedExercises.value.minusElement(event.exercise))
                    } else {
                        addAll(_selectedExercises.value)
                        add(event.exercise)
                    }
                }
                _expandedExercise.value = null
            }

            is SessionEvent.SetChanged -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        repo.updateSet(event.updatedSet)
                    }
                }
            }

            is SessionEvent.SetCreated -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        repo.createSet(event.sessionExercise.sessionExercise)
                    }
                }
            }

            is SessionEvent.SetDeleted -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        repo.deleteSet(event.set)
                    }
                }
            }

            is SessionEvent.TimerToggled -> sendUiEvent(UiEvent.ToggleTimer)
            is SessionEvent.TimerReset -> sendUiEvent(UiEvent.ResetTimer)
            is SessionEvent.TimerIncreased -> sendUiEvent(UiEvent.IncrementTimer)
            is SessionEvent.TimerDecreased -> sendUiEvent(UiEvent.DecrementTimer)
            is SessionEvent.OpenGuide -> {
                expandedExercise.value?.exercise?.let { openGuide(it) }
            }

            is SessionEvent.AddExercise -> {
                _session.value.sessionId.let { id ->
                    sendUiEvent(UiEvent.Navigate("${Routes.EXERCISE_PICKER}/$id"))
                }
            }

            is SessionEvent.RemoveSelectedExercises -> {
                viewModelScope.launch {
                    _selectedExercises.value.forEach {
                        repo.removeSessionExercise(it.sessionExercise)
                    }
                    _selectedExercises.value = emptyList()
                }
            }

            is SessionEvent.RemoveSession -> {
                sendUiEvent(UiEvent.Navigate(Routes.HOME, popBackStack = true))
                viewModelScope.launch {
                    repo.removeSession(_session.value)
                }
            }

            is SessionEvent.DeselectExercises -> {
                _selectedExercises.value = emptyList()
            }

            is SessionEvent.EndTimeChanged -> {
                var session = _session.value
                val date = session.end?.toLocalDate() ?: session.start.toLocalDate()
                val newEndTime = LocalDateTime.of(date, event.newTime)
                viewModelScope.launch {
                    repo.updateSession(
                        session.copy(
                            end = newEndTime
                        ).also { session = it }
                    )
                    launch(Dispatchers.IO) {
                        repo.getSessionById(sessionId)
                            .collect { newSession -> _session.value = newSession }
                    }

                }
            }

            is SessionEvent.StartTimeChanged -> {
                val session = _session.value
                val newStartTime = LocalDateTime.of(session.start.toLocalDate(), event.newTime)
                viewModelScope.launch {
                    repo.updateSession(
                        session.copy(
                            start = newStartTime
                        )
                    )
                    launch(Dispatchers.IO) {
                        repo.getSessionById(sessionId)
                            .collect { newSession -> _session.value = newSession }
                    }

                }
            }

            else -> Unit
        }
    }

    private fun openGuide(exercise: Exercise) {
        sendUiEvent(UiEvent.OpenWebsite(url = "https://duckduckgo.com/?q=! exrx ${exercise.name}"))
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}