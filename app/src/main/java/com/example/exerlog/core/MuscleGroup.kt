package com.example.exerlog.core

enum class MuscleGroup(val label: String) {
    CORE("Core"), HIPS("Hips"), CALVES("Calves"), GLUTES("Glutes"), HAMSTRINGS("Hamstrings"), QUADRICEPS(
        "Quadriceps"
    ),
    BACK("Back"), NECK("Neck"), BICEPS("Biceps"), FOREARMS("Forearms and Wrists"), TRICEPS("Triceps"), CHEST(
        "Chest"
    ),
    SHOULDERS("Shoulders");

    companion object {
        // Si necesitas obtener MuscleGroup desde un label:
        fun fromLabel(label: String): MuscleGroup? {
            return entries.find { it.label.equals(label, ignoreCase = true) }
        }

        // Si necesitas una lista de solo los labels:
        fun getAllMuscleGroups(): List<String> = entries.map { it.label }
    }
}

