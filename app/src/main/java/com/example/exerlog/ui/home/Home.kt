package com.example.exerlog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.ui.home.components.HomeBottomBar
import com.example.exerlog.ui.home.components.SessionCard
import java.time.LocalDateTime

// Dato dummy para Session
data class Session(
    val sessionId: Int,
    val start: LocalDateTime
)

// SessionWrapper dummy
val sampleSessions = listOf(
    SessionWrapper(
        session = Session(1, LocalDateTime.now()),
        muscleGroups = listOf("CHEST", "BACK", "ARMS")
    ),
    SessionWrapper(
        session = Session(2, LocalDateTime.now().minusDays(1)),
        muscleGroups = listOf("LEGS", "SHOULDERS")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessions: List<SessionWrapper> = sampleSessions,
    onSessionClick: (SessionWrapper) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
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
                    SessionCard(sessionWrapper = sessionWrapper, onClick = {
                        onSessionClick(sessionWrapper)
                    })
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    HomeBottomBar(
                        onAddClick = { /* Acción + */ },
                        onSettingsClick = { /* Acción Settings */ },
                        onOptionsClick = { /* Acción Options */ }
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
