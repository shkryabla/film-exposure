package com.filmexposure.ui.main.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonochromePhotos
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Переключатель ЧБ — иконка вместо ползунка (решение по проекту при упрощении экрана).
 * MonochromePhotos — однозначно читается как "чёрно-белый режим", в отличие от нейтрального
 * "фильтра"; подсвечена primary, когда включена.
 */
@Composable
fun BwToggle(isBW: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = { onToggle(!isBW) }, modifier = modifier) {
        Icon(
            imageVector = if (isBW) Icons.Filled.MonochromePhotos else Icons.Filled.PhotoCamera,
            contentDescription = if (isBW) "Чёрно-белый режим включён" else "Чёрно-белый режим выключен",
            tint = if (isBW) MaterialTheme.colorScheme.primary else Color.White,
        )
    }
}
