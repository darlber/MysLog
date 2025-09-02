package com.example.exerlog.ui.session.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.ui.TimerState
import com.example.exerlog.ui.session.SessionEvent
import com.example.exerlog.ui.session.actions.TimerAction
import com.example.exerlog.utils.Event

@Composable
fun SessionBottomBar(
    onDeleteSession: () -> Unit,
    onFinishSession: () -> Unit,
    timerState: TimerState,
    timerVisible: Boolean,
    onTimerPress: () -> Unit,
    onFAB: () -> Unit,
    onEvent: (Event) -> Unit
) {
    Column {
        // Barra de temporizador con animación
        AnimatedVisibility(
            visible = timerVisible,
            enter = slideInVertically(
                animationSpec = tween(250),
                initialOffsetY = { it / 2 }
            ),
            exit = slideOutVertically(
                animationSpec = tween(250),
                targetOffsetY = { it / 2 }
            )
        ) {
            TimerBar(
                timerState = timerState,
                onEvent = onEvent
            )
        }

        BottomAppBar(
            containerColor = Color.Transparent,
            actions = {
                Row {
                    // Botón de eliminar sesión
                    FloatingActionButton(
                        onClick = onDeleteSession,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete Session")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón de terminar sesión (más ancho)
                    FloatingActionButton(
                        onClick = onFinishSession,
                        modifier = Modifier
                            .width(80.dp)
                            .height(48.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text("Finish")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón de temporizador
// Botón de temporizador
                    FloatingActionButton(
                        onClick = {
                            onTimerPress() // alterna visibilidad

                        },
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        TimerAction(
                            onClick = {
                                onTimerPress()

                            },
                            timerState = timerState,
                            timerVisible = timerVisible
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onFAB,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Exercise"
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun SessionBottomBarPreview() {
    SessionBottomBar(
        onDeleteSession = {},
        onFinishSession = {},
        timerState = TimerState(running = false, time = 0L, maxTime = 0L),
        timerVisible = true,
        onTimerPress = {},
        onFAB = {},
        onEvent = {}
    )
}
