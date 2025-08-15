package com.example.exerlog.ui.exercisepicker.components

import androidx.compose.runtime.Composable

import com.example.exerlog.ui.exercisepicker.ExerciseEvent
import com.example.exerlog.utils.Event


//@Composable
//fun MuscleSheet(
//  selectedMusclegroups: List<String>,
//  onEvent: (Event) -> Unit
//) {
//  Sheet(
//    items = MuscleGroup.getAllMuscleGroups().sorted(),
//    selectedItems = selectedMusclegroups,
//    title = "Filter by Body-part",
//    onSelect = { onEvent(ExerciseEvent.SelectMuscle(it)) }
//  ) {
//    onEvent(ExerciseEvent.DeselectMuscles)
//  }
//}