package com.example.exerlog.ui.session.actions

import com.example.exerlog.ui.ExerciseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL

data class ExerciseVolume(val exerciseName: String, val volume: Int)
data class FinishResult(
    val exerciseVolumes: List<ExerciseVolume>,
    val totalVolume: Int,
    val funFact: String
)

class OpenInFinish {

    suspend fun calculateAndFetchFact(exercises: List<ExerciseWrapper>): FinishResult {
        val volumes = exercises.map { ex ->
            val vol = ex.sets.sumOf { (it.reps ?: 0) * (it.weight ?: 0f).toInt() }
            ExerciseVolume(ex.exercise.name, vol)
        }
        val total = volumes.sumOf { it.volume }

        // Fetch fact from numbersapi
        val fact = withContext(Dispatchers.IO) {
            try {
                Timber.d("Fetching fact from URL: http://numbersapi.com/$total?notfound=floor")
                URL("http://numbersapi.com/$total?notfound=floor").readText()
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Error fetching fact: ${e.message}")
                "No fact available"
            }
        }

        return FinishResult(volumes, total, fact)
    }
}
