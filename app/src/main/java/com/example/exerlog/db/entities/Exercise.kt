package com.example.exerlog.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.exerlog.core.Entities.EXERCISE

@Entity(tableName = EXERCISE)
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val force: String?,
    val level: String?,
    val mechanic: String?,
    val equipment: String?,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val category: String?,
    val images: List<String>

)

//    fun getMuscleGroups(exercise: Exercise = this): List<String> {
//        return exercise.targets.flatMap {
//            turnTargetIntoMuscleGroups(it)
//        }.distinct()
//    }
//
//    fun getStringMatch(string: String): Boolean {
//        return FuzzySearch.regexMatch(string, title)
//    }
