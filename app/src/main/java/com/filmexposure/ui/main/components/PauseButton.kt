package com.filmexposure.ui.main.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Кнопка пауза/пуск (§7.6): 56dp FAB, под шкалой дистанции. Morph-анимация 200мс — полировка. */
@Composable
fun PauseButton(isFrozen: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onToggle,
        containerColor = if (isFrozen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.size(56.dp),
    ) {
        Icon(
            imageVector = if (isFrozen) Icons.Filled.PlayArrow else Icons.Filled.Pause,
            contentDescription = if (isFrozen) "Продолжить" else "Заморозить кадр",
        )
    }
}
