package com.example.exerlog.db.entities

data class ExercisesVersion(
    val version: Int,
    val exercises: List<Exercise>
)
