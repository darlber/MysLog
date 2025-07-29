package com.example.exerlog.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

    //TODO: localizacion de textos
    if (sessionToDelete != null) {
        //TODO AlertDialog en otra clase
        AlertDialog(
            onDismissRequest = { viewModel.sessionToDelete = null },
            title = { Text("Eliminar sesión") },
            text = { Text("¿Estás seguro de que quieres eliminar esta sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(HomeEvent.ConfirmDeleteSession(sessionToDelete.session.sessionId))
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.sessionToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }


    // Manejo de eventos de navegación
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                onAddClick = { viewModel.onEvent(HomeEvent.NewSession) },
                onSettingsClick = { viewModel.onEvent(HomeEvent.OpenSettings) },
                onOptionsClick = { /* Podés manejar opciones aquí */ }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(sessions, key = { it.session.sessionId }) { sessionWrapper ->
                    SessionCard(
                        sessionWrapper = sessionWrapper,
                        onClick = {
                            viewModel.onEvent(HomeEvent.SessionClicked(sessionWrapper))
                        },
                        onLongClick = {
                            viewModel.onEvent(HomeEvent.DeleteSessionRequested(sessionWrapper))
                        }
                    )
                }
            }
        }
    }
}
