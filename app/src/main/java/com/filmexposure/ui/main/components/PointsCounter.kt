package com.filmexposure.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Счётчик точек замера (§7.8): 3 кружка, подсветка primary при 3/3, иконка очистки. */
@Composable
fun PointsCounter(count: Int, onClear: () -> Unit, modifier: Modifier = Modifier) {
    val filledColor = if (count >= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val emptyColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { index ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(10.dp)
                    .background(if (index < count) filledColor else emptyColor, CircleShape),
            )
        }
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Сбросить замеры",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp).size(18.dp).clickable(onClick = onClear),
        )
    }
}
