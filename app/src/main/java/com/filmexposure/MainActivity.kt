package com.filmexposure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.filmexposure.ui.theme.FilmExposureTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * ВРЕМЕННАЯ ЗАГЛУШКА.
 * Точка входа приложения. Главный экран-экспонометр (превью камеры, шкала стопов,
 * ленты пар, шкала дистанции — ТЗ §6.1) подключается на этапе UI-слоя (§18, шаг 9),
 * вместе с Navigation Compose и Hilt-инъекцией ViewModel.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmExposureTheme {
                SkeletonPlaceholder()
            }
        }
    }
}

@Composable
private fun SkeletonPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Film Exposure — skeleton",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
