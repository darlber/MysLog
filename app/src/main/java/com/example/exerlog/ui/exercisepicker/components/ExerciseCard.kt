package com.example.exerlog.ui.exercisepicker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    // cambiado porque dice chatgpt que es mejor
    // onEvent: (Event) -> Unit
    onEvent: (ExerciseEvent) -> Unit,
    onClick: () -> Unit
) {

    val targets = exercise.primaryMuscles
    val equipment = exercise.equipment
    val tonalElevation by animateDpAsState(targetValue = if (selected) 2.dp else 0.dp)
    val indicatorColor by
    animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
    )
    val localDensity = LocalDensity.current
    var rowHeightDp by remember { mutableStateOf(0.dp) }

    val indicatorHeight by
    animateDpAsState(targetValue = if (selected) rowHeightDp else 0.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .onGloballyPositioned { coordinates ->
                // Set column height using the LayoutCoordinates
                rowHeightDp = with(localDensity) {
                    coordinates.size.height
                        .minus(95)
                        .toDp()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = indicatorColor,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .width(3.dp)
                .height(indicatorHeight)
        ) {}
        Spacer(modifier = Modifier.width(4.dp))
        Surface(
            onClick = onClick,
            color = backgroundColor,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp),
            tonalElevation = tonalElevation,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 14.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = exercise.name,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(0.65f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        targets.forEach { target ->
                            SmallPill(text = target, modifier = Modifier.padding(end = 4.dp))
                        }
                        equipment
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.forEach { eq ->
                                SmallPill(text = eq)
                            }

                    }
                }
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OpenStatsAction {}
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