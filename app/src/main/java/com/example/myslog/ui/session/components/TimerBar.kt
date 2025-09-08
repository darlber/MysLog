package com.example.myslog.ui.session.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myslog.ui.TimerState
import com.example.myslog.ui.session.SessionEvent
import com.example.myslog.utils.Event

// Función de extensión para formatear tiempo en mm:ss
fun Long.toTimerString(): String {
    val totalSeconds = (this / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TimerBar(
    timerState: TimerState,
    onEvent: (Event) -> Unit
) {
    val timerToggleIcon = if (timerState.running) Icons.Default.Pause else Icons.Default.PlayArrow
    val timerTimeText =
        if (timerState.time > 0L) timerState.time.toTimerString() else timerState.maxTime.toTimerString()

    val timerRunning = timerState.running
    val timerTonalElevation by animateDpAsState(targetValue = if (timerRunning) 140.dp else 14.dp)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        val timerWidth = maxWidth * (timerState.time.toFloat() / timerState.maxTime)

        Surface(
            modifier = Modifier.fillMaxSize(),
            tonalElevation = 8.dp
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .width(timerWidth)
                        .height(50.dp),
                    tonalElevation = timerTonalElevation
                ) {}
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onEvent(SessionEvent.TimerDecreased) }) {
                            Icon(Icons.Default.Remove, "Decrease time")
                        }
                        Text(
                            text = timerTimeText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(50.dp)
                        )
                        IconButton(onClick = { onEvent(SessionEvent.TimerIncreased) }) {
                            Icon(Icons.Default.Add, "Increase time")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onEvent(SessionEvent.TimerReset) }) {
                            Icon(Icons.Default.Refresh, "Reset Timer")
                        }
                        IconButton(onClick = { onEvent(SessionEvent.TimerToggled) }) {
                            Icon(timerToggleIcon, "Toggle Timer")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TimerBarPreview() {
    TimerBar(
        timerState = TimerState(
            time = 60000L,
            maxTime = 120000L,
            running = false
        ),
        onEvent = {}
    )
}
