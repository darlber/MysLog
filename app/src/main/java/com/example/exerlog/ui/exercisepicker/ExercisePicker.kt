package com.example.exerlog.ui.exercisepicker

import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.exerlog.ui.exercisepicker.components.EquipmentSheet
import com.example.exerlog.ui.modalbottomsheet.ModalBottomSheetLayout
import com.example.exerlog.ui.modalbottomsheet.ModalBottomSheetValue
import com.example.exerlog.ui.modalbottomsheet.rememberModalBottomSheetState
import com.example.exerlog.utils.UiEvent
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import timber.log.Timber
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePicker(
    navController: NavController,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    Timber.d("ExercisePickerScreen: Composable launched")
    val exercises by viewModel.filteredExercises.collectAsState(initial = emptyList())
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val muscleFilter by viewModel.muscleFilter.collectAsState()
    val equipmentFilter by viewModel.equipmentFilter.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var equipmentBottomsheet by remember { mutableStateOf(false) }

    var showPopupExerciseId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("ExercisePickerScreen: Event received: $event")
            when (event) {
                is UiEvent.ShowImagePopup -> {
                    showPopupExerciseId = event.exerciseId
                    keyboardController?.hide()
                }
                else -> Unit
            }
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            if (equipmentBottomsheet) {
//                EquipmentSheet(
//                    selectedEquipment = equipmentFilter,  // tu filtro actual
//                    allEquipment = viewModel.allEquipment, // lista completa que debes definir en el ViewModel
//                    onEvent = viewModel::onEvent
//                )
//            } else {
//                MuscleSheet(
//                    selectedMuscles = viewModel.selectedMusclesList,     // Lista de músculos seleccionados
//                    onEvent = viewModel::onEvent                           // Función de eventos
//                )
            }
        },
        sheetState = sheetState,
        sheetShape = MaterialTheme.shapes.large
    )

    {
        ExercisePickerPreview(
            exercises = exercises,
            selectedExercises = selectedExercises,
            onAddClick = {
                viewModel.onEvent(ExerciseEvent.AddExercises)
                navController.popBackStack()
            },
            onExerciseClick = { exercise ->
                viewModel.onEvent(ExerciseEvent.ExerciseSelected(exercise))
            },
            onSearchChanged = { text -> viewModel.onEvent(ExerciseEvent.SearchChanged(text)) },
            searchText = searchText,
            onEvent = viewModel::onEvent,
            onFilterSelectedClick = { viewModel.onEvent(ExerciseEvent.FilterSelected) },
            onFilterUsedClick = { viewModel.onEvent(ExerciseEvent.FilterUsed) },
            onMuscleFilterClick = {
                equipmentBottomsheet = false
                coroutineScope.launch {
                    if (sheetState.isVisible) sheetState.hide() else {
                        keyboardController?.hide()
                        sheetState.show()
                    }
                }
            },
            onEquipmentFilterClick = {
                equipmentBottomsheet = true
                coroutineScope.launch {
                    if (sheetState.isVisible) sheetState.hide() else {
                        keyboardController?.hide()
                        sheetState.show()
                    }
                }
            }
        )
    }

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
