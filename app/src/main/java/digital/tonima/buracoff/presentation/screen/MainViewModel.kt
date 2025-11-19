package digital.tonima.buracoff.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        viewModelScope.launch {
            detector.potholeEvents.collect { magnitude ->
                _lastImpact.value = magnitude
                _potholeCount.value += 1
            }
        }
    }
}

