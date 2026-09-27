package com.filmexposure.ui.menu.profiles
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.filmexposure.domain.model.ApertureSeries
import com.filmexposure.domain.model.ApertureSeriesData
import com.filmexposure.domain.model.ShutterSeries
import com.filmexposure.domain.model.ShutterSeriesData
import com.filmexposure.ui.menu.profiles.components.SeriesPicker
/** Экран создания/редактирования профиля (замена рига+камеры+объектива+плёнки). profileId == null → новый. */
@Composable
fun ProfileEditorScreen(
    profileId: Long?,
    onBack: () -> Unit,
    viewModel: ProfileEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(profileId) { viewModel.load(profileId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (profileId == null) "Новый профиль" else "Редактировать профиль") },
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("Название профиля") },
                modifier = Modifier.fillMaxWidth(),
            )
            SeriesPicker(
                label = "Диафрагмы",
                options = ApertureSeries.entries,
                optionLabel = { if (it == ApertureSeries.MODERN) "Modern" else "Old European" },
                valuesPreview = ApertureSeriesData::valuesFor,
                selected = state.apertureSeries,
                onSelected = viewModel::setApertureSeries,
            )
            SeriesPicker(
                label = "Выдержки",
                options = ShutterSeries.entries,
                optionLabel = { if (it == ShutterSeries.MODERN) "Modern" else "Old European" },
                valuesPreview = ShutterSeriesData::valuesFor,
                selected = state.shutterSeries,
                onSelected = viewModel::setShutterSeries,
            )
            OutlinedTextField(
                value = state.focalInput,
                onValueChange = viewModel::setFocal,
                label = { Text("Фокусное (мм)") },
                modifier = Modifier.fillMaxWidth(),
            )
            var formatExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = formatExpanded, onExpandedChange = { formatExpanded = it }) {
                OutlinedTextField(
                    value = state.formats.firstOrNull { it.id == state.formatId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Формат кадра") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                ExposedDropdownMenu(expanded = formatExpanded, onDismissRequest = { formatExpanded = false }) {
                    state.formats.forEach { format ->
                        DropdownMenuItem(
                            text = { Text(format.name) },
                            onClick = { viewModel.setFormat(format); formatExpanded = false },
                        )
                    }
                }
            }
            Button(onClick = viewModel::save, enabled = state.isValid, modifier = Modifier.fillMaxWidth()) {
                Text("Сохранить")
            }
        }
    }
}
