package com.filmexposure.ui.menu.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

/** Список профилей (§6.2 "Профили"). Тап по карточке — выбрать для съёмки; карандаш/корзина — редактировать/удалить. */
@Composable
fun ProfileListScreen(
    onBack: () -> Unit,
    onCreateProfile: () -> Unit,
    onEditProfile: (Long) -> Unit,
    viewModel: ProfileListViewModel = hiltViewModel(),
) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedProfileId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профили") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateProfile) {
                Icon(Icons.Filled.Add, contentDescription = "Создать профиль")
            }
        },
    ) { padding ->
        if (profiles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    "Пока нет профилей — нажмите + чтобы создать первый",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(profiles, key = { it.id }) { profile ->
                val isSelected = profile.id == selectedId
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { viewModel.selectProfile(profile.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = "Выбран для съёмки",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 6.dp),
                                    )
                                }
                                Text(profile.name, style = MaterialTheme.typography.titleMedium)
                            }
                            Row {
                                IconButton(onClick = { onEditProfile(profile.id) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                                }
                                IconButton(onClick = { viewModel.deleteProfile(profile.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                                }
                            }
                        }
                        Text(
                            "ISO ${profile.iso} · ${profile.focalMm} мм · ${profile.apertures.size} диафрагм · ${profile.shutters.size} выдержек",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
