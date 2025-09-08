package com.example.myslog.ui.exercisepicker.components

import androidx.compose.runtime.Composable

import com.example.myslog.ui.exercisepicker.ExerciseEvent
import com.example.myslog.utils.Event

@Composable
fun MuscleSheet(
  selectedMusclegroups: List<String>,
  allMuscleGroups: List<String>,
  onEvent: (Event) -> Unit
) {
  Sheet(
    items = allMuscleGroups.sorted(),
    selectedItems = selectedMusclegroups,
    title = "Filter by Body-part",
    onSelect = { onEvent(ExerciseEvent.SelectMuscle(it)) }
  ) {
    onEvent(ExerciseEvent.DeselectMuscles)
  }
}