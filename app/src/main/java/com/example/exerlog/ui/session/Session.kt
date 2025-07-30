package com.example.exerlog.ui.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.session.components.HeaderSession
import com.example.exerlog.utils.UiEvent
import java.time.LocalDateTime

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
    val headerHeight = 240.dp
    val coroutineScope = rememberCoroutineScope()
    val timerVisible = remember { mutableStateOf(false) }

    Surface( // <- añade fondo
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {

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
                    scrollState = scrollState,
                    height = headerHeight,
                    topPadding = paddingValues.calculateTopPadding(),
                    onEndTime = { },
                    onStartTime = { }
                )
            }
        }
    }
}

private fun Unit.show() {
    TODO("Not yet implemented")
}

fun rememberMaterialDialogState() {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun PreviewSessionScreenMock() {
    val mockSession = SessionWrapper(
        session = Session(
            start = LocalDateTime.of(2022, 1, 1, 10, 0),
            end = LocalDateTime.of(2022, 1, 1, 11, 0)
        ),
        muscleGroups = listOf("Chest", "Shoulders", "Triceps")
    )

    val mockScrollState = rememberLazyListState()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    HeaderSession(
                        sessionWrapper = mockSession,
                        muscleGroups = mockSession.muscleGroups,
                        scrollState = mockScrollState,
                        height = 240.dp,
                        topPadding = 16.dp, // mock padding
                        onEndTime = {},
                        onStartTime = {}
                    )
                }
            }
        }
    }
}
