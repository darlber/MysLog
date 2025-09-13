package com.example.myslog.ui.exercisepicker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
    onFilterSelectedClick: () -> Unit,
    onFilterUsedClick: () -> Unit,
    onMuscleFilterClick: () -> Unit,
    onEquipmentFilterClick: () -> Unit,
    filterSelected: Boolean = false,
    filterUsed: Boolean = false,
    muscleFilterActive: Boolean = false,
    equipmentFilterActive: Boolean = false,
    workoutFilter: List<Long> = emptyList(),
    allWorkouts: List<Workout> = emptyList()
) {
    val filterColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    var showWorkoutNameDialog by remember { mutableStateOf(false) }
    var workoutName by remember { mutableStateOf("") }

    var showWorkoutDropdown by remember { mutableStateOf(false) }
    var newWorkoutName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier
                .height(64.dp)
                .width(80.dp)) {
                AnimatedVisibility(
                    visible = selectedExercises.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = onAddClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "ADD ${selectedExercises.size}",
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 10.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        topBar = {
            Surface(shape = CutCornerShape(0.dp), tonalElevation = 2.dp) {
                Column {
                    Spacer(Modifier.height(40.dp))
                    TextField(
                        value = searchText,
                        onValueChange = onSearchChanged,
                        label = {
                            Text(
                                "Search",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = filterSelected,
                            onClick = onFilterSelectedClick,
                            label = { Text("Selected") },
                            colors = filterColors
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = filterUsed,
                            onClick = onFilterUsedClick,
                            label = { Text("Used") },
                            colors = filterColors
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = muscleFilterActive,
                            onClick = onMuscleFilterClick,
                            label = {
                                Icon(
                                    Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            colors = filterColors
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = equipmentFilterActive,
                            onClick = onEquipmentFilterClick,
                            label = {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            colors = filterColors
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = workoutFilter.isNotEmpty(),
                            onClick = { showWorkoutDropdown = !showWorkoutDropdown },
                            label = {
                                Icon(
                                    Icons.AutoMirrored.Filled.DirectionsRun,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            colors = filterColors
                        )
                    }

                    AnimatedVisibility(visible = showWorkoutDropdown) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newWorkoutName,
                                onValueChange = { newWorkoutName = it },
                                label = { Text("Nuevo Workout") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (newWorkoutName.isNotBlank()) {
                                        onEvent(ExerciseEvent.AddWorkout(newWorkoutName))
                                        newWorkoutName = ""
                                    }
                                },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text("Añadir")
                            }

                            Spacer(Modifier.height(8.dp))

                            val selectedWorkoutId = workoutFilter.firstOrNull()
                            allWorkouts.forEach { workout ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = workout.workoutId == selectedWorkoutId,
                                        onClick = { onEvent(ExerciseEvent.SelectWorkout(workout.workoutId)) }
                                    )
                                    Text(
                                        workout.name,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onEvent(ExerciseEvent.DeleteWorkout(workout.workoutId)) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Borrar Workout"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding() + 8.dp)) }
            items(exercises) { exercise ->
                val isSelected = selectedExercises.contains(exercise)
                ExerciseCard(
                    exercise = exercise,
                    selected = isSelected,
                    onEvent = onEvent
                ) { onExerciseClick(exercise) }
            }
        }
    }

    if (showWorkoutNameDialog) {
        AlertDialog(
            onDismissRequest = { showWorkoutNameDialog = false },
            title = { Text("Nombre del Workout") },
            text = {
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Ingrese un nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                FilledTonalButton(onClick = {
                    if (workoutName.isNotBlank()) {
                        showWorkoutNameDialog = false
                        workoutName = ""
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showWorkoutNameDialog = false
                    workoutName = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
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
