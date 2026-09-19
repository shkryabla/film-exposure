package com.filmexposure.ui.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
fun CameraPreview(controller: CameraController, modifier: Modifier = Modifier) {
    val frozenBitmap by controller.frozenBitmap.collectAsStateWithLifecycle()

    val bitmap = frozenBitmap
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        AndroidView(
            factory = { controller.previewView },
            modifier = modifier.fillMaxSize(),
        )
    }
}
