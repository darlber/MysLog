package com.example.myslog.ui.exercisepicker.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myslog.core.Constants.Companion.BASE_IMAGE_URL
import com.example.myslog.db.entities.Exercise
import com.example.myslog.ui.session.components.SmallPill
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ImagePopup(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    exercise.primaryMuscles.forEach { muscle ->
                        SmallPill(
                            text = muscle,
                            backgroundColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(2.dp)
                        )
                    }

                    exercise.secondaryMuscles.forEach { muscle ->
                        SmallPill(
                            text = muscle,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    exercise.images.forEach { imageName ->
                        AsyncImage(
                            model = BASE_IMAGE_URL + imageName,
                            contentDescription = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    exercise.instructions.forEachIndexed { index, instruction ->
                        Text(
                            text = "${index + 1}. $instruction",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ImagePopupPreview() {
    ImagePopup(
        exercise = Exercise(
            id = "asda",
            name = "Push Up",
            primaryMuscles = listOf("Chest"),
            secondaryMuscles = listOf("Triceps", "Shoulders"),
            images = listOf("push_up_1.jpg", "push_up_2.jpg"),
            instructions = listOf(
                "Start in a plank position with your hands slightly wider than shoulder-width apart.",
                "Lower your body until your chest nearly touches the floor.",
                "Push back up to the starting position."
            ),
            force = "TODO()",
            level = "TODO()",
            mechanic = "TODO()",
            equipment = "TODO()",
            category = "TODO()"
        ),
        onDismiss = {}
    )
}
