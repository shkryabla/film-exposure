package com.filmexposure.ui.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Создаёт [CameraController] на время жизни компоновки и биндит/анбиндит его к жизненному циклу
 * текущего LifecycleOwner (экран уходит с композиции → камера освобождается для других экранов/приложений).
 */
@Composable
fun rememberCameraController(): CameraController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember { CameraController(context) }

    DisposableEffect(lifecycleOwner) {
        controller.bind(lifecycleOwner)
        onDispose { controller.unbind() }
    }

    return controller
}

/**
 * Превью камеры (§7.1). Живой поток или замороженный Bitmap — переключается снаружи через
 * [CameraController.freeze]/[unfreeze]. Оверлеи (сетка третей, маркеры, крестик — §7.2, §7.7)
 * подключаются отдельным Canvas поверх этого компонента на этапе главного экрана.
 */
@Composable
fun CameraPreview(controller: CameraController, isBW: Boolean = false, modifier: Modifier = Modifier) {
    val frozenBitmap by controller.frozenBitmap.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().grayscale(isBW)) {
        val bitmap = frozenBitmap
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AndroidView(
                factory = { controller.previewView },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * ЧБ-эффект (§7.9). AndroidView (живой поток CameraX) не поддерживает Compose ColorFilter
 * напрямую — обходной путь через saveLayer с Paint.colorFilter, единственный способ применить
 * фильтр единообразно и к Image (заморозка), и к AndroidView (живой поток). alpha=0.99f — известный
 * приём, заставляющий Compose создать реальный offscreen-слой для перехвата AndroidView-контента.
 */
private val GRAYSCALE_PAINT = Paint().apply {
    colorFilter = androidx.compose.ui.graphics.ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
}

private fun Modifier.grayscale(enabled: Boolean): Modifier {
    if (!enabled) return this
    return this.graphicsLayer(alpha = 0.99f).drawWithContent {
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(Offset.Zero, size), GRAYSCALE_PAINT)
            drawContent()
            canvas.restore()
        }
    }
}
