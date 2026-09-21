package com.filmexposure.ui.calibration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmexposure.ui.camera.CameraPermissionGate
import com.filmexposure.ui.camera.CameraPreview
import com.filmexposure.ui.camera.rememberCameraController

/** Эталонный Ev по правилу "Солнце-16": ISO100, f/16, 1/125 в ясный полдень. Точность ±1 стоп. */
private const val SUNNY_16_EV = 15f

private enum class CalibrationStep { PICKER, SUNNY16, MANUAL }

/**
 * Калибровка (§4.2) — НЕОБЯЗАТЕЛЬНАЯ (решение по проекту, пересмотрено после обсуждения C):
 * без неё используется разумный дефолт (AppSettings.DEFAULT_CALIBRATION_CONSTANT), приложение
 * полностью работоспособно. Экран даёт три пути: Sunny-16 (доступно всем, ±1 стоп), ручной ввод
 * Bv (если есть второй телефон/камера с EXIF, точнее) и «Пропустить».
 */
@Composable
fun CalibrationScreen(
    onCalibrated: () -> Unit,
    viewModel: CalibrationViewModel = hiltViewModel(),
) {
    var step by remember { mutableStateOf(CalibrationStep.PICKER) }

    when (step) {
        CalibrationStep.PICKER -> PickerContent(
            onPickSunny16 = { step = CalibrationStep.SUNNY16 },
            onPickManual = { step = CalibrationStep.MANUAL },
            onSkip = onCalibrated,
        )
        CalibrationStep.SUNNY16 -> MeasureContent(
            title = "Sunny-16",
            instructions = "Выйдите на улицу в ясный день, без облаков, около полудня. " +
                "Наведите телефон на светлую однородную поверхность — асфальт, стену, небо, снег.",
            fixedReferenceEv = SUNNY_16_EV,
            onBack = { step = CalibrationStep.PICKER },
            onCalibrated = onCalibrated,
            viewModel = viewModel,
        )
        CalibrationStep.MANUAL -> MeasureContent(
            title = "Ввод Bv вручную",
            instructions = "Если у вас есть второй телефон с pro-камерой (EXIF) или плёночная/" +
                "цифровая камера — посчитайте Bv = Av + Tv − Sv по её показаниям для той же " +
                "сцены и введите число ниже.",
            fixedReferenceEv = null,
            onBack = { step = CalibrationStep.PICKER },
            onCalibrated = onCalibrated,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun PickerContent(onPickSunny16: () -> Unit, onPickManual: () -> Unit, onSkip: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Калибровка (необязательно)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Приложению не нужна калибровка, чтобы считать стопы и подбирать пары. " +
                    "Она нужна, только если хотите точных абсолютных значений.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )

            Button(onClick = onPickSunny16, modifier = Modifier.fillMaxWidth()) {
                Text("Sunny-16 — ясный день, точность ±1 стоп")
            }
            OutlinedButton(onClick = onPickManual, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text("Второй телефон или камера — ввести Bv вручную")
            }
            TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text("Пропустить — использовать значения по умолчанию")
            }
        }
    }
}

/** Общий сценарий и для Sunny-16 (referenceEv фиксирован), и для ручного ввода (referenceEv вводится). */
@Composable
private fun MeasureContent(
    title: String,
    instructions: String,
    fixedReferenceEv: Float?,
    onBack: () -> Unit,
    onCalibrated: () -> Unit,
    viewModel: CalibrationViewModel,
) {
    CameraPermissionGate {
        val controller = rememberCameraController()
        val frame by controller.frame.collectAsStateWithLifecycle()

        var measuredBv by remember { mutableStateOf<Float?>(null) }
        var manualInput by remember { mutableStateOf("") }
        val referenceEv = fixedReferenceEv ?: manualInput.toFloatOrNull()

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CameraPreview(controller, modifier = Modifier.fillMaxSize())
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = instructions,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    )

                    Button(
                        onClick = { frame?.let { measuredBv = viewModel.measureUncalibratedBv(it) } },
                        enabled = frame != null,
                    ) {
                        Text(if (frame == null) "Ждём кадр камеры…" else "Замерить")
                    }

                    measuredBv?.let { bv ->
                        Text(
                            text = "Замер без калибровки: Bv ≈ ${"%.2f".format(bv)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }

                    if (fixedReferenceEv == null) {
                        OutlinedTextField(
                            value = manualInput,
                            onValueChange = { manualInput = it },
                            label = { Text("Известный Bv сцены") },
                            enabled = measuredBv != null,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    }

                    Button(
                        onClick = {
                            val bv = measuredBv
                            val ref = referenceEv
                            if (bv != null && ref != null) viewModel.saveCalibration(ref - bv, onCalibrated)
                        },
                        enabled = measuredBv != null && referenceEv != null,
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Text("Сохранить и продолжить")
                    }

                    TextButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp)) {
                        Text("Назад")
                    }
                }
            }
        }
    }
}
