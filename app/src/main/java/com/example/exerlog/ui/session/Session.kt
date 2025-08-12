package com.example.exerlog.ui.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.home.HomeEvent
import com.example.exerlog.ui.home.components.HomeBottomBar
import com.example.exerlog.ui.session.components.HeaderSession
import com.example.exerlog.ui.session.components.SessionPreview
import com.example.exerlog.utils.UiEvent
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val session by viewModel.session.collectAsState(SessionWrapper(Session(), emptyList()))
    val exercises by viewModel.exercises.collectAsState(initial = emptyList())
    val expandedExercise by viewModel.expandedExercise.collectAsState()
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val muscleGroups by viewModel.muscleGroups.collectAsState(emptyList())
//    val startTimeDialogState = rememberMaterialDialogState()
//    val endTimeDialogState = rememberMaterialDialogState()
    val scrollState = rememberLazyListState()
    val headerHeight = 120.dp
    val coroutineScope = rememberCoroutineScope()
    val timerVisible = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("UiEvent Received: $event")
            when (event) {
                is UiEvent.OpenWebsite -> {
                    uriHandler.openUri(event.url)
                }

                is UiEvent.Navigate -> onNavigate(event)
//                is UiEvent.ToggleTimer -> context.sendTimerAction(TimerService.Actions.TOGGLE)
//                is UiEvent.ResetTimer -> context.sendTimerAction(TimerService.Actions.RESET)
//                is UiEvent.IncrementTimer -> context.sendTimerAction(TimerService.Actions.INCREMENT)
//                is UiEvent.DecrementTimer -> context.sendTimerAction(TimerService.Actions.DECREMENT)
                else -> Unit
            }
        }
    }

    SessionPreview(
        session = session,
        exercises = exercises,
        expandedExercise = expandedExercise,
        selectedExercises = selectedExercises,
        muscleGroups = muscleGroups,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate
    )
}


private fun Unit.show() {
    TODO("Not yet implemented")
}

fun rememberMaterialDialogState() {
    TODO("Not yet implemented")
}


