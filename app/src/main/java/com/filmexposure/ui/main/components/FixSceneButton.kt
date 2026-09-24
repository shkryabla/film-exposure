package com.filmexposure.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * "Зафиксировать" (бывшая пауза, §7.6) — стопорит кадр+Ev+пары одновременно (MainViewModel.toggleFreeze
 * + CameraController.freeze/unfreeze вызываются вместе на уровне MainScreen). Стеклянная круглая
 * кнопка вместо FAB — под общий glassmorphism-стиль экрана.
 */
@Composable
fun FixSceneButton(isFrozen: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(56.dp)
            .background(
                color = if (isFrozen) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.45f),
                shape = CircleShape,
            )
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.2f), shape = CircleShape),
    ) {
        Icon(
            imageVector = if (isFrozen) Icons.Filled.Lock else Icons.Filled.CameraAlt,
            contentDescription = if (isFrozen) "Снять фиксацию" else "Зафиксировать сцену",
            tint = Color.White,
        )
    }
}
