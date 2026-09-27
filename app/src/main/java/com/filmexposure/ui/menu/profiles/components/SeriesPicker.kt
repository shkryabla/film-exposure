package com.filmexposure.ui.menu.profiles.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Выбор серии (диафрагмы/выдержки — Modern/Old European) сегментированной кнопкой вместо
 * дропдауна (решение по проекту — для 2 фиксированных вариантов нагляднее). Под выбором —
 * текстом сам список значений выбранного ряда.
 *
 * Сравнение selected по значению enum, а не по индексу — гарантирует ровно один активный
 * сегмент независимо от порядка options и не путается между независимыми диафрагмами/выдержками.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SeriesPicker(
    label: String,
    options: List<T>,
    optionLabel: (T) -> String,
    valuesPreview: (T) -> List<String>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    label = { Text(optionLabel(option)) },
                )
            }
        }
        Text(
            text = valuesPreview(selected).joinToString(", "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
