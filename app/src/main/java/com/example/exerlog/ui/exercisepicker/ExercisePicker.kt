package com.example.exerlog.ui.exercisepicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.exerlog.db.entities.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ExercisePicker(
    navController: NavController,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    Timber.d("ExercisePicker: Composable lanzado")
    val exercises by viewModel.filteredExercises.collectAsState(initial = emptyList())
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    Timber.d("ExercisePicker: Exercises loaded = ${exercises.size}, Selected = ${selectedExercises.size}")
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("ExercisePicker: UiEvent recibido -> $event")
            when (event) {
//                is UiEvent.OpenWebsite -> {
//                    uriHandler.openUri(event.url)
//                }
                else -> Unit
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .width(80.dp)
            ) {
                AnimatedVisibility(
                    visible = selectedExercises.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.popBackStack()
                            viewModel.onEvent(ExerciseEvent.AddExercises)
                        },
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
                        value = "",
                        onValueChange = {},
                        label = {
                            Text(
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
                                    Icons.Default.Check,
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
            items(exercises.size) { index ->
                val exercise = exercises[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

class PreviewExerciseViewModel : ViewModel() {
    private val _filteredExercises = MutableStateFlow(List(5) {
        Exercise(
            id = "id$it",
            name = "Exercise $it",
            force = null,
            level = null,
            mechanic = null,
            equipment = null,
            primaryMuscles = listOf("Biceps"),
            secondaryMuscles = listOf("Triceps"),
            instructions = emptyList(),
            category = null,
            images = emptyList()
        )
    })
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

    private val _selectedExercises = MutableStateFlow(emptyList<Exercise>())
    val selectedExercises: StateFlow<List<Exercise>> = _selectedExercises
}

@Preview(showBackground = true)
@Composable
fun ExercisePickerSkeletonPreview() {
    val navController = rememberNavController()
    val previewViewModel = remember { PreviewExerciseViewModel() }
    ExercisePicker(
        navController = navController,
        //
    )
}