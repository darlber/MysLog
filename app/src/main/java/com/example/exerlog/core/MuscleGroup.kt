package com.example.exerlog.core

import android.content.Context
import androidx.annotation.StringRes
import com.example.exerlog.R

enum class MuscleGroup(@param:StringRes val label: Int) {
    CORE(R.string.muscle_core),
    HIPS(R.string.muscle_hips),
    CALVES(R.string.muscle_calves),
    GLUTES(R.string.muscle_glutes),
    HAMSTRINGS(R.string.muscle_hamstrings),
    QUADRICEPS(R.string.muscle_quadriceps),
    BACK(R.string.muscle_back),
    NECK(R.string.muscle_neck),
    BICEPS(R.string.muscle_biceps),
    FOREARMS(R.string.muscle_forearms),
    TRICEPS(R.string.muscle_triceps),
    CHEST(R.string.muscle_chest),
    SHOULDERS(R.string.muscle_shoulders);


    companion object {
        // Si necesitas obtener MuscleGroup desde un label:
        fun fromLabel(context: Context, label: String): MuscleGroup? {
            return entries.find {
                context.getString(it.label).equals(label, ignoreCase = true)
            }
        }

        // Si necesitas una lista de solo los labels:
        fun getAllMuscleGroups(context: Context): List<String> =
            entries.map { context.getString(it.label) }
    }
}

