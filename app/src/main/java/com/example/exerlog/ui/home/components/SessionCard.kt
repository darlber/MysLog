package com.example.exerlog.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper

import java.time.LocalDateTime

@Composable
fun SessionCard(
    sessionWrapper: SessionWrapper,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        // Estilo de la tarjeta
        shape = MaterialTheme.shapes.small

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fecha a la izquierda
            SessionDate(
                session = sessionWrapper.session,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Info principal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Session #${sessionWrapper.session.sessionId}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = sessionWrapper.muscleGroups.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionCardPreview() {
    val dummySession = Session(
        sessionId = 1,
        start = LocalDateTime.now(),
        end = null
    )
    val dummyWrapper = SessionWrapper(
        session = dummySession,
        muscleGroups = listOf("CHEST", "BACK", "ARMS")
    )

    SessionCard(sessionWrapper = dummyWrapper, onClick = {})
}
