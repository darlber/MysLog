package com.example.exerlog.ui.exercisepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.exerlog.ui.exercisepicker.components.EquipmentSheet
import com.example.exerlog.ui.exercisepicker.components.ExercisePickerPreview
import com.example.exerlog.ui.exercisepicker.components.ImagePopup
import com.example.exerlog.ui.exercisepicker.components.MuscleSheet
import com.example.exerlog.ui.session.components.StatEntry
import com.example.exerlog.ui.session.components.StatsPopup
import com.example.exerlog.utils.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePicker(
    navController: NavController, viewModel: ExerciseViewModel = hiltViewModel()
) {
    val exercises by viewModel.filteredExercises.collectAsState(initial = emptyList())
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val equipmentFilter by viewModel.equipmentFilter.collectAsState()
    val muscleFilter by viewModel.muscleFilter.collectAsState()
    val context = LocalContext.current

    val allEquipment by viewModel._allEquipment.collectAsState() // <- lista completa desde DB
    val allMusclesList by viewModel._allMuscles.collectAsState() // <- lista completa desde DB


    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var equipmentBottomsheet by remember { mutableStateOf(false) }
    var showPopupExerciseId by remember { mutableStateOf<String?>(null) }
    var statsPopupSets by remember { mutableStateOf<List<StatEntry>?>(null) }
    statsPopupSets?.let { stats ->
        StatsPopup(
            stats = stats, onDismiss = { statsPopupSets = null })
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowImagePopup -> {
                    showPopupExerciseId = event.exerciseId
                    keyboardController?.hide()
                }

                is UiEvent.ShowStatsPopup -> {
                    statsPopupSets = event.stats // ahora statsPopupSets es List<StatEntry>
                    keyboardController?.hide()
                }

                else -> Unit
            }
        }
    }
    val filterSelected by viewModel.filterSelected.collectAsState()
    val filterUsed by viewModel.filterUsed.collectAsState()

    // Contenido principal
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
                keyboardController?.hide()
                sheetState.show()
            }
        },
        onEquipmentFilterClick = {
            equipmentBottomsheet = true
            coroutineScope.launch {
                keyboardController?.hide()
                sheetState.show()
            }
        },
        filterSelected = filterSelected,
        filterUsed = filterUsed,
        muscleFilterActive = muscleFilter.isNotEmpty(),
        equipmentFilterActive = equipmentFilter.isNotEmpty()
    )

    // BottomSheet oficial de Material3
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
            }, sheetState = sheetState
        ) {
            if (equipmentBottomsheet) {
                EquipmentSheet(
                    selectedEquipment = equipmentFilter,
                    allEquipment = allEquipment, // <- usamos los datos de la DB
                    onEvent = viewModel::onEvent
                )
            } else {
                MuscleSheet(
                    selectedMusclegroups = muscleFilter,
                    allMuscleGroups = allMusclesList,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }

    // Popup de imagen del ejercicio
    if (showPopupExerciseId != null) {
        val exercise = exercises.find { it.id == showPopupExerciseId }
        if (exercise != null) {
            ImagePopup(
                exercise = exercise, onDismiss = { showPopupExerciseId = null })
        }
    }
}
