package com.filmexposure.ui.main.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.DistanceUnit
import com.filmexposure.domain.model.DofResult
import com.filmexposure.ui.theme.glassPanel
import kotlin.math.exp
import kotlin.math.ln

/**
 * ГРИП-панель (§7.5), стеклянная, вдоль правого края экрана. УПРОЩЕНИЕ по решению проекта:
 * вместо полной шкалы с засечками — три метки (дальняя граница / точка фокуса / ближняя
 * граница), перетаскивание по вертикали меняет дистанцию фокусировки логарифмически.
 */
@Composable
fun DistanceScale(
    distanceM: Float,
    onDistanceChange: (Float) -> Unit,
    dof: DofResult?,
    unit: DistanceUnit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .glassPanel()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    // тянем вверх (dragAmount<0) -> дистанция растёт; лог-шаг ~на весь размер панели = x10
                    val logDelta = -dragAmount / size.height.coerceAtLeast(1) * ln(10f)
                    val newDistance = exp(ln(distanceM.coerceAtLeast(0.1f)) + logDelta)
                    onDistanceChange(newDistance)
                }
            },
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val farText = dof?.let { if (it.far.isInfinite()) "∞" else formatDistance(it.far / 1000f, unit) } ?: "—"
        val nearText = dof?.let { formatDistance(it.near / 1000f, unit) } ?: "—"
        val focusText = formatDistance(distanceM, unit)

        DistanceLabel(farText, emphasized = false)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DistanceLabel(focusText, emphasized = true)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp,
            )
        }
        DistanceLabel(nearText, emphasized = false)
    }
}

@Composable
private fun DistanceLabel(text: String, emphasized: Boolean) {
    Text(
        text = text,
        style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
        color = if (emphasized) Color.White else Color.White.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
    )
}

private fun formatDistance(m: Float, unit: DistanceUnit): String = when (unit) {
    DistanceUnit.METERS -> "%.1fм".format(m)
    DistanceUnit.FEET -> "%.1fфт".format(m * 3.28084f)
}
