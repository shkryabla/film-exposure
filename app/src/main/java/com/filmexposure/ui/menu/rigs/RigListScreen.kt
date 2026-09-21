package com.filmexposure.ui.menu.rigs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Список ригов (§6.2 "Мои риги"). Тап по карточке — выбрать для съёмки; карандаш — редактировать, корзина — удалить. */
@Composable
fun RigListScreen(
    onBack: () -> Unit,
    onCreateRig: () -> Unit,
    onEditRig: (Long) -> Unit,
    viewModel: RigListViewModel = hiltViewModel(),
) {
    val rigs by viewModel.rigs.collectAsStateWithLifecycle()
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()
    val lenses by viewModel.lenses.collectAsStateWithLifecycle()
    val selectedRigId by viewModel.selectedRigId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои риги") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRig) {
                Icon(Icons.Filled.Add, contentDescription = "Создать риг")
            }
        },
    ) { padding ->
        if (rigs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    "Пока нет ригов — нажмите + чтобы создать первый",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(rigs, key = { it.id }) { rig ->
                val cameraName = cameras.firstOrNull { it.id == rig.cameraId }?.name ?: rig.cameraId
                val lensName = lenses.firstOrNull { it.id == rig.lensId }?.name ?: rig.lensId
                val isSelected = rig.id == selectedRigId

                Card(
                    // Тап по карточке — выбрать риг для съёмки (первичное действие в списке-пикере).
                    // Редактирование и удаление — отдельные явные иконки, не спрятаны за жестами.
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { viewModel.selectRig(rig.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = "Выбран для съёмки",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 6.dp),
                                    )
                                }
                                Text(rig.name, style = MaterialTheme.typography.titleMedium)
                            }
                            androidx.compose.foundation.layout.Row {
                                IconButton(onClick = { onEditRig(rig.id) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                                }
                                IconButton(onClick = { viewModel.deleteRig(rig.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                                }
                            }
                        }
                        Text(
                            "$cameraName + $lensName, ${rig.focal} мм",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
