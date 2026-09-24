package com.filmexposure.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Подпись поля + значок (i), раскрывающий объяснение термина (например "МДФ — что это?").
 * Использовать вместо голого Text(label) там, где название поля — непонятный новичку жаргон.
 */
@Composable
fun InfoLabel(label: String, explanation: String, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        IconButton(onClick = { showDialog = true }, modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Filled.Info,
                contentDescription = "Что это?",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = { Text(explanation) },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Понятно") } },
        )
    }
}
