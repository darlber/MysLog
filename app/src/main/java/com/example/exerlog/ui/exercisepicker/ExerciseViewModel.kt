package com.example.exerlog.ui.exercisepicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.db.repository.ExerRepository
import com.example.exerlog.utils.Event
import com.example.exerlog.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repo: ExerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val selectedExercises = _selectedExercises.asStateFlow()

    private val _equipmentFilter = MutableStateFlow<List<String>>(emptyList())
    val equipmentFilter = _equipmentFilter.asStateFlow()

    private val _muscleFilter = MutableStateFlow<List<String>>(emptyList())
    val muscleFilter = _muscleFilter.asStateFlow()

    private val _filterSelected = MutableStateFlow(false)
    val filterSelected = _filterSelected.asStateFlow()

    private val _filterUsed = MutableStateFlow(false)
    val filterUsed = _filterUsed.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Lista de todos los equipos disponibles
    private val _allEquipment = MutableStateFlow<List<String>>(emptyList())

    val filteredExercises = combine(
        repo.getAllExercises(),
        selectedExercises,
        equipmentFilter,
        muscleFilter,
        filterSelected,
        filterUsed,
        searchText
    ) { exercises, selected, equipment, muscles, selActive, usedActive, query ->
        exercises.filter { exercise ->
            val muscleGroups =
                (exercise.primaryMuscles + exercise.secondaryMuscles).map { it.lowercase() }
            val muscleCondition =
                muscles.isEmpty() || muscles.any { it.lowercase() in muscleGroups }

            val equipmentCondition =
                equipment.isEmpty() || equipment.contains(exercise.equipment.orEmpty())

            val selectedCondition = !selActive || selected.contains(exercise)

            val searchCondition =
                query.isBlank() || exercise.name.contains(query, ignoreCase = true)

            muscleCondition && equipmentCondition && selectedCondition && searchCondition
        }.sortedBy { exercise ->
            if (query.isNotBlank()) {
                exercise.name.length
            } else {
                exercise.name.firstOrNull()?.code ?: 0
            }
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Cargar lista de equipos desde la DB
        viewModelScope.launch {
            repo.getAllEquipment().collect { equipmentList ->
                _allEquipment.value = equipmentList
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is ExerciseEvent.OpenGuide -> openGuide(event.exercise)
            is ExerciseEvent.ExerciseSelected -> {
                _selectedExercises.value = _selectedExercises.value.toMutableList().apply {
                    if (contains(event.exercise)) remove(event.exercise)
                    else add(event.exercise)
                }
            }

            is ExerciseEvent.FilterSelected -> _filterSelected.value = !_filterSelected.value
            is ExerciseEvent.FilterUsed -> _filterUsed.value = !_filterUsed.value
            is ExerciseEvent.SelectMuscle -> {
                _muscleFilter.value = _muscleFilter.value.toMutableList().apply {
                    if (contains(event.muscle)) remove(event.muscle) else add(event.muscle)
                }
            }

            is ExerciseEvent.DeselectMuscles -> _muscleFilter.value = emptyList()
            is ExerciseEvent.SelectEquipment -> {
                _equipmentFilter.value = _equipmentFilter.value.toMutableList().apply {
                    if (contains(event.equipment)) remove(event.equipment) else add(event.equipment)
                }
            }

            is ExerciseEvent.DeselectEquipment -> _equipmentFilter.value = emptyList()
            is ExerciseEvent.AddExercises -> {
                viewModelScope.launch {
                    _selectedExercises.value.forEach { exercise ->
                        savedStateHandle.get<Long>("session_id")?.let { sessionId ->
                            repo.insertSessionExercise(
                                SessionExercise(
                                    parentSessionId = sessionId,
                                    parentExerciseId = exercise.id,
                                )
                            )
                        }
                    }
                }
            }

            is ExerciseEvent.SearchChanged -> _searchText.value = event.text
        }
    }

    private fun openGuide(exercise: Exercise) {
        sendUiEvent(UiEvent.ShowImagePopup(exercise.id))
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}

inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> {
    return combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }
}
