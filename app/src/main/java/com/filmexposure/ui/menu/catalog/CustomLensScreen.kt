package com.filmexposure.ui.menu.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
fun CustomLensScreen(onBack: () -> Unit, viewModel: CustomLensViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый объектив") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(state.name, viewModel::setName, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.focalMinInput, viewModel::setFocalMin, label = { Text("Фокусное мин. (мм)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.focalMaxInput, viewModel::setFocalMax, label = { Text("Фокусное макс. (мм)") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = state.aperturesInput,
                onValueChange = viewModel::setAperturesInput,
                label = { Text("Диафрагмы (через ;)") },
                modifier = Modifier.fillMaxWidth(),
            )
            PreviewChips(viewModel.parsedAperturesPreview())

            OutlinedTextField(state.minFocusInput, viewModel::setMinFocus, label = { Text("МДФ (м)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.mount, viewModel::setMount, label = { Text("Байонет (опционально)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.notes, viewModel::setNotes, label = { Text("Заметки") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = viewModel::save, enabled = state.isValid, modifier = Modifier.fillMaxWidth()) { Text("Сохранить") }
        }
    }
}
