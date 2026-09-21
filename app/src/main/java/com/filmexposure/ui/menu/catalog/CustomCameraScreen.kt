package com.filmexposure.ui.menu.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.filmexposure.domain.model.FilmFormat

@Composable
fun CustomCameraScreen(onBack: () -> Unit, viewModel: CustomCameraViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая камера") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(state.name, viewModel::setName, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())

            var formatExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = formatExpanded, onExpandedChange = { formatExpanded = it }) {
                OutlinedTextField(
                    value = state.formats.firstOrNull { it.id == state.formatId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Формат плёнки") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                androidx.compose.material3.ExposedDropdownMenu(expanded = formatExpanded, onDismissRequest = { formatExpanded = false }) {
                    state.formats.forEach { format: FilmFormat ->
                        DropdownMenuItem(text = { Text(format.name) }, onClick = { viewModel.setFormat(format); formatExpanded = false })
                    }
                }
            }

            Text("Тип затвора")
            Row {
                SHUTTER_TYPES.forEach { type ->
                    Row(
                        modifier = Modifier.selectable(selected = state.shutterType == type, onClick = { viewModel.setShutterType(type) }),
                    ) {
                        RadioButton(selected = state.shutterType == type, onClick = { viewModel.setShutterType(type) })
                        Text(type)
                    }
                }
            }

            OutlinedTextField(
                value = state.speedsInput,
                onValueChange = viewModel::setSpeedsInput,
                label = { Text("Выдержки (через ;)") },
                modifier = Modifier.fillMaxWidth(),
            )
            PreviewChips(viewModel.parsedSpeedsPreview())

            OutlinedTextField(state.xSync, viewModel::setXSync, label = { Text("X-sync (опционально)") }, modifier = Modifier.fillMaxWidth())

            Text("Шаг")
            Row {
                TECH_STEPS.forEach { step ->
                    Row(modifier = Modifier.selectable(selected = state.step == step, onClick = { viewModel.setStep(step) })) {
                        RadioButton(selected = state.step == step, onClick = { viewModel.setStep(step) })
                        Text(step)
                    }
                }
            }

            OutlinedTextField(state.notes, viewModel::setNotes, label = { Text("Заметки") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = viewModel::save, enabled = state.isValid, modifier = Modifier.fillMaxWidth()) { Text("Сохранить") }
        }
    }
}

@Composable
fun PreviewChips(values: List<String>) {
    if (values.isEmpty()) return
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(values.size) { index -> AssistChip(onClick = {}, label = { Text(values[index]) }) }
    }
}
