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
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
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
                            model = "file:///android_asset/images/$imageName",
                            contentDescription = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    itemsIndexed(exercise.instructions) { index, instruction ->
                        Text(
                            "${index + 1}. $instruction",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
            id = "3_4_Sit-Up",
            name = "3/4 Sit-Up",
            primaryMuscles = listOf("Abdominals"),
            secondaryMuscles = emptyList(),
            images = listOf("3_4_Sit-Up/0.jpg", "3_4_Sit-Up/1.jpg"),
            instructions = listOf(
                "Lie down on the floor and secure your feet. Your legs should be bent at the knees.",
                "Place your hands behind or to the side of your head.",
                "Flex your hips and spine to raise your torso toward your knees."
            ),
            force = "pull",
            level = "beginner",
            mechanic = "compound",
            equipment = "body only",
            category = "strength"
        ),
        onDismiss = {}
    )
}
