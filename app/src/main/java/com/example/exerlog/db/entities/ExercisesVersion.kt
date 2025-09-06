package com.example.exerlog.db.entities

data class ExercisesVersion(
    val version: Double,
    val exercises: List<Exercise>
)
