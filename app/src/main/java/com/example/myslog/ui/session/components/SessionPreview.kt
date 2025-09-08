package com.example.myslog.ui.session.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myslog.db.entities.Exercise
import com.example.myslog.db.entities.GymSet
import com.example.myslog.db.entities.Session
import com.example.myslog.db.entities.SessionExercise
import com.example.myslog.ui.ExerciseWrapper
import com.example.myslog.ui.SessionWrapper
import com.example.myslog.ui.TimerState
import com.example.myslog.ui.session.SessionEvent
import com.example.myslog.utils.Event
import com.example.myslog.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionPreview(
    session: SessionWrapper,
    exercises: List<ExerciseWrapper>,
    expandedExercise: ExerciseWrapper?,
    selectedExercises: List<ExerciseWrapper>,
    muscleGroups: List<String>,
    onEvent: (Event) -> Unit,
    onNavigate: (UiEvent.Navigate) -> Unit,
    deleteExerciseDialog: MutableState<Boolean>,
    deleteSessionDialog: MutableState<Boolean>,
    deleteSetDialog: MutableState<GymSet?>,
    timerVisible: MutableState<Boolean>,
    timerState: TimerState
) {
    val scrollState = rememberLazyListState()

    Scaffold(
        bottomBar = {
            SessionBottomBar(
                onDeleteSession = { deleteSessionDialog.value = true },
                onFinishSession = { onEvent(SessionEvent.FinishSession) },
                timerVisible = timerVisible.value,
                timerState = timerState,
                onTimerPress = { timerVisible.value = !timerVisible.value },
                onFAB = { onEvent(SessionEvent.AddExercise) },
                onEvent = onEvent
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            contentPadding = paddingValues
        ) {
            // Header
            item {
                HeaderSession(
                    sessionWrapper = session,
                    muscleGroups = muscleGroups,
                    topPadding = paddingValues.calculateTopPadding(),
                    scrollState = scrollState,
                    onStartTime = { newTime ->
                        onEvent(SessionEvent.StartTimeChanged(newTime.toLocalTime()))
                    },
                    onEndTime = { newTime ->
                        onEvent(SessionEvent.EndTimeChanged(newTime.toLocalTime()))
                    }
                )
            }

            // Lista de ejercicios
            itemsIndexed(
                items = exercises,
                key = { _, exercise -> exercise.sessionExercise.sessionExerciseId }
            ) { _, exercise ->
                val expanded =
                    exercise.sessionExercise.sessionExerciseId == expandedExercise?.sessionExercise?.sessionExerciseId
                val selected = selectedExercises.contains(exercise)

                SessionExerciseCard(
                    exerciseWrapper = exercise,
                    expanded = expanded,
                    selected = selected,
                    onEvent = onEvent,
                    onLongClick = { onEvent(SessionEvent.ExerciseSelected(exercise)) },
                    onSetDeleted = { deleteSetDialog.value = it }
                ) {
                    onEvent(SessionEvent.ExerciseExpanded(exercise))
                }
            }

            // Spacer para el bottom bar
            item {
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
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
        instructions = listOf(
            "Keep your body straight",
            "Lower yourself until your chest nearly touches the floor"
        ),
        category = "Strength",
        images = listOf()
    )

    val dummySessionExercise = SessionExercise(
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
    val deleteExerciseDialog = remember { mutableStateOf(false) }
    val deleteSessionDialog = remember { mutableStateOf(false) }
    val deleteSetDialog = remember { mutableStateOf<GymSet?>(null) }
    val timerVisible = remember { mutableStateOf(true) }
    val timerState = TimerState(running = false, time = 0L, maxTime = 0L)

    SessionPreview(
        session = dummySession,
        exercises = listOf(dummyExerciseWrapper),
        expandedExercise = null,
        selectedExercises = emptyList(),
        muscleGroups = dummyMuscleGroups,
        onEvent = {},
        onNavigate = {},
        deleteExerciseDialog = deleteExerciseDialog,
        deleteSessionDialog = deleteSessionDialog,
        deleteSetDialog = deleteSetDialog,
        timerVisible = timerVisible,
        timerState = timerState
    )
}
