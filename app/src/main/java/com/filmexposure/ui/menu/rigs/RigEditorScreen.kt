package com.filmexposure.ui.menu.rigs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.model.Lens

/** Экран создания/редактирования рига (§6.3). rigId == null → новый риг. */
@Composable
fun RigEditorScreen(
    rigId: Long?,
    onBack: () -> Unit,
    viewModel: RigEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(rigId) { viewModel.load(rigId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (rigId == null) "Новый риг" else "Редактировать риг") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoading) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("Название рига") },
                modifier = Modifier.fillMaxWidth(),
            )

            LabeledDropdown(
                label = "Камера",
                items = state.cameras,
                selectedLabel = state.cameras.firstOrNull { it.id == state.cameraId }?.name,
                itemLabel = { it.name },
                onSelect = viewModel::setCamera,
            )

            LabeledDropdown(
                label = "Объектив",
                items = state.lenses,
                selectedLabel = state.lenses.firstOrNull { it.id == state.lensId }?.name,
                itemLabel = { it.name },
                onSelect = viewModel::setLens,
            )

            OutlinedTextField(
                value = state.focalInput,
                onValueChange = viewModel::setFocal,
                label = { Text("Фокусное (мм)") },
                modifier = Modifier.fillMaxWidth(),
            )

            LabeledDropdown(
                label = "Формат плёнки",
                items = state.formats,
                selectedLabel = state.formats.firstOrNull { it.id == state.formatId }?.name,
                itemLabel = { it.name },
                onSelect = viewModel::setFormat,
            )

            OutlinedTextField(
                value = state.activeSpeedsInput,
                onValueChange = viewModel::setActiveSpeeds,
                label = { Text("Выдержки (через ;)") },
                modifier = Modifier.fillMaxWidth(),
            )
            ParsedPreviewRow(viewModel.parsedSpeedsPreview())

            OutlinedTextField(
                value = state.activeAperturesInput,
                onValueChange = viewModel::setActiveApertures,
                label = { Text("Диафрагмы (через ;)") },
                modifier = Modifier.fillMaxWidth(),
            )
            ParsedPreviewRow(viewModel.parsedAperturesPreview())

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::setNotes,
                label = { Text("Заметки") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = viewModel::save,
                enabled = state.isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Сохранить")
            }
        }
    }
}

/** Показ распознанных значений перед сохранением (§6.4). */
@Composable
private fun ParsedPreviewRow(values: List<String>) {
    if (values.isEmpty()) return
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(values.size) { index ->
            AssistChip(onClick = {}, label = { Text(values[index], style = MaterialTheme.typography.labelSmall) })
        }
    }
}

@Composable
private fun <T> LabeledDropdown(
    label: String,
    items: List<T>,
    selectedLabel: String?,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    },
                )
            }
        }
    }
}
