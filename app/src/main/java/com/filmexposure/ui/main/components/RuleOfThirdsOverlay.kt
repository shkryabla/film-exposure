package com.filmexposure.ui.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Сетка третей (§7.2): 1dp линии, alpha 0.3, опционально точки на пересечениях. Не участвует в расчётах. */
@Composable
fun RuleOfThirdsOverlay(showIntersections: Boolean, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val dotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.dp.toPx())

        for (i in 1..2) {
            val x = w * i / 3f
            drawLine(lineColor, Offset(x, 0f), Offset(x, h), strokeWidth = stroke.width)
            val y = h * i / 3f
            drawLine(lineColor, Offset(0f, y), Offset(w, y), strokeWidth = stroke.width)
        }

        if (showIntersections) {
            for (i in 1..2) {
                for (j in 1..2) {
                    drawCircle(dotColor, radius = 4.dp.toPx(), center = Offset(w * i / 3f, h * j / 3f))
                }
            }
        }
    }
}
