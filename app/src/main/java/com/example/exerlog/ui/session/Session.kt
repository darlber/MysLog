package com.example.exerlog.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exerlog.core.Routes
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.TimerState
import com.example.exerlog.ui.session.actions.FinishResult
import com.example.exerlog.ui.session.components.DeletionAlertDialog
import com.example.exerlog.ui.session.components.HeaderSession
import com.example.exerlog.ui.session.components.SessionPreview
import com.example.exerlog.utils.TimerService
import com.example.exerlog.utils.UiEvent
import com.example.exerlog.utils.sendTimerAction
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    // Estado de dominio desde el ViewModel
    val session by viewModel.session.collectAsState(SessionWrapper(Session(), emptyList()))
    val exercises by viewModel.exercises.collectAsState(initial = emptyList())
    val expandedExercise by viewModel.expandedExercise.collectAsState()
    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val muscleGroups by viewModel.muscleGroups.collectAsState(emptyList())

    // ---- Estado de UI efímero ----
    val deleteExerciseDialog = remember { mutableStateOf(false) }
    val deleteSessionDialog = remember { mutableStateOf(false) }
    val deleteSetDialog = remember { mutableStateOf<GymSet?>(null) }
    val timerVisible = remember { mutableStateOf(false) }
    val timerState = remember { mutableStateOf(TimerState(0L, false, 0L)) }
    val finishResult = remember { mutableStateOf<FinishResult?>(null) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    timerState.value = TimerState(
                        time = it.getLongExtra(TimerService.Intents.Extras.TIME.toString(), 0L),
                        running = it.getBooleanExtra(TimerService.Intents.Extras.IS_RUNNING.toString(), false),
                        maxTime = it.getLongExtra(TimerService.Intents.Extras.MAX_TIME.toString(), 0L)
                    )
                }
            }
        }

        val filter = IntentFilter(TimerService.Intents.STATUS.toString())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires explicit receiver flag
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }

        context.sendTimerAction(TimerService.Actions.QUERY)

        onDispose { context.unregisterReceiver(receiver) }
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            Timber.d("UiEvent Received: $event")
            when (event) {
                is UiEvent.OpenWebsite -> uriHandler.openUri(event.url)
                is UiEvent.Navigate -> onNavigate(event)
                is UiEvent.ToggleTimer -> context.sendTimerAction(TimerService.Actions.TOGGLE)
                is UiEvent.ResetTimer -> context.sendTimerAction(TimerService.Actions.RESET)
                is UiEvent.IncrementTimer -> context.sendTimerAction(TimerService.Actions.INCREMENT)
                is UiEvent.DecrementTimer -> context.sendTimerAction(TimerService.Actions.DECREMENT)
                is UiEvent.ShowFinishResult -> finishResult.value = event.result
                is UiEvent.FileCreated -> TODO()
                is UiEvent.ShowImagePopup -> TODO()
                is UiEvent.ShowStatsPopup -> TODO()
                is UiEvent.ShowSnackbar -> Timber.d("Snackbar requested with message: ${event.message}")
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
        onNavigate = onNavigate,
        deleteExerciseDialog = deleteExerciseDialog,
        deleteSessionDialog = deleteSessionDialog,
        deleteSetDialog = deleteSetDialog,
        timerVisible = timerVisible,
        timerState = timerState.value
    )

    // ---- Diálogos ----
    if (deleteExerciseDialog.value) {
        DeletionAlertDialog(
            onDismiss = { deleteExerciseDialog.value = false },
            onDelete = {
                viewModel.onEvent(SessionEvent.RemoveSelectedExercises)
                deleteExerciseDialog.value = false
            },
            title = { Text("Remove ${selectedExercises.size} Exercise${if (selectedExercises.size > 1) "s" else ""}?") },
            text = { Text("Are you sure you want to remove the selected exercises from this session? This action can not be undone.") }
        )
    }

    if (deleteSessionDialog.value) {
        DeletionAlertDialog(
            onDismiss = { deleteSessionDialog.value = false },
            onDelete = {
                viewModel.onEvent(SessionEvent.RemoveSession)
                deleteSessionDialog.value = false
            },
            title = { Text("Delete Session?") },
            text = { Text("Are you sure you want to delete this session and all of its contents? This action can not be undone.") }
        )
    }

    if (deleteSetDialog.value != null) {
        DeletionAlertDialog(
            onDismiss = { deleteSetDialog.value = null },
            onDelete = {
                deleteSetDialog.value?.let { viewModel.onEvent(SessionEvent.SetDeleted(it)) }
                deleteSetDialog.value = null
            },
            title = { Text("Delete Set?") },
            text = { Text("Are you sure you want to delete this set? This action can not be undone.") }
        )
    }

    // ---- Popup de resultados al finalizar ----
    if (finishResult.value != null) {
        AlertDialog(
            onDismissRequest = { finishResult.value = null },
            title = { Text("Sesión terminada 🎉") },
            text = {
                Column {
                    finishResult.value!!.exerciseVolumes.forEach { ev ->
                        Text("${ev.exerciseName}: ${ev.volume}")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Total volumen: ${finishResult.value!!.totalVolume}")
                    Spacer(Modifier.height(8.dp))
                    Text(finishResult.value!!.funFact)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    finishResult.value = null
                    onNavigate(UiEvent.Navigate(Routes.HOME, popBackStack = true))
                }) {
                    Text("OK")
                }
            }
        )
    }
}
