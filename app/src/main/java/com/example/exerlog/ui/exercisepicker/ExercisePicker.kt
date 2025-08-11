package com.example.exerlog.ui.exercisepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.exerlog.ui.exercisepicker.components.ExercisePickerPreview
import com.example.exerlog.ui.exercisepicker.components.ImagePopup
import com.example.exerlog.utils.UiEvent
import timber.log.Timber

@Composable
fun ExercisePicker(
    navController: NavController,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    Timber.d("ExercisePickerScreen: Composable launched")
    val exercises by viewModel.filteredExercises.collectAsState(initial = emptyList())
    val selectedExercises by viewModel.selectedExercises.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    var showPopupExerciseId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("ExercisePickerScreen: Event received: $event")
            when (event) {
                is UiEvent.ShowImagePopup -> {
                    showPopupExerciseId = event.exerciseId
                    keyboardController?.hide()

                }

                else -> {
                    // Otros eventos si necesitas manejarlos
                }
            }
        }
    }
    val searchText by viewModel.searchText.collectAsState()
    ExercisePickerPreview(
        exercises = exercises,
        selectedExercises = selectedExercises,
        onAddClick = {
            navController.popBackStack()
            viewModel.onEvent(ExerciseEvent.AddExercises)
        },
        onExerciseClick = { exercise ->
            viewModel.onEvent(ExerciseEvent.ExerciseSelected(exercise))
        },
        onSearchChanged = { text -> viewModel.onEvent(ExerciseEvent.SearchChanged(text)) },
        searchText = searchText,
        onEvent = viewModel::onEvent
    )
    // Mostrar popup si hay ejercicio seleccionado para popup
    if (showPopupExerciseId != null) {
        val exercise = exercises.find { it.id == showPopupExerciseId }
        if (exercise != null) {
            ImagePopup(
                exercise = exercise,
                onDismiss = { showPopupExerciseId = null }
            )
        }
    }
}
