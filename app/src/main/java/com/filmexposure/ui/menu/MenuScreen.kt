package com.filmexposure.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Меню (§6.2) — полноэкранный диалог с 6 секциями. Навигация к подэкранам — через колбэки,
 * реальный NavHost-роутинг подключается в FilmExposureRoot.
 */
@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onOpenRigs: () -> Unit,
    onOpenCameras: () -> Unit,
    onOpenLenses: () -> Unit,
    onOpenFilms: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Меню") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
            MenuRow("Мои риги", Icons.Filled.PhotoCamera, onOpenRigs)
            HorizontalDivider()
            MenuRow("Камеры", Icons.Filled.CameraAlt, onOpenCameras)
            HorizontalDivider()
            MenuRow("Объективы", Icons.Filled.Lens, onOpenLenses)
            HorizontalDivider()
            MenuRow("Плёнки", Icons.Filled.PhotoCamera, onOpenFilms)
            HorizontalDivider()
            MenuRow("Настройки", Icons.Filled.Settings, onOpenSettings)
            HorizontalDivider()
            MenuRow("О программе", Icons.Filled.Info, onOpenAbout)
        }
    }
}

@Composable
private fun MenuRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        leadingContent = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    )
}
