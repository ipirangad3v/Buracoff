package digital.tonima.buracoff.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.tonima.buracoff.domain.model.SensorDataPoint
import digital.tonima.buracoff.domain.usecase.PotholeDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val detector: PotholeDetector
) : ViewModel() {

    private val _lastImpact = MutableStateFlow(0.0)
    val lastImpact = _lastImpact.asStateFlow()

    private val _potholeCount = MutableStateFlow(0)
    val potholeCount = _potholeCount.asStateFlow()

    private val _sensorHistory = MutableStateFlow<List<SensorDataPoint>>(emptyList())
    val sensorHistory = _sensorHistory.asStateFlow()

    private val MAX_HISTORY_SIZE = 100

    init {
        viewModelScope.launch {
            detector.potholeEvents.collect { magnitude ->
                _lastImpact.value = magnitude
                _potholeCount.value += 1
            }
        }

        viewModelScope.launch {
            detector.sensorData.collect { dataPoint ->
                _sensorHistory.value = (_sensorHistory.value + dataPoint)
                    .takeLast(MAX_HISTORY_SIZE)
            }
        }
    }
}

