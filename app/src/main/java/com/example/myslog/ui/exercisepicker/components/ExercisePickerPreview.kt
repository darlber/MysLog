package com.example.myslog.ui.exercisepicker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.Workout
import com.example.myslog.ui.exercisepicker.ExerciseEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerPreview(
    exercises: List<Exercise>,
    selectedExercises: List<Exercise>,
    onAddClick: () -> Unit,
    onExerciseClick: (Exercise) -> Unit,
    onEvent: (ExerciseEvent) -> Unit,
    searchText: String,
    onSearchChanged: (String) -> Unit,
    filterSelected: Boolean = false,
    filterUsed: Boolean = false,
    muscleFilterActive: Boolean = false,
    equipmentFilterActive: Boolean = false,
    workoutFilter: List<Long> = emptyList(),
    allWorkouts: List<Workout> = emptyList(),
    onFilterSelectedClick: () -> Unit,
    onFilterUsedClick: () -> Unit,
    onMuscleFilterClick: () -> Unit,
    onEquipmentFilterClick: () -> Unit
) {
    FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    var showWorkoutDropdown by remember { mutableStateOf(false) }
    var newWorkoutName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedExercises.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "ADD ${selectedExercises.size}",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 👈 asegura que respete status bar y FAB
        ) {
            // 🔹 Barra superior fija
            ExercisePickerTopBar(
                searchText = searchText,
                onSearchChanged = onSearchChanged,
                filterSelected = filterSelected,
                filterUsed = filterUsed,
                muscleFilterActive = muscleFilterActive,
                equipmentFilterActive = equipmentFilterActive,
                workoutFilter = workoutFilter,
                allWorkouts = allWorkouts,
                showWorkoutDropdown = showWorkoutDropdown,
                onFilterSelectedClick = onFilterSelectedClick,
                onFilterUsedClick = onFilterUsedClick,
                onMuscleFilterClick = onMuscleFilterClick,
                onEquipmentFilterClick = onEquipmentFilterClick,
                onWorkoutToggle = { showWorkoutDropdown = !showWorkoutDropdown },
                onEvent = onEvent,
                newWorkoutName = newWorkoutName,
                onNewWorkoutNameChange = { newWorkoutName = it }
            )

            // 🔹 Lista debajo de la barra
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(exercises) { exercise ->
                    val isSelected = selectedExercises.contains(exercise)
                    ExerciseCard(exercise = exercise, selected = isSelected, onEvent = onEvent) {
                        onExerciseClick(exercise)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExercisePickerTopBar(
    searchText: String,
    onSearchChanged: (String) -> Unit,
    filterSelected: Boolean,
    filterUsed: Boolean,
    muscleFilterActive: Boolean,
    equipmentFilterActive: Boolean,
    workoutFilter: List<Long>,
    allWorkouts: List<Workout>,
    showWorkoutDropdown: Boolean,
    onFilterSelectedClick: () -> Unit,
    onFilterUsedClick: () -> Unit,
    onMuscleFilterClick: () -> Unit,
    onEquipmentFilterClick: () -> Unit,
    onWorkoutToggle: () -> Unit,
    onEvent: (ExerciseEvent) -> Unit,
    newWorkoutName: String,
    onNewWorkoutNameChange: (String) -> Unit
) {
    Column {
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChanged,
            label = { Text("Search", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            singleLine = true
        )

        FilterRow(
            filterSelected, filterUsed, muscleFilterActive, equipmentFilterActive,
            workoutFilter.isNotEmpty(), onFilterSelectedClick, onFilterUsedClick,
            onMuscleFilterClick, onEquipmentFilterClick, onWorkoutToggle
        )

        if (showWorkoutDropdown) {
            WorkoutDropdown(allWorkouts, workoutFilter.firstOrNull(), onEvent, newWorkoutName, onNewWorkoutNameChange)
        }
    }
}

@Composable
private fun FilterRow(
    filterSelected: Boolean,
    filterUsed: Boolean,
    muscleFilterActive: Boolean,
    equipmentFilterActive: Boolean,
    workoutFilterActive: Boolean,
    onFilterSelectedClick: () -> Unit,
    onFilterUsedClick: () -> Unit,
    onMuscleFilterClick: () -> Unit,
    onEquipmentFilterClick: () -> Unit,
    onWorkoutToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterChip(selected = filterSelected, onClick = onFilterSelectedClick, label = { Text("Selected") })
        FilterChip(selected = filterUsed, onClick = onFilterUsedClick, label = { Text("Used") })
        FilterChip(selected = muscleFilterActive, onClick = onMuscleFilterClick, label = { Icon(Icons.Default.AccessibilityNew, null) })
        FilterChip(selected = equipmentFilterActive, onClick = onEquipmentFilterClick, label = { Icon(Icons.Default.FitnessCenter, null) })
        FilterChip(selected = workoutFilterActive, onClick = onWorkoutToggle, label = { Icon(Icons.AutoMirrored.Filled.DirectionsRun, null) })
    }
}

@Composable
private fun WorkoutDropdown(
    allWorkouts: List<Workout>,
    selectedWorkoutId: Long?,
    onEvent: (ExerciseEvent) -> Unit,
    newWorkoutName: String,
    onNewWorkoutNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = newWorkoutName,
            onValueChange = onNewWorkoutNameChange,
            label = { Text("Nuevo Workout") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { if (newWorkoutName.isNotBlank()) onEvent(ExerciseEvent.AddWorkout(newWorkoutName)) }) {
            Text("Añadir")
        }
        Spacer(Modifier.height(8.dp))
        allWorkouts.forEach { workout ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                RadioButton(selected = workout.workoutId == selectedWorkoutId, onClick = { onEvent(ExerciseEvent.SelectWorkout(workout.workoutId)) })
                Text(workout.name, modifier = Modifier.weight(1f).padding(start = 8.dp))
                IconButton(onClick = { onEvent(ExerciseEvent.DeleteWorkout(workout.workoutId)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar Workout")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExercisePickerContentPreview() {
    val dummyExercises = listOf(
        Exercise(
            id = "1",
            name = "Push Up",
            force = "Push",
            level = "Beginner",
            mechanic = "Compound",
            equipment = "Bodyweight",
            primaryMuscles = listOf("Chest", "Triceps"),
            secondaryMuscles = listOf(),
            instructions = listOf("Place hands on the ground...", "Lower your body..."),
            category = "Strength",
            images = listOf()
        )
    )
    ExercisePickerPreview(
        exercises = dummyExercises,
        selectedExercises = listOf(dummyExercises.first()),
        onAddClick = {},
        onExerciseClick = {},
        onEvent = {},
        searchText = "",
        onSearchChanged = {},
        onFilterSelectedClick = {},
        onFilterUsedClick = {},
        onMuscleFilterClick = {},
        onEquipmentFilterClick = {},
        filterSelected = true,
        filterUsed = false,
        muscleFilterActive = true,
        equipmentFilterActive = false
    )
}
