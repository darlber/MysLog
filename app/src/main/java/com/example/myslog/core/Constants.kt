package com.example.myslog.core

class Constants {
    companion object {
        const val DATABASE_NAME = "mys_db"
        const val APP_VERSION = "1.0.0"
        const val APP_AUTHOR = "Darlber"
        const val BASE_IMAGE_URL = "https://raw.githubusercontent.com/darlber/free-exercise-db/main/exercises/"
        const val BASE_JSON_URL = "https://raw.githubusercontent.com/darlber/free-exercise-db/refs/heads/main/dist/"
        const val CACHE_FILENAME = "exercises_cache.json"
        const val VERSION_KEY = "exercises_version"

        fun getJsonUrlForLanguage(lang: String): String {
            val fileName = when (lang) {
                "es" -> "exercises_es.json"
                "en" -> "exercises_en.json"
                else -> "exercises_en.json" // fallback
            }
            return BASE_JSON_URL + fileName
        }
    }
}