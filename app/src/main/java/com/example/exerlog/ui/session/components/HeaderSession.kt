package com.example.exerlog.ui.session.components

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Session
import com.example.exerlog.ui.SessionWrapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun SmallPillPreview(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeaderSession(
    sessionWrapper: SessionWrapper,
    muscleGroups: List<String>,
    scrollState: LazyListState,
    topPadding: Dp,
    onEndTime: () -> Unit,
    onStartTime: () -> Unit
) {
    val session = sessionWrapper.session
    val startTime = DateTimeFormatter.ofPattern("HH:mm").format(session.start)
    val endTime = session.end?.let { DateTimeFormatter.ofPattern("HH:mm").format(it) } ?: "ongoing"

    Box(
        modifier = Modifier
            .padding(
                start = 12.dp,
                top = topPadding,
                end = 12.dp
            )
            .wrapContentHeight()
            .fillMaxWidth(),
        // TODO: Uncomment for parallax effect
//                .graphicsLayer {
//                    val scroll = if(scrollState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0) {
//                        scrollState.firstVisibleItemScrollOffset.toFloat()
//                    } else {
//                        10000f
//                    }
//                    translationY = scroll / 3f // Parallax effect
//                    alpha = 1 - scroll / 250f // Fade out text
//                    scaleX = 1 - scroll / 3000f
//                    scaleY = 1 - scroll / 3000f
//                }

    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Column {
                Text(
                    text = session.toSessionTitle(),
                    style = MaterialTheme.typography.headlineSmall
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(1.dp)
                        .fillMaxWidth(0.5f) // puedes ajustar el ancho (30% del total)
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
                                onStartTime()
                            }
                    )
                    Text(
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
                                onEndTime()
                            }
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.Center
                ) {
                    muscleGroups.forEach { muscle ->
                        SmallPillPreview(
                            text = muscle,
                            modifier = Modifier.padding(4.dp)
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
fun PreviewSessionHeader() {
    val scrollState = rememberLazyListState()

    val session = Session(
        start = LocalDateTime.of(2022, 1, 1, 10, 0),
        end = LocalDateTime.of(2022, 1, 1, 11, 0)
    )

    val muscleGroups = listOf("Biceps", "Triceps", "Back")

    val sessionWrapper = SessionWrapper(
        session = session,
        muscleGroups
    )

    HeaderSession(
        sessionWrapper = sessionWrapper,
        muscleGroups = muscleGroups,
        scrollState = scrollState,
        topPadding = 16.dp,
        onEndTime = {},
        onStartTime = {}
    )
}