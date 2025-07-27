package com.example.exerlog.utils


import com.example.exerlog.db.entities.Exercise

fun List<Exercise>.sortedListOfMuscleGroups(): List<String> {
    return this.flatMap { it.primaryMuscles + it.secondaryMuscles }
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .map { it.first }
}
