package com.filmexposure.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filmexposure.domain.model.ExposurePair

/**
 * Ленты пар (§7.4). УПРОЩЕНИЕ v1: в ТЗ — две раздельные синхронные ленты друг над другом;
 * здесь — одна лента карточек "диафрагма над выдержкой" (та же информация, тот же принцип
 * выбора, но без раздельного скролла двух полос). Snap-физика и elevation-анимация выбора —
 * отдельная полировка, сейчас выбор мгновенный по тапу.
 */
@Composable
fun ExposurePairsRow(
    pairs: List<ExposurePair>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pairs.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "Нет пар под текущий Ev в активном наборе рига",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        itemsIndexed(pairs) { index, pair ->
            val selected = index == selectedIndex
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp),
                modifier = Modifier.clickable { onSelect(index) },
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(pair.aperture, style = MaterialTheme.typography.headlineSmall)
                    Text(pair.shutter, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
