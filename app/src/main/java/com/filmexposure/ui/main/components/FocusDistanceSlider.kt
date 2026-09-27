package com.filmexposure.ui.main.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.DistanceUnit
import com.filmexposure.domain.model.DofResult
import com.filmexposure.ui.theme.glassPanel
import kotlin.math.roundToInt

/**
 * Дистанция фокусировки — линейный Slider с динамическим шагом (§8.4, FocusDistanceStops),
 * заменяет старый лог-драг по боковой панели. Панель убрана целиком (решение по проекту) —
 * слайдер работает всегда, а результат ГРИП показывается текстом отдельно (см. [DofResultText]),
 * только когда пара выбрана явным тапом в ExposureDial.
 */
@Composable
fun FocusDistanceSlider(
    stops: List<Float>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit,
    unit: DistanceUnit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.glassPanel().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { onIndexChanged(it.roundToInt()) }, // Slider всегда отдаёт Float, даже с фиксированным steps
            valueRange = 0f..stops.lastIndex.toFloat(),
            steps = (stops.size - 2).coerceAtLeast(0), // засечки между первой и последней точкой
        )
        Text(
            text = formatDistance(stops[selectedIndex], unit),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Текст результата ГРИП по центру экрана (§8.2) — показывается только когда пара выбрана тапом. */
@Composable
fun DofResultText(dof: DofResult, unit: DistanceUnit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.glassPanel().padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val near = formatDistance(dof.near / 1000f, unit)
        val far = if (dof.far.isInfinite()) "∞" else formatDistance(dof.far / 1000f, unit)
        Text("$near … $far", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(
            "гиперфокал ${formatDistance(dof.hyperfocal / 1000f, unit)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
        )
    }
}

private fun formatDistance(meters: Float, unit: DistanceUnit): String = when {
    meters.isInfinite() -> "∞"
    unit == DistanceUnit.METERS -> "%.1fм".format(meters)
    else -> "%.1fфт".format(meters * 3.28084f)
}
