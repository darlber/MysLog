package com.example.exerlog.ui.exercisepicker.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.exerlog.db.entities.Exercise

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import com.example.exerlog.ui.session.components.SmallPill

//TODO Related exercises
@Composable
fun ImagePopup(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Box(
        Modifier
            .fillMaxSize()
            //dim background to dark with some transparency
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

                // Título del ejercicio
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    // Primarios: color primario del tema
                    exercise.primaryMuscles.forEach { muscle ->
                        SmallPill(
                            text = muscle,
                            backgroundColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(2.dp)
                        )
                    }

                    // Secundarios: color secundario del tema
                    exercise.secondaryMuscles.forEach { muscle ->
                        SmallPill(
                            text = muscle,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }


                // Imágenes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    exercise.images.forEach { imageAssetName ->
                        val path = "file:///android_asset/exercises/$imageAssetName"
                        val painter = rememberAsyncImagePainter(path)
                        Image(
                            painter = painter,
                            contentDescription = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Instrucciones
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