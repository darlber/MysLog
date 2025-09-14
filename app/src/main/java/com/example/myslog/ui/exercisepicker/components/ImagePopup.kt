package com.example.myslog.ui.exercisepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myslog.core.Constants.Companion.BASE_IMAGE_URL
import com.example.myslog.db.entities.Exercise
import com.example.myslog.ui.session.components.SmallPill

@Composable
fun ImagePopup(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(text = exercise.name, style = MaterialTheme.typography.titleLarge) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()   // ocupa casi todo el ancho
                    .fillMaxHeight(0.7f) // ocupa 70% de la altura
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            exercise.primaryMuscles.forEach { muscle ->
                                SmallPill(
                                    text = muscle,
                                    backgroundColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            }
                            exercise.secondaryMuscles.forEach { muscle ->
                                SmallPill(
                                    text = muscle,
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }

                    items(exercise.images) { imageName ->
                        AsyncImage(
                            model = BASE_IMAGE_URL + imageName,
                            contentDescription = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    itemsIndexed(exercise.instructions) { index, instruction ->
                        Text("${index + 1}. $instruction", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun ImagePopupPreview() {
    ImagePopup(
        exercise = Exercise(
            id = "asda",
            name = "Pushs  Up",
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
