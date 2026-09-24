package com.filmexposure.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Панель в стиле glassmorphism: скруглённые углы, полупрозрачная тёмная заливка, тонкая светлая
 * рамка — поверх живого превью камеры читается как "стекло".
 *
 * ЧЕСТНО: здесь НЕТ настоящего backdrop-blur (размытия того, что ПОЗАДИ панели, как в iOS).
 * Modifier.blur() в Compose размывает содержимое САМОЙ панели (текст/иконки внутри неё), а не
 * фон за ней — это другой эффект, и применять его сюда было бы ошибкой (текст стал бы нечитаемым).
 * Настоящий backdrop-blur через RenderEffect — сложная, версионно-зависимая техника, которую
 * рискованно писать не глядя на реальное устройство. Полупрозрачная плашка без блюра — тоже
 * узнаваемый "стеклянный" стиль, просто без размытия фона; можно добавить позже осознанно.
 */
fun Modifier.glassPanel(cornerRadius: Dp = 20.dp): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(color = Color.Black.copy(alpha = 0.45f), shape = shape)
        .border(width = 1.dp, color = Color.White.copy(alpha = 0.2f), shape = shape)
}
