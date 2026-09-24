package com.filmexposure.ui.menu.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.domain.model.DistanceUnit

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenCalibration: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionLabel("Единицы дистанции")
            SingleChoiceRow(
                options = DistanceUnit.entries,
                selected = settings.distanceUnit,
                label = { if (it == DistanceUnit.METERS) "Метры" else "Футы" },
                onSelect = viewModel::setDistanceUnit,
            )
            HorizontalDivider()

            SectionLabel("Сторона кнопки \"Зафиксировать\"")
            SingleChoiceRow(
                options = ButtonSide.entries,
                selected = settings.pauseButtonSide,
                label = { if (it == ButtonSide.RIGHT) "Справа" else "Слева" },
                onSelect = viewModel::setPauseButtonSide,
            )
            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Чёрно-белое превью", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = settings.isBW, onCheckedChange = viewModel::setBW)
            }

            HorizontalDivider()
            SectionLabel("Калибровка")
            Text(
                if (settings.isCalibrated) {
                    "Откалибровано (C = ${"%.2f".format(settings.calibrationConstant)})"
                } else {
                    "Используется значение по умолчанию (C = ${"%.2f".format(settings.calibrationConstant)})"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onOpenCalibration, modifier = Modifier.fillMaxWidth()) {
                Text(if (settings.isCalibrated) "Перекалибровать" else "Откалибровать")
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun <T> SingleChoiceRow(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            Row(
                modifier = Modifier.selectable(selected = option == selected, onClick = { onSelect(option) }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option == selected, onClick = { onSelect(option) })
                Text(label(option))
            }
        }
    }
}
