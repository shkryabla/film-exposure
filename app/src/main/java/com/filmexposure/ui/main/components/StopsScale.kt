package com.filmexposure.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.ExposurePostingResult
import com.filmexposure.domain.model.ScaleMode

/**
 * Шкала стопов (§7.3). Simple — 3 зоны для новичков (иконки вместо цифр). Pro — числовая шкала
 * −3…+3 с перетаскиваемым маркером экспокоррекции (evShift, §8.4) и строкой с широтой сцены/плёнки.
 * Долгий тап переключает Simple↔Pro (упрощённо: без morph-анимации 200мс из §7.3 — визуальная
 * полировка отложена).
 */
@Composable
fun StopsScale(
    scaleMode: ScaleMode,
    postingResult: ExposurePostingResult?,
    evShift: Float,
    filmLatitudePlus: Int,
    onShiftChange: (Float) -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = { onToggleMode() })
        },
    ) {
        when (scaleMode) {
            ScaleMode.SIMPLE -> SimpleZones(evShift)
            ScaleMode.PRO -> ProScale(postingResult, evShift, filmLatitudePlus, onShiftChange)
        }
    }
}

@Composable
private fun SimpleZones(evShift: Float) {
    val zone = when {
        evShift < -0.3f -> 0
        evShift > 0.3f -> 2
        else -> 1
    }
    val labels = listOf("🌑 Темнее", "⚖️ Норма", "☀️ Светлее")
    val colors = listOf(
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
    )

    Row(modifier = Modifier.fillMaxSize()) {
        labels.forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(2.dp)
                    .background(
                        if (index == zone) colors[index] else colors[index].copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ProScale(postingResult: ExposurePostingResult?, evShift: Float, filmLatitudePlus: Int, onShiftChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val stopsPerPx = 6f / size.width.coerceAtLeast(1) // диапазон -3..+3 на всю ширину
                        onShiftChange(evShift + dragAmount * stopsPerPx)
                    }
                },
        ) {
            // Трек шкалы
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            // Маркер: -3..+3 -> 0..1 доли ширины, смещаем на половину своего размера для центрирования
            val fraction = ((evShift + 3f) / 6f).coerceIn(0f, 1f)
            val markerSize = 16.dp
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = maxWidth * fraction - markerSize / 2)
                    .size(markerSize)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            )
        }
        val info = if (postingResult != null) {
            val sceneSpan = postingResult.points.maxOfOrNull { it.ev } ?.minus(postingResult.points.minOfOrNull { it.ev } ?: 0f) ?: 0f
            "Сцена ${"%.1f".format(sceneSpan)} стоп, плёнка ±$filmLatitudePlus, " +
                "недобор теней ${"%.1f".format(postingResult.shadowLossStops)}, " +
                "перебор света ${"%.1f".format(postingResult.lightLossStops)}"
        } else {
            "Сделайте замер"
        }
        Text(info, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
