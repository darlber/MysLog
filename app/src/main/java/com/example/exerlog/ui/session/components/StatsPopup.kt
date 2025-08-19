package com.example.exerlog.ui.session.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.exerlog.db.entities.GymSet
import java.time.LocalDate

data class StatEntry(val date: LocalDate, val pesoMax: Float)

@Composable
fun StatsPopup(
    sets: List<GymSet>,           // recibir GymSet reales
    onDismiss: () -> Unit
) {
    // Agrupar sets por fecha y calcular el peso máximo de cada fecha
    val stats = sets.groupBy { it.parentSessionExerciseId } // o por fecha si tienes timestamp
        .map { entry ->
            val maxPeso = entry.value.maxOfOrNull { it.weight ?: 0f } ?: 0f
            StatEntry(LocalDate.now(), maxPeso) // TODO: cambiar LocalDate.now() por fecha real si la tienes
        }.sortedBy { it.date }

    val lineColor = MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Estadísticas") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (stats.isEmpty()) {
                    Text("No hay datos")
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxPeso = stats.maxOf { it.pesoMax }
                        val minPeso = stats.minOf { it.pesoMax }
                        val padding = 32f
                        val graphWidth = size.width - padding * 2
                        val graphHeight = size.height - padding * 2

                        // Mapeo de puntos escalados al canvas
                        val points = stats.mapIndexed { index, stat ->
                            val x = padding + index * (graphWidth / (stats.size - 1).coerceAtLeast(1))
                            val y = padding + (1 - (stat.pesoMax - minPeso) / ((maxPeso - minPeso).coerceAtLeast(1f))) * graphHeight
                            Offset(x, y)
                        }

                        // Dibujar líneas
                        for (i in 0 until points.size - 1) {
                            drawLine(
                                color = lineColor,
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 4f
                            )
                        }

                        // Dibujar puntos
                        points.forEach { point ->
                            drawCircle(color = lineColor, radius = 6f, center = point)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
