package com.filmexposure.ui.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

/** Крестик в центре превью (§6.1) — визуальный ориентир, в расчётах не участвует. */
@Composable
fun CrosshairOverlay(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val armPx = 10.dp.toPx()
        val strokePx = 1.5.dp.toPx()
        drawLine(color, Offset(cx - armPx, cy), Offset(cx + armPx, cy), strokeWidth = strokePx)
        drawLine(color, Offset(cx, cy - armPx), Offset(cx, cy + armPx), strokeWidth = strokePx)
    }
}
