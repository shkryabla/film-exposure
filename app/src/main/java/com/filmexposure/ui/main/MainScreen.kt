package com.filmexposure.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * ВРЕМЕННАЯ ЗАГЛУШКА главного экрана. Подтверждает, что стек UI → ViewModel → Repository →
 * DataStore работает целиком (переключатель ЧБ реально сохраняется). Полноценный экран
 * экспонометра (превью камеры, шкала стопов, ленты пар, шкала дистанции — ТЗ §6.1) —
 * отдельная большая задача UI-слоя.
 */
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Film Exposure — калибровка пройдена",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "C = ${settings.calibrationConstant}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )
            BwToggle(settings.isBW, onToggle = viewModel::toggleBW)
        }
    }
}

@Composable
private fun BwToggle(isBW: Boolean, onToggle: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("ЧБ", style = MaterialTheme.typography.labelMedium)
        Switch(checked = isBW, onCheckedChange = onToggle)
    }
}
