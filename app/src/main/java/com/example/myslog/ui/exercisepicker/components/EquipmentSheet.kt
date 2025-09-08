package com.example.myslog.ui.exercisepicker.components

import androidx.compose.runtime.Composable

import com.example.myslog.ui.exercisepicker.ExerciseEvent
import com.example.myslog.utils.Event

@Composable
fun EquipmentSheet(
  selectedEquipment: List<String>,
  allEquipment: List<String>,
  onEvent: (Event) -> Unit
) {
  Sheet(
    items = allEquipment.sorted(),
    selectedItems = selectedEquipment,
    title = "Filter by Equipment",
    onSelect = { onEvent(ExerciseEvent.SelectEquipment(it)) }
  ) {
    onEvent(ExerciseEvent.DeselectEquipment)
  }
}