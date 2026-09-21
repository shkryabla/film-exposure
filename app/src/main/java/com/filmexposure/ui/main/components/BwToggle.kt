package com.filmexposure.ui.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Переключатель ЧБ (§7.9) — Switch, не иконка глаза; подпись "ЧБ" рядом. */
@Composable
fun BwToggle(isBW: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("ЧБ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = isBW, onCheckedChange = onToggle)
    }
}
