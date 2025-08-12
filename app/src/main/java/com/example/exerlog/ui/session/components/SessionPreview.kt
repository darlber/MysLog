package com.example.exerlog.ui.session.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise
import com.example.exerlog.ui.ExerciseWrapper
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.home.HomeEvent
import com.example.exerlog.ui.home.components.HomeBottomBar
import com.example.exerlog.ui.session.SessionEvent
import com.example.exerlog.ui.session.components.HeaderSession
import com.example.exerlog.utils.Event
import com.example.exerlog.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionPreview(
    session: SessionWrapper,
    exercises: List<ExerciseWrapper>,
    expandedExercise: ExerciseWrapper?,
    selectedExercises: List<ExerciseWrapper>,
    muscleGroups: List<String>,
    onEvent: (Event) -> Unit,
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    val scrollState = rememberLazyListState()
    Scaffold(
        bottomBar = {
            HomeBottomBar { event ->
                when (event) {
                    is HomeEvent.NewSession -> onEvent(SessionEvent.AddExercise)
                    is HomeEvent.OpenSettings -> onNavigate(UiEvent.Navigate("settings"))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
             HeaderSession(
                sessionWrapper = session,
                muscleGroups = muscleGroups,
                topPadding = paddingValues.calculateTopPadding(),
                onEndTime = { },
                scrollState = scrollState,
                onStartTime = { }
            )
            LazyColumn(state = scrollState) {
                itemsIndexed(
                    items = exercises,
                    key = { _, exercise -> exercise.sessionExercise.sessionExerciseId }
                ) { index, exercise ->
                    val expanded = exercise.sessionExercise.sessionExerciseId == expandedExercise?.sessionExercise?.sessionExerciseId
                    val selected = selectedExercises.contains(exercise)
                    SessionExerciseCard(
                        exerciseWrapper = exercise,
                        expanded = expanded,
                        selected = selected,
                        onEvent = onEvent,
                        onLongClick = { onEvent(SessionEvent.ExerciseSelected(exercise)) },
                        onSetDeleted = { /* TODO if needed */ }
                    ) {
                        onEvent(SessionEvent.ExerciseExpanded(exercise))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionScreenPreviewContent() {
    val dummyExercise = Exercise(
        id = "1",
        name = "Push Up",
        force = "Push",
        level = "Beginner",
        mechanic = "Compound",
        equipment = "Bodyweight",
        primaryMuscles = listOf("Chest", "Triceps"),
        secondaryMuscles = listOf("Shoulders"),
        instructions = listOf("Keep your body straight", "Lower yourself until your chest nearly touches the floor"),
        category = "Strength",
        images = listOf()
    )

    val dummySessionExercise = com.example.exerlog.db.entities.SessionExercise(
        sessionExerciseId = 1L,
        parentSessionId = 1L,
        parentExerciseId = "1"
    )

    val dummyExerciseWrapper = ExerciseWrapper(
        sessionExercise = dummySessionExercise,
        exercise = dummyExercise,
        sets = emptyList()
    )

    val dummySession = SessionWrapper(
        session = Session(sessionId = 1L),
        muscleGroups = listOf("Chest", "Triceps", "Shoulders")
    )

    val dummyMuscleGroups = listOf("Chest", "Triceps", "Shoulders")

    SessionPreview(
        session = dummySession,
        exercises = listOf(dummyExerciseWrapper),
        expandedExercise = null,
        selectedExercises = emptyList(),
        muscleGroups = dummyMuscleGroups,
        onEvent = {},
        onNavigate = {}
    )
}
