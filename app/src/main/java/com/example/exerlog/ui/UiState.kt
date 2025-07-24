package com.example.exerlog.ui

//este archivo contiene las clases de estado de la UI que se utilizan en la aplicación ExerLog.
//una clase de estado de la UI es una representación de los datos que se muestran en la interfaz de usuario.
import com.example.exerlog.db.entities.Exercise
import com.example.exerlog.db.entities.GymSet
import com.example.exerlog.db.entities.Session
import com.example.exerlog.db.entities.SessionExercise


data class SessionWrapper(
  val session: Session,
  val muscleGroups: List<String>
)

data class ExerciseWrapper(
  val sessionExercise: SessionExercise,
  val exercise: Exercise,
  val sets: List<GymSet>
)

data class TimerState(
  val time: Long,
  val running: Boolean,
  val maxTime: Long
)

data class DatabaseModel(
  val sessions: List<Session>,
  val exercises: List<Exercise>,
  val sessionExercises: List<SessionExercise>,
  val sets: List<GymSet>
)