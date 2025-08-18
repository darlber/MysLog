package com.example.exerlog.utils

import androidx.room.TypeConverter
import java.time.LocalDateTime

class Converters {

    // --- List<String> <-> String ---
    @TypeConverter
    fun fromString(source: String?): List<String> {
        return source?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
    }

    @TypeConverter
    fun fromList(source: List<String>?): String {
        return source?.joinToString("|") ?: ""
    }

    // --- LocalDateTime <-> String ---
    @TypeConverter
    fun fromDateTime(source: LocalDateTime?): String {
        return source?.toString() ?: ""
    }

    @TypeConverter
    fun toDateTime(source: String?): LocalDateTime? {
        return try {
            if (source.isNullOrBlank()) null else LocalDateTime.parse(source)
        } catch (e: Exception) {
            null
        }
    }
}
