package com.example.myslog.utils


import com.example.myslog.db.entities.Exercise

fun List<Exercise>.sortedListOfMuscleGroups(): List<String> {
    return this.flatMap { it.primaryMuscles }
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .map { it.first }
}
//fun List<Exercise>.sortedListOfMuscleGroups(): List<String> {
//    return this.flatMap { it.primaryMuscles + it.secondaryMuscles }
//        .groupingBy { it }
//        .eachCount()
//        .toList()
//        .sortedByDescending { it.second }
//        .map { it.first }
//}
