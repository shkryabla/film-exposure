package com.filmexposure.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.MeterPoint
import com.filmexposure.domain.model.PointCategory
import kotlin.math.roundToInt

/**
 * Маркеры замера (§7.7). Цвета по договорённости проекта: тень → primary (не tertiary — tertiary
 * закреплён только за гиперфокалом), среднее → onSurface, свет → secondary.
 * Тап/свайп для удаления обрабатывается на уровне жеста превью (MainScreen), не здесь —
 * этот компонент только отрисовывает. point.x/y — пиксельные координаты тапа внутри превью.
 */
@Composable
fun MeterMarkersOverlay(points: List<MeterPoint>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        points.forEach { point ->
            val color = when (point.category) {
                PointCategory.SHADOW -> MaterialTheme.colorScheme.primary
                PointCategory.MID -> MaterialTheme.colorScheme.onSurface
                PointCategory.LIGHT -> MaterialTheme.colorScheme.secondary
            }
            val label = when (point.category) {
                PointCategory.SHADOW -> "тень"
                PointCategory.MID -> "среднее"
                PointCategory.LIGHT -> "свет"
            }

            Column(
                modifier = Modifier.offset {
                    IntOffset(point.x.roundToInt() - 20.dp.roundToPx(), point.y.roundToInt() - 20.dp.roundToPx())
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(color, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "%.1f".format(point.ev),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Text(label, color = color, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
