package com.filmexposure.ui.menu.catalog

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CustomFilmScreen(onBack: () -> Unit, viewModel: CustomFilmViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая плёнка") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(state.name, viewModel::setName, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.isoInput, viewModel::setIso, label = { Text("ISO") }, modifier = Modifier.fillMaxWidth())

            Text("Тип")
            Row {
                FILM_TYPES.forEach { type ->
                    Row(modifier = Modifier.selectable(selected = state.type == type, onClick = { viewModel.setType(type) })) {
                        RadioButton(selected = state.type == type, onClick = { viewModel.setType(type) })
                        Text(type)
                    }
                }
            }

            OutlinedTextField(state.latitudeMinusInput, viewModel::setLatitudeMinus, label = { Text("Широта, недодержка (стопов)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.latitudePlusInput, viewModel::setLatitudePlus, label = { Text("Широта, передержка (стопов)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.pushMaxInput, viewModel::setPushMax, label = { Text("Макс. пуш (стопов)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.notes, viewModel::setNotes, label = { Text("Заметки") }, modifier = Modifier.fillMaxWidth())

            Text(
                "Таблица взаимозаместимости (закон Шварцшильда) для своих плёнок пока не заполняется — " +
                    "используется без коррекции длинных выдержек.",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(onClick = viewModel::save, enabled = state.isValid, modifier = Modifier.fillMaxWidth()) { Text("Сохранить") }
        }
    }
}
