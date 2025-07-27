package com.example.exerlog.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exerlog.ui.SessionWrapper
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
                        }
                    )
                }
            }
        }
    }
}
