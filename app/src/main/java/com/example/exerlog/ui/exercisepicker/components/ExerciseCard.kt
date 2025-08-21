package com.example.exerlog.ui.exercisepicker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.ui.exercisepicker.ExerciseEvent
import com.example.exerlog.ui.session.actions.OpenInNewAction
import com.example.exerlog.ui.session.actions.OpenStatsAction
import com.example.exerlog.ui.session.components.SmallPill
import com.example.exerlog.utils.Event
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    exercise: Exercise,
    selected: Boolean,
    onEvent: (ExerciseEvent) -> Unit,
    onClick: () -> Unit
) {
    val targets = exercise.primaryMuscles
    val equipment = exercise.equipment

    // Animación de elevación
    val tonalElevation by animateDpAsState(targetValue = if (selected) 2.dp else 0.dp)

    // Animación de color
    val containerColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerLow
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador lateral
        Surface(
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
        ) {}

        Spacer(modifier = Modifier.width(4.dp))

        // Card principal
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = tonalElevation),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.dp, top = 8.dp)
                ) {
                    Text(
                        text = exercise.name,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(0.65f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(modifier = Modifier.padding(bottom = 4.dp)) {
                        targets.forEach { target ->
                            SmallPill(text = target, modifier = Modifier.padding(end = 4.dp))
                        }
                        equipment?.split(",")?.map { it.trim() }?.forEach { eq ->
                            SmallPill(text = eq)
                        }
                    }
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OpenStatsAction { onEvent(ExerciseEvent.OpenStats(exercise)) }
                    OpenInNewAction { onEvent(ExerciseEvent.OpenGuide(exercise)) }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExerciseCardPreview() {
    val mockExercise = Exercise(
        id = "1",
        name = "Pene al fallo",
        force = "TODO()",
        level = "TODO()",
        mechanic = "TODO()",
        equipment = "Barbell",
        primaryMuscles = listOf("Chest", "Triceps"),
        secondaryMuscles = listOf("TODO()"),
        instructions = listOf("TODO()"),
        category = "TODO()",
        images = listOf("TODO()")
    )

    MaterialTheme {
        ExerciseCard(
            exercise = mockExercise,
            selected = true,
            onEvent = {},
            onClick = {}
        )
    }
}