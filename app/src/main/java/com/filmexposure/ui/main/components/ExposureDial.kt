package com.filmexposure.ui.main.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filmexposure.domain.model.ExposurePair
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Ленты пар как две катушки-«гурты монет» (§7.4), лежащие одна на другой: диафрагмы сверху,
 * выдержки снизу. Видна только "боковая поверхность" каждой — текущее значение по центру плоское
 * и крупное, соседние заворачиваются по кривизне влево/вправо (rotationY) и утончаются к краю,
 * как будто катушка реально круглая и мы смотрим на неё сбоку. Обе катушки крутятся ОДНИМ жестом
 * синхронно — пары уже посчитаны один-к-одному под целевой Ev (CalculateValidPairsUseCase),
 * рассинхронизировать катушки было бы физически бессмысленно (диафрагма без своей выдержки не
 * даёт валидную пару).
 *
 * УПРОЩЕНИЕ v1: после отпускания — доводка до ближайшего значения через spring, без инерции
 * («доскролла» по инерции пальца); угловой шаг и радиус кривизны — подобраны на глаз, потребуют
 * подстройки при взгляде на реальном экране.
 */
@Composable
fun ExposureDial(
    pairs: List<ExposurePair>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pairs.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                "Нет пар под текущий Ev в активном наборе рига",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    val safeIndex = selectedIndex.coerceIn(0, pairs.lastIndex)
    var liveDragIndexDelta by remember { mutableStateOf(0f) }

    val settledIndex by animateFloatAsState(
        targetValue = safeIndex.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "reelIndex",
    )
    val continuousIndex = settledIndex + liveDragIndexDelta

    val density = LocalDensity.current
    val pxPerItem = with(density) { 56.dp.toPx() } // сколько пикселей драга = один шаг ленты

    val highlightColor = MaterialTheme.colorScheme.primary
    val normalColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.pointerInput(pairs.size) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    val newIndex = ((safeIndex + liveDragIndexDelta.roundToInt()) % pairs.size + pairs.size) % pairs.size
                    onSelect(newIndex)
                    liveDragIndexDelta = 0f
                },
                onDragCancel = { liveDragIndexDelta = 0f },
            ) { change, dragAmount ->
                change.consume()
                liveDragIndexDelta -= dragAmount / pxPerItem
            }
        },
    ) {
        CoinEdgeReel(
            labels = pairs.map { it.aperture },
            continuousIndex = continuousIndex,
            highlightColor = highlightColor,
            normalColor = normalColor,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        CoinEdgeReel(
            labels = pairs.map { it.shutter },
            continuousIndex = continuousIndex,
            highlightColor = highlightColor,
            normalColor = normalColor,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        )
    }
}

private const val ANGLE_STEP_DEGREES = 26f
private const val CURVE_RADIUS_DP = 90f
private const val MAX_VISIBLE_ANGLE = 85f
private const val VISIBLE_ITEMS_EACH_SIDE = 3

/** Одна катушка — гурт монеты сбоку: центр плоский и крупный, края заворачиваются по кривизне и тают. */
@Composable
private fun CoinEdgeReel(
    labels: List<String>,
    continuousIndex: Float,
    highlightColor: Color,
    normalColor: Color,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { CURVE_RADIUS_DP.dp.toPx() }
    val centerSlot = continuousIndex.roundToInt()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        for (slot in (centerSlot - VISIBLE_ITEMS_EACH_SIDE)..(centerSlot + VISIBLE_ITEMS_EACH_SIDE)) {
            val angleDeg = (slot - continuousIndex) * ANGLE_STEP_DEGREES
            if (abs(angleDeg) > MAX_VISIBLE_ANGLE) continue

            val labelIndex = ((slot % labels.size) + labels.size) % labels.size
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val curveFactor = cos(angleRad).toFloat().coerceAtLeast(0f)
            val translationXPx = radiusPx * sin(angleRad).toFloat()
            val isCenter = slot == centerSlot
            val fontSize = if (isCenter) 22.sp else 16.sp

            Text(
                text = labels[labelIndex],
                color = if (isCenter) highlightColor else normalColor,
                fontSize = fontSize,
                modifier = Modifier.graphicsLayer {
                    translationX = translationXPx
                    rotationY = angleDeg
                    alpha = curveFactor
                    cameraDistance = 16f * density.density
                },
            )
        }
    }
}
