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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filmexposure.domain.model.MeterPoint
import com.filmexposure.domain.model.PointCategory
import kotlin.math.roundToInt

/**
 * Тень под текстом подписи (§7.7) — подпись лежит поверх произвольного кадра с камеры (не поверх
 * фиксированного Material-фона), однотонный цвет без тени может потеряться на светлом/пёстром участке.
 */
private val LABEL_SHADOW = Shadow(color = Color.Black, offset = Offset(0f, 1f), blurRadius = 4f)

/**
 * Маркеры замера (§7.7). Цвета по договорённости проекта: тень → primary (не tertiary — tertiary
 * закреплён только за гиперфокалом), среднее → onSurface, свет → secondary.
 * Текст числа Ev внутри кружка подбирается контрастным именно к своему фону — раньше был жёстко
 * белым, что на светлом фоне "среднее" (onSurface — почти белый в тёмной теме) было нечитаемо.
 * Тап/свайп для удаления обрабатывается на уровне жеста превью (MainScreen), не здесь —
 * этот компонент только отрисовывает. point.x/y — пиксельные координаты тапа внутри превью.
 */
@Composable
fun MeterMarkersOverlay(points: List<MeterPoint>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        points.forEach { point ->
            val circleColor = when (point.category) {
                PointCategory.SHADOW -> MaterialTheme.colorScheme.primary
                PointCategory.MID -> MaterialTheme.colorScheme.onSurface
                PointCategory.LIGHT -> MaterialTheme.colorScheme.secondary
            }
            // Контрастный к своему кругу текст (не универсальный белый):
            val onCircleColor = when (point.category) {
                PointCategory.SHADOW -> MaterialTheme.colorScheme.onPrimary
                PointCategory.MID -> MaterialTheme.colorScheme.surface
                PointCategory.LIGHT -> MaterialTheme.colorScheme.onSecondary
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
                    modifier = Modifier.size(40.dp).background(circleColor, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "%.1f".format(point.ev),
                        color = onCircleColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(shadow = LABEL_SHADOW),
                    color = Color.White,
                )
            }
        }
    }
}
