package com.filmexposure.ui.calibration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

/**
 * Калибровка по реальному кадру камеры (§4.2). Пользователь наводит камеру на сцену с ИЗВЕСТНЫМ
 * заранее Ev (снятым отдельным экспонометром либо посчитанным по правилу "Солнце-16"), жмёт
 * «Замерить» — приложение вычисляет Bv без калибровки (C=0) по текущему кадру, а разница между
 * введённым эталонным Ev и этим Bv и есть калибровочная константа C.
 */
@Composable
fun CalibrationScreen(
    onCalibrated: () -> Unit,
    viewModel: CalibrationViewModel = hiltViewModel(),
) {
    CameraPermissionGate {
        val controller = rememberCameraController()
        val frame by controller.frame.collectAsStateWithLifecycle()

        var measuredBv by remember { mutableStateOf<Float?>(null) }
        var referenceInput by remember { mutableStateOf("") }
        val referenceEv = referenceInput.toFloatOrNull()

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CameraPreview(controller, modifier = Modifier.fillMaxSize())
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Наведите камеру на сцену с известным Ev (серая карта, эталонный " +
                            "экспонометр или правило «Солнце-16») и нажмите «Замерить».",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Button(
                        onClick = { frame?.let { measuredBv = viewModel.measureUncalibratedBv(it) } },
                        enabled = frame != null,
                        modifier = Modifier.padding(top = 12.dp),
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

                    OutlinedTextField(
                        value = referenceInput,
                        onValueChange = { referenceInput = it },
                        label = { Text("Известный Ev этой сцены") },
                        enabled = measuredBv != null,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )

                    Button(
                        onClick = {
                            val bv = measuredBv
                            val ref = referenceEv
                            if (bv != null && ref != null) {
                                viewModel.saveCalibration(ref - bv, onCalibrated)
                            }
                        },
                        enabled = measuredBv != null && referenceEv != null,
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Text("Сохранить и продолжить")
                    }
                }
            }
        }
    }
}
