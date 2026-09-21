package com.filmexposure.ui.main

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.ui.camera.CameraPermissionGate
import com.filmexposure.ui.camera.CameraPreview
import com.filmexposure.ui.camera.rememberCameraController
import com.filmexposure.ui.main.components.BwToggle
import com.filmexposure.ui.main.components.CrosshairOverlay
import com.filmexposure.ui.main.components.DistanceScale
import com.filmexposure.ui.main.components.ExposurePairsRow
import com.filmexposure.ui.main.components.MeterMarkersOverlay
import com.filmexposure.ui.main.components.PauseButton
import com.filmexposure.ui.main.components.PointsCounter
import com.filmexposure.ui.main.components.RuleOfThirdsOverlay
import com.filmexposure.ui.main.components.StopsScale

/**
 * Главный экран-экспонометр (§6.1). Использует rigContext из MainViewModel — реальный риг+плёнку,
 * выбранные в §6.2 "Мои риги"/"Плёнки", либо MockRig-заглушку, если ничего не выбрано.
 */
@Composable
fun MainScreen(onOpenMenu: () -> Unit, viewModel: MainViewModel = hiltViewModel()) {
    CameraPermissionGate {
        val controller = rememberCameraController()
        val settings by viewModel.settings.collectAsStateWithLifecycle()
        val rigContext by viewModel.rigContext.collectAsStateWithLifecycle()
        val meterPoints by viewModel.meterPoints.collectAsStateWithLifecycle()
        val postingResult by viewModel.postingResult.collectAsStateWithLifecycle()
        val evShift by viewModel.evShift.collectAsStateWithLifecycle()
        val validPairs by viewModel.validPairs.collectAsStateWithLifecycle()
        val selectedPairIndex by viewModel.selectedPairIndex.collectAsStateWithLifecycle()
        val focusDistanceM by viewModel.focusDistanceM.collectAsStateWithLifecycle()
        val dof by viewModel.dof.collectAsStateWithLifecycle()

        var isFrozen by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Верхняя панель: ЧБ слева, Меню справа (§6.1). Зеркалится по pauseButtonSide.
                TopBar(
                    isBW = settings.isBW,
                    onToggleBW = viewModel::toggleBW,
                    onOpenMenu = onOpenMenu,
                    mirrored = settings.pauseButtonSide == ButtonSide.LEFT,
                )

                if (rigContext.isMock) {
                    androidx.compose.material3.Text(
                        text = "Тестовые данные — выберите риг и плёнку в меню",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }

                val exposureColumn: @Composable () -> Unit = {
                    Column(modifier = Modifier.fillMaxHeight().weight(1f)) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            CameraPreview(controller, modifier = Modifier.fillMaxSize())

                            if (settings.showRuleOfThirds) {
                                RuleOfThirdsOverlay(
                                    showIntersections = settings.showIntersections,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            CrosshairOverlay(modifier = Modifier.fillMaxSize())
                            MeterMarkersOverlay(meterPoints, modifier = Modifier.fillMaxSize())

                            PointsCounter(
                                count = meterPoints.size,
                                onClear = viewModel::clearPoints,
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                            )

                            // Тап-замер (§8.1): координаты тапа идут в маркер как есть; яркость
                            // берётся усреднённой по всему кадру (см. LuminanceAnalyzer) — точный
                            // замер 5×5 px по координате тапа отложен, см. комментарий там.
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(controller) {
                                        detectTapGestures { offset ->
                                            controller.frame.value?.let { frame ->
                                                val ev = viewModel.meterPointEv(frame)
                                                viewModel.onTap(offset.x, offset.y, ev)
                                            }
                                        }
                                    },
                            )
                        }

                        StopsScale(
                            scaleMode = settings.scaleMode,
                            postingResult = postingResult,
                            evShift = evShift,
                            filmLatitudePlus = rigContext.filmLatitudePlus,
                            onShiftChange = viewModel::setEvShift,
                            onToggleMode = viewModel::toggleScaleMode,
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                        )

                        ExposurePairsRow(
                            pairs = validPairs,
                            selectedIndex = selectedPairIndex,
                            onSelect = viewModel::selectPairIndex,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )
                    }
                }

                val distanceColumn: @Composable () -> Unit = {
                    Column(
                        modifier = Modifier.width(72.dp).fillMaxHeight().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        DistanceScale(
                            distanceM = focusDistanceM,
                            onDistanceChange = viewModel::setFocusDistanceM,
                            dof = dof,
                            unit = settings.distanceUnit,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                        PauseButton(
                            isFrozen = isFrozen,
                            onToggle = {
                                isFrozen = !isFrozen
                                if (isFrozen) controller.freeze() else controller.unfreeze()
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (settings.pauseButtonSide == ButtonSide.LEFT) {
                        distanceColumn()
                        exposureColumn()
                    } else {
                        exposureColumn()
                        distanceColumn()
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(isBW: Boolean, onToggleBW: (Boolean) -> Unit, onOpenMenu: () -> Unit, mirrored: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val bwToggle: @Composable () -> Unit = { BwToggle(isBW, onToggleBW) }
        // Временно: иконка "Меню" открывает калибровку (§6.2 полноценное меню ещё не построено).
        val menuButton: @Composable () -> Unit = {
            IconButton(onClick = onOpenMenu) {
                Icon(Icons.Filled.Menu, contentDescription = "Меню")
            }
        }
        if (mirrored) {
            menuButton()
            bwToggle()
        } else {
            bwToggle()
            menuButton()
        }
    }
}
