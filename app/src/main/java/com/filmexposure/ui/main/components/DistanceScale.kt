package com.filmexposure.ui.main.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.DistanceUnit
import com.filmexposure.domain.model.DofResult
import kotlin.math.exp
import kotlin.math.ln

/**
 * Шкала дистанции (§7.5). УПРОЩЕНИЕ v1: засечки расположены равномерно (не по истинным
 * логарифмическим позициям 0.5/1/2/3/5/10/20/∞), перетаскивание меняет дистанцию логарифмически.
 * Маркер гиперфокала/точный snap на засечки — отдельная полировка.
 */
@Composable
fun DistanceScale(
    distanceM: Float,
    onDistanceChange: (Float) -> Unit,
    dof: DofResult?,
    unit: DistanceUnit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        // тянем вверх (dragAmount<0) -> дистанция растёт; лог-шаг ~на весь размер экрана = x10
                        val logDelta = -dragAmount / size.height.coerceAtLeast(1) * ln(10f)
                        val newDistance = exp(ln(distanceM.coerceAtLeast(0.1f)) + logDelta)
                        onDistanceChange(newDistance)
                    }
                },
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().align(Alignment.Center),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf("∞", "20", "10", "5", "3", "2", "1", "0.5").forEach { tick ->
                    Text(tick, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        val dofText = dof?.let { formatDof(it, unit) } ?: "—"
        Text(
            text = dofText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private fun formatDof(dof: DofResult, unit: DistanceUnit): String {
    val nearM = dof.near / 1000f
    val farM = if (dof.far.isInfinite()) null else dof.far / 1000f
    fun fmt(m: Float): String = when (unit) {
        DistanceUnit.METERS -> "%.1f м".format(m)
        DistanceUnit.FEET -> "%.1f фт".format(m * 3.28084f)
    }
    return if (farM == null) "Резко от ${fmt(nearM)} до ∞" else "Резко от ${fmt(nearM)} до ${fmt(farM)}"
}
