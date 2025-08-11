package com.example.exerlog.ui.exercisepicker.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.ui.exercisepicker.ExerciseEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerPreview(
    exercises: List<Exercise>,
    selectedExercises: List<Exercise>,
    onAddClick: () -> Unit,
    onExerciseClick: (Exercise) -> Unit,
    onEvent: (ExerciseEvent) -> Unit,
    searchText: String,
    onSearchChanged: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier.height(64.dp).width(80.dp)) {
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
            Surface(
                shape = CutCornerShape(0.dp),
                tonalElevation = 2.dp
            ) {
                Column {
                    Spacer(Modifier.height(40.dp))
                    TextField(
                        value = searchText,
                        onValueChange = onSearchChanged,
                        label = {
                            Text(
                                //TODO localizacion
                                text = "Search",
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
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilterChip(selected = false, onClick = {}, label = { Text("Selected") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = false, onClick = {}, label = { Text("Used") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = false,
                            onClick = {},
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
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = false,
                            onClick = {},
                            label = {
                                Icon(
                                    Icons.Default.ShoppingCart,
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
                            }
                        )
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
            item {
                Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding() + 8.dp))
            }
            items(exercises) { exercise ->
                val isSelected = selectedExercises.contains(exercise)
                ExerciseCard(
                    exercise = exercise,
                    selected = isSelected,
                    onEvent = onEvent
                ) {
                    onExerciseClick(exercise)
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
            name = "Pene al fallo",
            force = "TODO()",
            level = "TODO()",
            mechanic = "TODO()",
            equipment = "Barbell",
            primaryMuscles = listOf("Chest", "Triceps"),
            secondaryMuscles = listOf("TODO()"),
            instructions = listOf("TODO()"),
            category = "TODO()",
            images = listOf("TODO()")
        )

    )
    ExercisePickerPreview(
        exercises = dummyExercises,
        selectedExercises = listOf(dummyExercises.first()),
        onAddClick = {},
        onExerciseClick = {},
        onEvent = {},
        searchText = "",
        onSearchChanged = {}
    )
}
