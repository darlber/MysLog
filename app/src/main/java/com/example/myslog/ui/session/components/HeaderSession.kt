package com.example.myslog.ui.session.components

// Imports necesarios para LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myslog.db.entities.Session
import com.example.myslog.ui.SessionWrapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun SmallPillPreview(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeaderSession(
    sessionWrapper: SessionWrapper,
    muscleGroups: List<String>,
    scrollState: LazyListState,
    topPadding: Dp,
    onEndTime: (LocalDateTime) -> Unit,
    onStartTime: (LocalDateTime) -> Unit
) {
    val session = sessionWrapper.session
    val startTime = DateTimeFormatter.ofPattern("HH:mm").format(session.start)
    val endTime = session.end?.let { DateTimeFormatter.ofPattern("HH:mm").format(it) } ?: "ongoing"

    Box(
        modifier = Modifier
            .padding(
                start = 12.dp, top = topPadding, end = 12.dp
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .graphicsLayer {
                val scroll =
                    if (scrollState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0) {
                        scrollState.firstVisibleItemScrollOffset.toFloat()
                    } else {
                        // Si no es el primer item o no hay info (como en un preview sin LazyColumn),
                        // y el offset es 0, no deberíamos usar 10000f.
                        // Para el preview, si no está en una lista, el offset será 0.
                        // Si el scrollState no tiene items, su offset será 0.
                        // Esta lógica es la que causa el problema en el preview.
                        // No obstante, la solución del LazyColumn en el Preview lo arregla.
                        10000f
                    }
                translationY = scroll / 3f // Parallax effect
                alpha = 1 - scroll / 250f // Fade out text
                scaleX = 1 - scroll / 3000f
                scaleY = 1 - scroll / 3000f
            }

    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Column {
                Text(
                    text = session.toSessionTitle(), style = MaterialTheme.typography.headlineMedium
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(1.dp)
                        // ajustar el ancho de la linea
                        .fillMaxWidth(0.66f)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = startTime,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                                onStartTime(session.start)
                        }
                    )
                                Text (
                                text = "-",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = endTime,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                            session.end?.let { onEndTime(it) }
                        }
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.Center
                ) {
                    muscleGroups.forEach { muscle ->
                        SmallPillPreview(
                            text = muscle, modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun Session.toSessionTitle(): String {
    return try {
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy").format(this.start)
    } catch (e: Exception) {
        "no date"
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHeaderSession() {
    val scrollState = rememberLazyListState()

    val session = Session(
        start = LocalDateTime.of(2023, 8, 4, 10, 30), end = LocalDateTime.of(2023, 8, 4, 12, 0)
    )
    val muscleGroups = listOf("Biceps", "Triceps", "Back")

    val sessionWrapper = SessionWrapper(
        session = session, muscleGroups = muscleGroups
    )

    // Envuelve HeaderSession en un LazyColumn
    LazyColumn(state = scrollState) {
        item {
            HeaderSession(
                sessionWrapper = sessionWrapper,
                muscleGroups = muscleGroups,
                scrollState = scrollState, // Pasa el mismo scrollState
                topPadding = 16.dp,
                onEndTime = {},
                onStartTime = {}
            )
        }
        // Puedes añadir items de relleno para probar el scroll si quieres
        // items(20) { index ->
        //     Text("Item de relleno $index", modifier = Modifier.padding(16.dp).fillMaxWidth())
        // }
    }
}
