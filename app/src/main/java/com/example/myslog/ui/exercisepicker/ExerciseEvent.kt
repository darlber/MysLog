package com.example.myslog.ui.exercisepicker

import com.example.myslog.db.entities.Exercise
import com.example.myslog.utils.Event

sealed class ExerciseEvent: Event {
    data class ExerciseSelected(val exercise: Exercise) : ExerciseEvent()
    object FilterSelected : ExerciseEvent()
    object FilterUsed : ExerciseEvent()
    data class SelectMuscle(val muscle: String) : ExerciseEvent()
    object DeselectMuscles : ExerciseEvent()
    data class SelectEquipment(val equipment: String) : ExerciseEvent()
    object DeselectEquipment : ExerciseEvent()
    object AddExercises : ExerciseEvent()

    data class SearchChanged(val text: String) : ExerciseEvent()

    data class OpenGuide(val exercise: Exercise) : ExerciseEvent()
    data class OpenStats(val exercise: Exercise) : ExerciseEvent()

    data class SaveWorkout(val workoutName: String) : ExerciseEvent()
    data class SelectWorkout(val workoutId: Long) : ExerciseEvent()
    object DeselectWorkouts : ExerciseEvent()
    data class AddWorkout(val workoutName: String) : ExerciseEvent()

}
