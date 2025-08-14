package com.example.exerlog.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exerlog.ui.home.components.HomeBottomBar
import com.example.exerlog.ui.home.components.SessionCard
import com.example.exerlog.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsState()
    val sessionToDelete = viewModel.sessionToDelete

    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.sessionToDelete = null },
            title = { Text("Eliminar sesión") },
            text = { Text("¿Estás seguro de que quieres eliminar esta sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(
                        HomeEvent.ConfirmDeleteSession(
                            sessionToDelete.session.sessionId
                        )
                    )
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.sessionToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            if (event is UiEvent.Navigate) onNavigate(event)
        }
    }
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        bottomBar = { HomeBottomBar(onEvent = viewModel::onEvent) }
    ) { _ -> // aquí ignoramos paddingValues
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding), // solo padding superior
            contentPadding = PaddingValues(bottom = 0.dp) // ignorar padding inferior
        ) {
            items(sessions, key = { it.session.sessionId }) { sessionWrapper ->
                SessionCard(
                    sessionWrapper = sessionWrapper,
                    onClick = { viewModel.onEvent(HomeEvent.SessionClicked(sessionWrapper)) },
                    onLongClick = { viewModel.onEvent(HomeEvent.DeleteSessionRequested(sessionWrapper)) }
                )
            }
        // Spacer para que el último elemento quede scrollable sobre el BottomAppBar
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Ajusta altura según tu BottomBar
        }
        }
    }
}
