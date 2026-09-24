package com.filmexposure.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.vector.ImageVector

/** Меню (§6.2) — теперь три секции: Профили, Настройки, О программе. */
@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onOpenProfiles: () -> Unit,
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
            MenuRow("Профили", Icons.Filled.AccountBox, onOpenProfiles)
            HorizontalDivider()
            MenuRow("Настройки", Icons.Filled.Settings, onOpenSettings)
            HorizontalDivider()
            MenuRow("О программе", Icons.Filled.Info, onOpenAbout)
        }
    }
}

@Composable
private fun MenuRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        leadingContent = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    )
}
