package com.filmexposure.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors

/**
 * Биндинг CameraX (Preview + ImageAnalysis) к жизненному циклу + чтение реальных параметров
 * экспозиции телефона через Camera2Interop (t0/N0/ISO0 — §4.2). См. предупреждение в
 * [LuminanceFrame] про упрощённую синхронизацию luminance и exposure-метаданных.
 *
 * Класс НЕ Hilt-компонент — создаётся и живёт в рамках Compose через [rememberCameraController],
 * так как ему нужен Context конкретного экрана и PreviewView, а не singleton-граф.
 */
class CameraController(private val context: Context) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    @Volatile private var latestLuminance: Float = 0f
    @Volatile private var latestExposureTimeNanos: Long = 0L
    @Volatile private var latestIso: Int = 0
    @Volatile private var latestAperture: Float = 0f

    val previewView: PreviewView by lazy { PreviewView(context) }

    private val _frame = MutableStateFlow<LuminanceFrame?>(null)
    val frame: StateFlow<LuminanceFrame?> = _frame

    private val _frozenBitmap = MutableStateFlow<Bitmap?>(null)
    val frozenBitmap: StateFlow<Bitmap?> = _frozenBitmap

    @OptIn(ExperimentalCamera2Interop::class)
    fun bind(lifecycleOwner: LifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysisBuilder = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

            Camera2Interop.Extender(analysisBuilder).setSessionCaptureCallback(
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult,
                    ) {
                        result.get(CaptureResult.SENSOR_EXPOSURE_TIME)?.let { latestExposureTimeNanos = it }
                        result.get(CaptureResult.SENSOR_SENSITIVITY)?.let { latestIso = it }
                        result.get(CaptureResult.LENS_APERTURE)?.let { latestAperture = it }
                        pushFrame()
                    }
                },
            )

            val analysis = analysisBuilder.build().also {
                it.setAnalyzer(
                    cameraExecutor,
                    LuminanceAnalyzer { luminance ->
                        latestLuminance = luminance
                        pushFrame()
                    },
                )
            }

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(context))
    }

    fun unbind() {
        runCatching { ProcessCameraProvider.getInstance(context).get().unbindAll() }
        cameraExecutor.shutdown()
    }

    /** Заморозка превью (§7.1, §7.6): берём текущий кадр PreviewView как статичный Bitmap. */
    fun freeze() {
        _frozenBitmap.value = previewView.bitmap
    }

    fun unfreeze() {
        _frozenBitmap.value = null
    }

    private fun pushFrame() {
        val t0 = latestExposureTimeNanos
        val iso0 = latestIso
        val n0 = latestAperture
        if (t0 <= 0L || iso0 <= 0 || n0 <= 0f) return // ждём первый полный набор метаданных
        _frame.value = LuminanceFrame(
            averageLuminance = latestLuminance,
            exposureTimeSeconds = t0 / 1_000_000_000f,
            aperture = n0,
            isoSensitivity = iso0,
        )
    }
}
