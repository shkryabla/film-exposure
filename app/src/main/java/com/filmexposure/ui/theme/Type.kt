package com.filmexposure.ui.theme

import androidx.compose.material3.Typography

// Базовая типографика M3. Точные размеры под элементы экспонометра (headlineSmall 24sp
// для значений диафрагмы/выдержки, labelSmall 11sp для единиц и т.д. — ТЗ §10)
// уже входят в дефолтную шкалу Typography() из material3 и переопределяются точечно
// в конкретных компонентах на этапе UI-слоя.
val FilmExposureTypography = Typography()
