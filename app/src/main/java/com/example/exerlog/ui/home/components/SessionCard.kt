package com.example.exerlog.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.home.HomeEvent

import java.time.LocalDateTime

@Composable
fun SessionCard(
    sessionWrapper: SessionWrapper,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val muscleTitle by remember {
        derivedStateOf {
            sessionWrapper.muscleGroups.firstOrNull()?: "Session #${sessionWrapper.session.sessionId}"
        }
    }

    val muscleSubtitle by remember {
        derivedStateOf {
            sessionWrapper.muscleGroups.drop(1)
                .take(3)
                .joinToString(", ")
        }
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
                // Estilo de la tarjeta
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),


    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fecha a la izquierda
            SessionDate(
                session = sessionWrapper.session,
                modifier = Modifier.padding(end = 16.dp).align(Alignment.CenterVertically)
            )
            // Info principal
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = muscleTitle,
                    style = MaterialTheme.typography.headlineSmall
                )
                if (muscleSubtitle.isNotEmpty()) {
                    Text(
                        text = muscleSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
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
    val muscles = listOf("Pecho", "Espalda", "Biceps", "Tríceps","Tríceps","Hombros", "Hombros", "Hombros")

    val sortedMuscles = muscles
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .map { it.first }

    val dummyWrapper = SessionWrapper(
        session = dummySession,
        muscleGroups = sortedMuscles
    )

    SessionCard(
        sessionWrapper = dummyWrapper,
        onClick = {},
        onLongClick = {}
    )
}
