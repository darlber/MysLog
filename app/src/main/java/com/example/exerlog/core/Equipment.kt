package com.example.exerlog.core

enum class Equipment(val displayName: String) {
    ATLAS_STONE("Atlas Stone"), BARBELL("Barbell"), BODYWEIGHT("Bodyweight"), BROOM_STICK("Broom Stick"), CABLE(
        "Cable"
    ),
    DUMBBELLS("Dumbbell"), KETTLEBELL("Kettlebell"), LEVER("Lever"), MACHINE("Machine"), MEDICINE_BALL(
        "Medicine Ball"
    ),
    RESISTANCE_BAND("Resistance Band"), ROPE("Rope"), ROPE_MACHINE("Rope Machine"), SLED("Sled"), SMITH_MACHINE(
        "Smith Machine"
    ),
    STABILITY_BALL("Stability Ball"), SUSPENSION("Suspension"), WEIGHT("Weight");

    companion object {
        fun getAllEquipment(): List<String> = entries.map { it.displayName }

        fun fromDisplayName(name: String): Equipment? =
            entries.find { it.displayName.equals(name, ignoreCase = true) }
    }
}