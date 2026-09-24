package com.filmexposure.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Цветовая схема приложения. Согласно ТЗ §9:
 * - Dynamic Color отключён.
 * - Экран камеры всегда тёмный независимо от системной темы.
 * Пока используем единую тёмную схему для всего приложения; при необходимости
 * светлая схема для НЕ-камерных экранов будет добавлена отдельно на этапе UI-слоя.
 */
private val FilmExposureDarkScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,

    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,

    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
)

@Composable
fun FilmExposureTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FilmExposureDarkScheme,
        typography = FilmExposureTypography,
        content = content,
    )
}
