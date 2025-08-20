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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.toArgb


data class StatEntry(val date: java.time.LocalDate, val pesoMax: Float)

@Composable
fun StatsPopup(
    stats: List<StatEntry>,
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    val lineColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val textColor = MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Estadísticas") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(8.dp)
            ) {
                if (stats.isEmpty()) {
                    Text("No hay datos")
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val padding = 50f // más espacio para etiquetas
                        val graphWidth = size.width - padding * 2
                        val graphHeight = size.height - padding * 2

                        val maxPeso = stats.maxOf { it.pesoMax }
                        val minPeso = stats.minOf { it.pesoMax }

                        // Dibujar ejes
                        drawLine(
                            color = axisColor,
                            start = Offset(padding, padding),
                            end = Offset(padding, padding + graphHeight),
                            strokeWidth = 2f
                        ) // eje Y
                        drawLine(
                            color = axisColor,
                            start = Offset(padding, padding + graphHeight),
                            end = Offset(padding + graphWidth, padding + graphHeight),
                            strokeWidth = 2f
                        ) // eje X

                        val xLabelPadding = 16f // margen extra a la izquierda
                        val points = stats.mapIndexed { index, stat ->
                            val x = padding + xLabelPadding + index * ((graphWidth - xLabelPadding) / (stats.size - 1).coerceAtLeast(1))
                            val y = padding + (1 - (stat.pesoMax - minPeso) / ((maxPeso - minPeso).coerceAtLeast(1f))) * graphHeight
                            Offset(x, y)
                        }

                        // Dibujar líneas y puntos
                        for (i in 0 until points.size - 1) {
                            drawLine(color = lineColor, start = points[i], end = points[i + 1], strokeWidth = 4f)
                        }
                        points.forEach { drawCircle(color = lineColor, radius = 6f, center = it) }

                        // Etiquetas Y (peso)
                        val steps = 5
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = 30f
                                textAlign = android.graphics.Paint.Align.RIGHT
                            }
                            for (i in 0..steps) {
                                val value = minPeso + i * (maxPeso - minPeso) / steps
                                val y = padding + (1 - (value - minPeso) / ((maxPeso - minPeso).coerceAtLeast(1f))) * graphHeight
                                drawText(value.toInt().toString(), padding - 25, y + 10, paint)
                            }
                        }

                        // Etiquetas X (fecha)
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            points.forEachIndexed { index, point ->
                                val dateLabel = stats[index].date.format(formatter)
                                drawText(dateLabel, point.x, padding + graphHeight + 35, paint)
                            }
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
