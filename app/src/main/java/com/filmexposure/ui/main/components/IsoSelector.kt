package com.filmexposure.ui.main.components
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.filmexposure.domain.model.ISO_STOPS

/**
 * Выбор ISO плёнки — на главном экране (не в Profile, решение по проекту), рядом с иконкой ЧБ,
 * под ней. Full-stop ряд ISO_STOPS, консистентно с диафрагмами/выдержками — никаких half/third.
 */
@Composable
fun IsoSelector(currentIso: Int, onIsoSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        TextButton(onClick = { expanded = true }) {
            Text("ISO $currentIso", color = Color.White.copy(alpha = 0.9f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ISO_STOPS.forEach { iso ->
                DropdownMenuItem(
                    text = { Text("ISO $iso") },
                    onClick = { onIsoSelected(iso); expanded = false },
                )
            }
        }
    }
}
