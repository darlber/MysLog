package com.example.exerlog.ui.exercisepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.exerlog.ui.exercisepicker.components.ExercisePickerPreview
import timber.log.Timber

@Composable
fun ExercisePicker(
    navController: NavController,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    Timber.d("ExercisePickerScreen: Composable launched")
    val exercises by viewModel.filteredExercises.collectAsState(initial = emptyList())
    val selectedExercises by viewModel.selectedExercises.collectAsState()

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("ExercisePickerScreen: UiEvent received -> $event")
            // Handle UI events if necessary
        }
    }

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
        onEvent = viewModel::onEvent
    )
}
