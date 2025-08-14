package com.example.exerlog.ui.session.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.example.exerlog.ui.session.actions.TimerAction

@Composable
fun SessionBottomBar(
    onDeleteSession: () -> Unit,
    onFinishSession: () -> Unit,
    timerState: TimerState,
    timerVisible: Boolean,
    onTimerPress: () -> Unit,
    onFAB: () -> Unit
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        actions = {
            Row {
                // Botón de eliminar sesión
                FloatingActionButton(
                    onClick = { onDeleteSession() },
                    modifier = Modifier.width(48.dp).height(48.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete Session")
                }

                Spacer(modifier = Modifier.width(12.dp))
                // Botón de terminar sesión (más ancho)
                FloatingActionButton(
                    onClick = { onFinishSession() },
                    modifier = Modifier.width(80.dp).height(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text("Finish") // o usa un icono si quieres
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Botón de temporizador
                FloatingActionButton(
                    onClick = { onTimerPress() },
                    modifier = Modifier.width(48.dp).height(48.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    TimerAction(onClick = onTimerPress, timerState = timerState, timerVisible = timerVisible)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onFAB() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Exercise")
            }
        }
    )
}

@Composable
@Preview
fun SessionBottomBarPreview() {
    SessionBottomBar(
        onDeleteSession = {},
        onFinishSession = {},
        timerState = TimerState(running = false, time = 0L, maxTime = 0L),
        timerVisible = true,
        onTimerPress = {},
        onFAB = {}
    )
}
