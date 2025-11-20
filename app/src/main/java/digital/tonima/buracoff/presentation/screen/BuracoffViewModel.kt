package digital.tonima.buracoff.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.tonima.buracoff.data.repository.SensorDataRepository
import digital.tonima.buracoff.domain.model.DetectorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class BuracoffViewModel @Inject constructor(
    private val repository: SensorDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectorUiState())
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()

    private val IMPACT_THRESHOLD = 6.0
    private var lastDetectionTime: Long = 0

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isMonitoring = true)

            repository.sensorReadings.collect { data ->
                processSensorData(data.x, data.y, data.z)
            }
        }
    }

    private fun processSensorData(x: Float, y: Float, z: Float) {
        val gForce = sqrt((x * x + y * y + z * z).toDouble())

        _uiState.value = _uiState.value.copy(currentGForce = gForce)

        if (gForce > IMPACT_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastDetectionTime > 500) {
                lastDetectionTime = now
                registerPothole(gForce)
            }
        }
    }

    private fun registerPothole(force: Double) {
        val currentCount = _uiState.value.potholeCount
        _uiState.value = _uiState.value.copy(
            potholeCount = currentCount + 1,
            lastImpactForce = force
        )
    }
}
